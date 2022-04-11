//Java API
import java.awt.*;

//AGI Java API
import agi.core.*;
import agi.core.awt.*;
import agi.stkutil.*;
import agi.stkvgt.*;
import agi.stkobjects.*;

public class SampleCode
{
	private AgStkObjectRootClass	m_AgStkObjectRootClass;
	private AgScenarioClass			m_AgScenarioClass;
	private IAgStkObjectCollection	m_ScenarioChildren;
	private String                  m_StartTime;
	private String					m_StopTime;

	/* package */SampleCode(AgStkObjectRootClass root)
	throws AgCoreException
	{
		this.m_AgStkObjectRootClass = root;
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
			//String tarPath = tar.getPath();
			//System.out.println(tarPath);
//			IAgStkAccess senAccess = (IAgStkAccess) sen.getAccessToObject(tar);
//			senAccess.computeAccess();
//			IAgIntervalCollection computedAccessIntervalTimes = senAccess.getComputedAccessIntervalTimes();
//			int count = computedAccessIntervalTimes.getCount();
//			System.out.println("lzy:"+count);
//			System.out.println(computedAccessIntervalTimes);
//			Object o = computedAccessIntervalTimes.toArray_AsObject(1, 1);
//			System.out.println(o);

//			System.out.println();
//			Object dataPrvElements[] = new Object[]{"Time","FromAngularRate","FromRange"};
//			IAgDataPrvTimeVar dp = (IAgDataPrvTimeVar) senAccess.getDataProviders();


//			IAgStkAccess access = sam.getAccessToObject(tar);
//			IAgDataProviderCollection dpc = access.getDataProviders();

			//IAgDataPrvInterval dpInfo = (IAgDataPrvInterval) senAccess.getDataProviders();

			//IAgStkAccess senAccess = sen.getAccess(tarPath);
			//senAccess.computeAccess();int dpCount = dpc.getCount();
			////			System.out.println("dpCount=" + dpCount);
//			//IAgDataProviderCollection dpc = senAccess.getDataProviders();

//

//			for(int i = 0; i < dpCount; i++)
//			{
//				IAgDataProviderInfo dpi = dpc.getItem(new Integer(i));
//				String name = dpi.getName();
//				AgEDataProviderType type = dpi.getType_AsObject();
//				boolean isGroup = dpi.isGroup();
//				System.out.println("DataProviderInfo[ name=" + name + ", type=" + type + ", typeInfo=" + this.getHumanReadeableDpType(type) + ", isGroup=" + isGroup + " ]");
//
//				if(name.equalsIgnoreCase("AER Data"))
//				{
//					if(isGroup)
//					{
//						IAgDataProviderGroup group = new AgDataProviderGroupClass(dpi);
//						IAgDataProviders prvs = group.getGroup();
//
//						int llaDpcCount = prvs.getCount();
//						System.out.println("llaDpcCount=" + llaDpcCount);
//
//						IAgDataProviderInfo dpilla = prvs.getItem("Time Variable");
//						String llaname = dpilla.getName();
//						AgEDataProviderType llatype = dpilla.getType_AsObject();
//						boolean llaisGroup = dpilla.isGroup();
//						System.out.println("DataProviderInfo for LLA State[ name=" + llaname + ", type=" + llatype + ", typeInfo=" + this.getHumanReadeableDpType(llatype) + ", isGroup=" + llaisGroup
//								+ " ]");
//
//						String startTime = (String)this.m_AgScenarioClass.getStartTime_AsObject();
//						String stopTime = (String)this.m_AgScenarioClass.getStopTime_AsObject();
//						double timeStep = 10;
//
//						IAgDataPrvTimeVar var = new AgDataPrvTimeVarClass(dpilla);
//						IAgDrResult result = var.exec(startTime, stopTime, timeStep);
//
//						IAgDrDataSetCollection dsc = result.getDataSets();
//						int dscCount = dsc.getCount();
//						System.out.println("dscCount=" + dscCount);
//
//						String elementNames[] = new String[dscCount];
//						int elementTypes[] = new int[dscCount];
//						int elementUnits[] = new int[dscCount];
//						int valueCounts[] = new int[dscCount];
//						Object values[] = new Object[dscCount];
//						Object units[] = new Object[dscCount];
//
//						for(int dscIndex = 0; dscIndex < dscCount; dscIndex++)
//						{
//							IAgDrDataSet dataset = dsc.getItem(dscIndex);
//
//							elementNames[dscIndex] = dataset.getElementName();
//							elementTypes[dscIndex] = dataset.getElementType();
//							elementUnits[dscIndex] = dataset.getUnitType();
//							valueCounts[dscIndex] = dataset.getCount();
//							units[dscIndex] = dataset.getInternalUnitValues().getJavaObjectArray();
//							values[dscIndex] = dataset.getValues().getJavaObjectArray();
//						}
//
//						for(int dscIndex = 0; dscIndex < dscCount; dscIndex++)
//						{
//							StringBuffer sb = new StringBuffer();
//
//							sb.append(elementNames[dscIndex]);
//							sb.append("[ ");
//							sb.append(getHumanReadeableElemType(AgEDataPrvElementType.getFromValue(elementTypes[dscIndex])));
//							sb.append(", ");
//							sb.append(elementUnits[dscIndex]);
//							sb.append(", ");
//							sb.append(valueCounts[dscIndex]);
//							sb.append(" ]:");
//
//							Object[] valuesArray = (Object[])values[dscIndex];
//
//							for(int dsIndex = 0; dsIndex < valueCounts[dscIndex]; dsIndex++)
//							{
//								sb.append("  ");
//								sb.append(valuesArray[dsIndex]);
//							}
//
//							System.out.println(sb.toString());
//						}
//
//						IAgDrIntervalCollection ic = result.getIntervals();
//						int icCount = ic.getCount();
//						System.out.println("icCount=" + icCount);
//
//						IAgDrInterval interval = ic.getItem(0);
//						Object strStartTime = interval.getStartTime_AsObject();
//						Object strStopTime = interval.getStopTime_AsObject();
//
//						System.out.println("\tstartTime=" + strStartTime + ", stopTime=" + strStopTime);
//					}
//				}
//			}
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

			IAgStkObject sam = this.m_ScenarioChildren.getItem("SAM");
			IAgStkObject sen = sam.getChildren().getItem("SEN");
			IAgStkObject tar = this.m_ScenarioChildren.getItem("TAR");

			System.out.println("==============================");
			System.out.println(" Got SEN");
			System.out.println("==============================");

			IAgDataProviderCollection dpc = sen.getDataProviders();

			int dpCount = dpc.getCount();
			System.out.println("dpCount=" + dpCount);

			for(int i = 0; i < dpCount; i++)
			{
				IAgDataProviderInfo dpi = dpc.getItem(new Integer(i));
				String name = dpi.getName();
				AgEDataProviderType type = dpi.getType_AsObject();
				boolean isGroup = dpi.isGroup();
				System.out.println("DataProviderInfo[ name=" + name + ", type=" + type + ", typeInfo=" + this.getHumanReadeableDpType(type) + ", isGroup=" + isGroup + " ]");

				if(name.equalsIgnoreCase("LLA State"))
				{
					if(!isGroup)
					{
						IAgDataProviderGroup group = new AgDataProviderGroupClass(dpi);
						IAgDataProviders prvs = group.getGroup();

						int llaDpcCount = prvs.getCount();
						System.out.println("llaDpcCount=" + llaDpcCount);

						IAgDataProviderInfo dpilla = prvs.getItem("Fixed");
						String llaname = dpilla.getName();
						AgEDataProviderType llatype = dpilla.getType_AsObject();
						boolean llaisGroup = dpilla.isGroup();
						System.out.println("DataProviderInfo for LLA State[ name=" + llaname + ", type=" + llatype + ", typeInfo=" + this.getHumanReadeableDpType(llatype) + ", isGroup=" + llaisGroup
						+ " ]");

						String startTime = (String)this.m_AgScenarioClass.getStartTime_AsObject();
						String stopTime = (String)this.m_AgScenarioClass.getStopTime_AsObject();
						double timeStep = 10;

						IAgDataPrvTimeVar var = new AgDataPrvTimeVarClass(dpilla);
						IAgDrResult result = var.exec(startTime, stopTime, timeStep);

						IAgDrDataSetCollection dsc = result.getDataSets();
						int dscCount = dsc.getCount();
						System.out.println("dscCount=" + dscCount);

						String elementNames[] = new String[dscCount];
						int elementTypes[] = new int[dscCount];
						int elementUnits[] = new int[dscCount];
						int valueCounts[] = new int[dscCount];
						Object values[] = new Object[dscCount];
						Object units[] = new Object[dscCount];

						for(int dscIndex = 0; dscIndex < dscCount; dscIndex++)
						{
							IAgDrDataSet dataset = dsc.getItem(dscIndex);

							elementNames[dscIndex] = dataset.getElementName();
							elementTypes[dscIndex] = dataset.getElementType();
							elementUnits[dscIndex] = dataset.getUnitType();
							valueCounts[dscIndex] = dataset.getCount();
							units[dscIndex] = dataset.getInternalUnitValues().getJavaObjectArray();
							values[dscIndex] = dataset.getValues().getJavaObjectArray();
						}

						for(int dscIndex = 0; dscIndex < dscCount; dscIndex++)
						{
							StringBuffer sb = new StringBuffer();

							sb.append(elementNames[dscIndex]);
							sb.append("[ ");
							sb.append(getHumanReadeableElemType(AgEDataPrvElementType.getFromValue(elementTypes[dscIndex])));
							sb.append(", ");
							sb.append(elementUnits[dscIndex]);
							sb.append(", ");
							sb.append(valueCounts[dscIndex]);
							sb.append(" ]:");

							Object[] valuesArray = (Object[])values[dscIndex];

							for(int dsIndex = 0; dsIndex < valueCounts[dscIndex]; dsIndex++)
							{
								sb.append("  ");
								sb.append(valuesArray[dsIndex]);
							}

							System.out.println(sb.toString());
						}

						IAgDrIntervalCollection ic = result.getIntervals();
						int icCount = ic.getCount();
						System.out.println("icCount=" + icCount);

						IAgDrInterval interval = ic.getItem(0);
						Object strStartTime = interval.getStartTime_AsObject();
						Object strStopTime = interval.getStopTime_AsObject();

						System.out.println("\tstartTime=" + strStartTime + ", stopTime=" + strStopTime);
					}
				}
			}
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