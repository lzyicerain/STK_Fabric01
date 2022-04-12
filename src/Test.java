import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Test {

    private HFClient m_hfClient;
    private User m_user;
    private Channel m_channel;
    private ChaincodeID m_chaincodeID;

    //设置加密方式
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Test()throws IOException, EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException{
        try {
            System.out.println("aaa");
            //创建客户端代理
            HFClient hfClient = HFClient.createNewInstance();
            m_hfClient = hfClient;
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
            hfClient.setCryptoSuite(cryptoSuite);

            //设置用户
            User user = getFabricUser4Local("user88","Org1MSP","Org1MSP");
            m_user = user;
            //User user = getFabricUser4FabricCA("admin","Org1MSP","Org1MSP");

            hfClient.setUserContext(user);

            //创建通道的客户端代理
            Channel channel = hfClient.newChannel("fabricchannel");
            m_channel = channel;


            //创建orderer服务客户端代理并加到通道中
            Orderer orderer = hfClient.newOrderer("OrdererOrg","grpc://192.168.43.251:7050");
            channel.addOrderer(orderer);

            //创建Peer服务器节点的客户端代理并加入到通道中
            Peer peer = hfClient.newPeer("peer0","grpc://192.168.43.251:7051");
            channel.addPeer(peer);
            //初始化通道
            channel.initialize();
            //获取区块链相关信息
            BlockchainInfo blockchainInfo = channel.queryBlockchainInfo(peer);
            System.out.println(blockchainInfo.getHeight() + " ****!!!");

            //获取当前Peer加入了哪些Channel
            Set<String> peerchannels = hfClient.queryChannels(peer);
            for (String string : peerchannels){
                System.out.println("Have come to the channel: "+string);
            }

            //获取当前Peer服务器中状态为install的Chaincode的信息
            List<Query.ChaincodeInfo> installchaincodes = hfClient.queryInstalledChaincodes(peer);
            for (Query.ChaincodeInfo chaincodeInfo : installchaincodes){
                System.out.println(chaincodeInfo.getPath());
            }

            //获取当前Peer加入某个Channel中状态为Instantiate的Chaincode的详细信息
            List<Query.ChaincodeInfo> instancechaincodes = channel.queryInstantiatedChaincodes(peer);
            for (Query.ChaincodeInfo chaincodeInfo : installchaincodes){
                System.out.println(chaincodeInfo.getName());
            }

            //调用Chaincode中invoke的getlastvalue方法
            ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("lwz").build();
            m_chaincodeID = chaincodeID;
//            sendTranstion(hfClient,channel,chaincodeID,user).thenApply(transactionEvent -> {
//                String tranid = transactionEvent.getTransactionID();
//                System.out.println("===  " + tranid);
//                return null;
//            }).exceptionally(e -> {
//                return null;
//            });

        }catch (RuntimeException | IllegalAccessException | InstantiationException | ClassNotFoundException | CryptoException | InvalidArgumentException | NoSuchMethodException | InvocationTargetException | TransactionException | ProposalException e){
            e.printStackTrace();
        }

    }

    public CompletableFuture<BlockEvent.TransactionEvent> sendTranstion(String st) throws InvalidArgumentException, ProposalException {
        //ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("sampledemo5_9").build();
        Collection<ProposalResponse> successful = new LinkedList<>();
        TransactionProposalRequest transactionProposalRequest = m_hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(m_chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[] {"putvalue","starttimee",st});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(m_user);

        Collection<ProposalResponse> invokeProResp = m_channel.sendTransactionProposal(transactionProposalRequest);
        for (ProposalResponse response : invokeProResp){
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS){
                System.out.println("Successful transaction proposal response Txid: " + response.getTransactionID() + " from peer %s" + response.getPeer().getName());
                String result = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                System.out.println(result);
                successful.add(response);
            } else {
                System.out.println(invokeProResp);
                System.out.println(response.getTransactionID());
                System.out.println("loose");
            }
        }
        return m_channel.sendTransaction(successful,m_user);
    }

    public CompletableFuture<BlockEvent.TransactionEvent> sendTranstionGet() throws InvalidArgumentException, ProposalException {
        //ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName("sampledemo5_9").build();
        Collection<ProposalResponse> successful = new LinkedList<>();
        TransactionProposalRequest transactionProposalRequest = m_hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(m_chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[] {"getlastvalue","starttimee","  "});
        transactionProposalRequest.setProposalWaitTime(300000);
        transactionProposalRequest.setUserContext(m_user);

        Collection<ProposalResponse> invokeProResp = m_channel.sendTransactionProposal(transactionProposalRequest);
        for (ProposalResponse response : invokeProResp){
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS){
                System.out.println("Successful transaction proposal response Txid: " + response.getTransactionID() + " from peer %s" + response.getPeer().getName());
                String result = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                System.out.println(result);
                successful.add(response);
            } else {
                System.out.println(invokeProResp);
                System.out.println(response.getTransactionID());
                System.out.println("loose");
            }
        }
        return m_channel.sendTransaction(successful,m_user);
    }

    //根据cryptogen生成的账号创建Fabric账号
    private static User getFabricUser4Local (String username, String org, String orgID) throws IOException {
        FabricUsersImpl user = new FabricUsersImpl(username,org);
        user.setMspId(orgID);
        String certificate = new String(IOUtils.toByteArray(new FileInputStream(new File("G:/fabricconfig/crypto-config/peerOrganizations/org1.lzy.com/users/Admin@org1.lzy.com/msp/signcerts/Admin@org1.lzy.com-cert.pem"))),"UTF-8");
        File privatekeyfile = new File("G:/fabricconfig/crypto-config/peerOrganizations/org1.lzy.com/users/Admin@org1.lzy.com/msp/keystore/1521a7455ce026c13a7aca9e9733e6c5ea31afab28cb72cf97a475d819063d23_sk");
        PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privatekeyfile)));
        EnrollMentImpl enrollMent = new EnrollMentImpl(privateKey,certificate);
        user.setEnrollment(enrollMent);
        return user;
    }

    //配置文件中获取私钥
    private static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {

        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        PEMParser pemParser = new PEMParser(pemReader);
        pemPair = (PrivateKeyInfo) pemParser.readObject();
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        return privateKey;
    }

    //enrollm实现类
    static final class EnrollMentImpl implements Enrollment, Serializable{

        private static final long serialVersionUID = -2784835212445309006L;
        private final PrivateKey privateKey;
        private final String certificate;

        public EnrollMentImpl(PrivateKey privateKey, String certificate) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }
        @Override
        public PrivateKey getKey() {
            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }
    }

    //Frbric user实现类
    static final class FabricUsersImpl implements User, Serializable{

        private String name;
        private Set<String> roles;
        private String account;
        private String affiliation;
        private String organization;
        private String enrollmentSecret;
        Enrollment enrollment = null;
        private String keyValStoreName;
        private String mspId;

        FabricUsersImpl(String name, String org){
            this.name = name;
            this.organization = org;
        }


        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Set<String> getRoles() {
            return this.roles;
        }

        @Override
        public String getAccount() {
            return this.account;
        }

        @Override
        public String getAffiliation() {
            return this.affiliation;
        }

        @Override
        public Enrollment getEnrollment() {
            return this.enrollment;
        }

        @Override
        public String getMspId() {
            return mspId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getEnrollmentSecret() {
            return enrollmentSecret;
        }

        public void setEnrollmentSecret(String enrollmentSecret) {
            this.enrollmentSecret = enrollmentSecret;
        }

        public void setEnrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
        }

        public String getKeyValStoreName() {
            return keyValStoreName;
        }

        public void setKeyValStoreName(String keyValStoreName) {
            this.keyValStoreName = keyValStoreName;
        }

        public void setMspId(String mspId) {
            this.mspId = mspId;
        }

        public boolean isEnrolled(){
            return this.enrollment != null;
        }
    }

}


