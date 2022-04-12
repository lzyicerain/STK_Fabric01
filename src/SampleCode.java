//Java API
import java.awt.*;
import java.util.Collection;

//AGI Java API
import agi.core.*;
import agi.core.awt.*;
import agi.stkutil.*;
import agi.stkvgt.*;
import agi.stkobjects.*;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.ProposalResponse;

public class SampleCode
{
	private AgStkObjectRootClass	m_AgStkObjectRootClass;
	private AgScenarioClass			m_AgScenarioClass;
	private IAgStkObjectCollection	m_ScenarioChildren;
	private String                  m_StartTime;
	private String					m_StopTime;
	private String                  st;
	private String					sp;
	private Test                    m_test;

	/* package */SampleCode(AgStkObjectRootClass root, Test test)
	throws AgCoreException
	{
		this.m_AgStkObjectRootClass = root;
		this.m_test = test;
	}

	/* package */void setUnits()
	throws Throwable
	{
		// ===================================================================================
		// Set measurement unit preferences
		// NOTE: It is best to always set the Unit preferences on the AgStkObjectRoot object.
		// ===================================================================================
		// If this throws a throwable it will be passed back through the initializeSTKEngine method
		// and back through the constructor to the main method to stop the application.
		// This is desired, because if we were to continue with the application,
		// then we would surely get more errors when creating the objects, with
		// specific numbers for specific units of measurement
		// ===================================================================================
		this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("LongitudeUnit", "deg");
		this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("LatitudeUnit", "deg");
		this.m_AgStkObjectRootClass.getUnitPreferences().setCurrentUnit("DateFormat", "UTCG");
	}

	/* package */void sateliteDataDisplay() throws Throwable
	{
		try
		{
			//开始更新场景
			this.m_AgStkObjectRootClass.beginUpdate();

			this.m_AgStkObjectRootClass.loadScenario("G:\\Java\\STK_Fabric01\\Test3\\Test3.sc");

			//获取加载的场景、场景子集
			this.m_AgScenarioClass = (AgScenarioClass) this.m_AgStkObjectRootClass.getCurrentScenario();
			this.m_ScenarioChildren = this.m_AgScenarioClass.getChildren();

			System.out.println("==============================");
			System.out.println(" Got SCENARIO");
			System.out.println("==============================");

			//描述场景
			this.m_AgScenarioClass.setShortDescription("Test secnario created via AGI Java wrapper for Object Model");
			this.m_AgScenarioClass.setLongDescription("See Short Description");

			//获取场景的开始时间和结束时间
			this.m_StartTime = (String) this.m_AgScenarioClass.getStartTime_AsObject();
			this.m_StopTime = (String) this.m_AgScenarioClass.getStopTime_AsObject();

			System.out.println(m_ScenarioChildren);

			IAgStkObject sam = this.m_ScenarioChildren.getItem("Satellite1");
			IAgSatellite sat = new AgSatellite(sam);

			System.out.println("==============================");
			System.out.println(" Got Satellite/SAM ");
			System.out.println("==============================");

			IAgSaVO vo = sat.getVO();
			IAgVODataDisplayCollection ddc = vo.getDataDisplay();

			int ddcount = ddc.getCount();
			System.out.println("ddcount=" + ddcount);

			for(int i = 0; i < ddcount; i++)
			{
				IAgVODataDisplayElement dde = ddc.getItem(i);

				if((dde.getName()).equals("LLA Position"))
				{
					dde.setIsVisible(true);
				}
				// You could display other data, such as Velocity Heading
				else if((dde.getName()).equals("Velocity Heading"))
				{
					dde.setIsVisible(false);
				}
			}

//			Object[] fonts = this.m_AgScenarioClass.getVO().getLargeFont().getAvailableFonts().getJavaObjectArray();
//			System.out.println("Available Fonts Include:");
//			for(int colIndex = 0; colIndex < fonts.length; colIndex++)
//			{
//				System.out.println("\t[" + colIndex + "] = " + fonts[colIndex]);
//			}
		}
		finally {
			this.m_AgStkObjectRootClass.endUpdate();
			this.m_AgStkObjectRootClass.rewind();
		}
	}

	/* package */void access()
	throws Throwable
	{
		try
		{
			//开始更新场景
			this.m_AgStkObjectRootClass.beginUpdate();

			//根据卫星来获取传感器
			IAgStkObject sam = this.m_ScenarioChildren.getItem("Satellite1");
			IAgStkObject sen = sam.getChildren().getItem("Sensor1");
			IAgStkObject pla = this.m_ScenarioChildren.getItem("Place1");



			System.out.println("==============================");
			System.out.println(" Got SEN AND PLACE");
			System.out.println("==============================");


			IAgStkAccess accessToObject = sen.getAccessToObject(pla);
			accessToObject.computeAccess();
			IAgDataProviderCollection dataProviders = accessToObject.getDataProviders();
			IAgDataPrvInterval access_data = dataProviders.getDataPrvIntervalFromPath("Access Data");

			IAgDrResult result1 = access_data.exec("10 Apr 2022 04:00:00.000", "10 Apr 2022 14:00:00.000");

			System.out.println(result1);
			IAgDrDataSetCollection dataSets = result1.getDataSets();

			Object[] accessNum = (Object[])dataSets.getItem(0).getValues_AsObject();
			Object[] startTime = (Object[])dataSets.getItem(1).getValues_AsObject();
			Object[] stopTime = (Object[])dataSets.getItem(2).getValues_AsObject();
			Object[] duration = (Object[])dataSets.getItem(3).getValues_AsObject();

			st = startTime[0].toString();
			System.out.println(st);
			sp = stopTime[0].toString();
			System.out.println(sp);

			System.out.println("=======Num=======");
			System.out.println(access_data);
			System.out.println("=================");

			System.out.println("=======startTime=======");
			System.out.println(startTime);
			System.out.println("=================");

			System.out.println("=======stopTime=======");
			System.out.println(stopTime);
			System.out.println("=================");

			System.out.println("=======Duration=======");
			System.out.println(duration);
			System.out.println("=================");

		}
		finally
		{
			this.m_AgStkObjectRootClass.endUpdate();
			this.m_AgStkObjectRootClass.rewind();
		}
	}

	/* package */void dataTransmissionAndFeedback()
	throws Throwable
	{
		try
		{
			//开始更新场景
			this.m_AgStkObjectRootClass.beginUpdate();

			IAgStkObject sam = this.m_ScenarioChildren.getItem("Satellite1");
			IAgSensor sen = (IAgSensor) sam.getChildren().getItem("Sensor1");

			System.out.println("==============================");
			System.out.println(" Got SEN");
			System.out.println("==============================");

			m_test.sendTranstion(st).thenApply(transactionEvent -> {
				String tranid = transactionEvent.getTransactionID();
				System.out.println("===  " + tranid);
				sen.setPatternType(AgESnPattern.E_SN_SIMPLE_CONIC);
				IAgSnPattern pattern = sen.getPattern();
				AgSnSimpleConicPattern agSnSimpleConicPattern = new AgSnSimpleConicPattern(pattern);
				agSnSimpleConicPattern.setConeAngle(new Double(60.0));
				return null;
			}).exceptionally(e -> {
				return null;
			});

			Thread.sleep(1000*5);

			m_test.sendTranstionGet().thenApply(transactionEvent -> {
				String tranid = transactionEvent.getTransactionID();
				System.out.println("===  " + tranid);
				return null;
			}).exceptionally(e -> {
				return null;
			});


		}
		finally
		{
			this.m_AgStkObjectRootClass.endUpdate();
			this.m_AgStkObjectRootClass.rewind();
		}
	}

	public void reset()
	throws Throwable
	{
		try
		{
			this.m_AgStkObjectRootClass.closeScenario();

		}
		finally
		{
		}
	}


	private String getHumanReadeableElemType(AgEDataPrvElementType elemType)
	{
		String humanElemType = null;

		switch(elemType)
		{
			case E_REAL:
			{
				humanElemType = "Real";
				break;
			}
			case E_INT:
			{
				humanElemType = "Int";
				break;
			}
			case E_CHAR:
			{
				humanElemType = "Char";
				break;
			}
			case E_CHAR_OR_REAL:
			{
				humanElemType = "CharOrReal";
				break;
			}
			default:
			{
				humanElemType = "unreadable";
				break;
			}
		}

		return humanElemType;
	}

	private String getHumanReadeableDpType(AgEDataProviderType dpType)
	{
		String humanDpType = null;

		switch(dpType)
		{
			case E_DR_DUP_TIME:
			{
				humanDpType = "Duplicate Time";
				break;
			}
			case E_DR_DYN_IGNORE:
			{
				humanDpType = "Dynamic Ignore";
				break;
			}
			case E_DR_FIXED:
			{
				humanDpType = "Fixed";
				break;
			}
			case E_DR_INTVL:
			{
				humanDpType = "Interval";
				break;
			}
			case E_DR_INTVL_DEFINED:
			{
				humanDpType = "Interval Defined";
				break;
			}
			case E_DR_STAND_ALONE:
			{
				humanDpType = "StandAlone";
				break;
			}
			case E_DR_TIME_VAR:
			{
				humanDpType = "Time Variable";
				break;
			}
			default:
			{
				humanDpType = "unreadable";
				break;
			}
		}

		return humanDpType;
	}
}