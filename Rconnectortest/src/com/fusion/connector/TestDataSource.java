package com.fusion.connector;

// java core package
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//parse json
import org.json.JSONArray;
import org.json.JSONObject;

//R engine library
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

//com.fusion is just a label
import com.fusion.connector.TestMetaData;

//YellowFin packages
import com.hof.mi.thirdparty.interfaces.AbstractDataSet;
import com.hof.mi.thirdparty.interfaces.AbstractDataSource;
import com.hof.mi.thirdparty.interfaces.AggregationType;
import com.hof.mi.thirdparty.interfaces.ColumnMetaData;
import com.hof.mi.thirdparty.interfaces.DataType;
import com.hof.mi.thirdparty.interfaces.FilterData;
import com.hof.mi.thirdparty.interfaces.FilterOperator;
import com.hof.mi.thirdparty.interfaces.FilterMetaData;
import com.hof.mi.thirdparty.interfaces.ScheduleDefinition;
import com.hof.mi.thirdparty.interfaces.ScheduleDefinition.FrequencyTypeCode;
import com.hof.pool.JDBCMetaData;
import com.hof.util.YFLogger;

public class TestDataSource extends AbstractDataSource {
	//Use Yellowfin's logger
	private static final YFLogger log = YFLogger.getLogger(TestDataSource.class.getName());
	public String getDataSourceName() {

		return "Test Data Source";

	}
	
	/* 
	 * Can this step be used in the "import from 3rd party" ETL step
	 * 
	 */
//	Can this code be used for transformation flow
	public boolean isTransformationCompatible()
	{
		return true;
	}


	public ArrayList<AbstractDataSet> getDataSets() {

		/* In this function define the list of datasets available for this connector. */

		ArrayList<AbstractDataSet> p = new ArrayList<AbstractDataSet>();

		/* Each dataset is an AbstractDataSet. */
		p.add(TestData());

		return p;

	}

	private AbstractDataSet TestData() {
		AbstractDataSet simpleDataSet = new AbstractDataSet() {
			
//            Define the parameter
			public List<FilterMetaData> getFilters() {
				/* In this function define he list of available filters */
				List<FilterMetaData> fm = new ArrayList<FilterMetaData>();

				fm.add(new FilterMetaData("model_parameter", DataType.TEXT, true,
						new FilterOperator[] { FilterOperator.EQUAL }));
				fm.add(new FilterMetaData("output_parameter", DataType.NUMERIC, true,
						new FilterOperator[] { FilterOperator.EQUAL }));
				return fm;

			}

			public String getDataSetName() {
				/* Here define the dataset name */
				return "TestData";

			}

			public boolean isFilterValueEnabled(String filter) {
				if (filter.equals("model_parameter"))
					return true;
				if (filter.equals("output_parameter"))
					return true;
				return false;
			}
			
			

			/**
			 * Receives the name of the filter you want the values for, and a list of
			 * pre-applied filters (ie. AccessFilters). Returns a list with values that may
			 * be used by the filter.
			 */
			public List<Object> getFilterValues(String filter, HashMap<String, FilterData> appliedFilters) {

//				if (filter.equals("collection")) {
//					List<Object> values = new ArrayList<Object>();
//					values.add("Health Care");
//					values.add("Real Estate");
//					values.add("Energy");
//					values.add("Utilities");
//					values.add("Industrials");
//					return values;
//				}

				return null;

			}

			public List<ColumnMetaData> getColumns() {
				
				String host = (String) getAttribute("HOST");
				String port = (String) getAttribute("PORT");
				String StagingScript = (String) getAttribute("STAGINGSCRIPT");
				String ExecutionScript = (String) getAttribute("EXECUTIONSCRIPT");
				System.out.println("hostname = "+host);

//				if (rResponseObject.inherits("try-error")) {
//					System.out.println(rResponseObject.asString());
//				}
				
				
				/* In this function define the list of columns available in the dataset */
				List<ColumnMetaData> cm = new ArrayList<ColumnMetaData>();
				AggregationType[] agg;
				agg = new AggregationType[6];
				agg[0] = AggregationType.COUNT;
				agg[1] = AggregationType.COUNTDISTINCT;
				agg[2] = AggregationType.MAX;
				agg[3] = AggregationType.MIN;
				agg[4] = AggregationType.AVG;
				agg[5] = AggregationType.SUM;
				
				/* Only field that have filter operators applied are able to be used as user filters at the report level */
				FilterOperator numeric_fo[] = { FilterOperator.EQUAL, FilterOperator.BETWEEN, FilterOperator.GREATER,
						FilterOperator.GREATEREQUAL, FilterOperator.INLIST, FilterOperator.ISNOTNULL,
						FilterOperator.ISNULL, FilterOperator.LESS, FilterOperator.LESSEQUAL, FilterOperator.NOTEQUAL,
						FilterOperator.NOTINLIST };

				FilterOperator text_fo[] = { FilterOperator.EQUAL, FilterOperator.INLIST, FilterOperator.ISNOTNULL,
						FilterOperator.ISNULL, FilterOperator.NOTEQUAL,
						FilterOperator.NOTINLIST };
				
				
				cm.add(new ColumnMetaData("Subcategory_Code", DataType.NUMERIC, null, agg, numeric_fo));
				cm.add(new ColumnMetaData("JourneyStep", DataType.TEXT, null, null, text_fo));
				cm.add(new ColumnMetaData("Attr", DataType.TEXT, null, null, text_fo));
				cm.add(new ColumnMetaData("year_flag", DataType.NUMERIC, null, agg, numeric_fo));
				cm.add(new ColumnMetaData("TotalSample", DataType.NUMERIC, null, agg, numeric_fo));
				cm.add(new ColumnMetaData("Slope", DataType.NUMERIC, null, agg, numeric_fo));
				
				
				
				//cm.add(new ColumnMetaData("low", DataType.NUMERIC, null, agg, numeric_fo));
//				cm.add(new ColumnMetaData("openTimeOne", DataType.TIMESTAMP));
//				cm.add(new ColumnMetaData("closeTime", DataType.TIMESTAMP));
	

				return cm;
			}

			public Object[][] execute(List<ColumnMetaData> columns, List<FilterData> filters) {

				//Get filter value
				String model_parameter = findFilter("model_parameter", filters).getFilterValue().toString();
				
				String output_parameter = findFilter("output_parameter", filters).getFilterValue().toString();


				
				String host = (String) getAttribute("HOST");
				int port = Integer.valueOf((String) getAttribute("PORT"));
				String StagingScript = (String) getAttribute("STAGINGSCRIPT");
				String ExecutionScript = (String) getAttribute("EXECUTIONSCRIPT");
				System.out.println("hostname = "+host);		
				double[] Subcategory_Code;
				String[] JourneyStep;
				String[] Attr;
				double[] year_flag;
				double[] TotalSample;
				double[] Slope;
				
				
				
				
				RConnection c;
				try {
					log.info("Attempting R Connection");
					c = new RConnection(host, port);
					log.info("R Connection Successful");
					System.out.println(c.eval("version$version.string"));
					//REXP rResponseObject = c.parseAndEval("summary(df)");
					
//					 run staging script
					c.parseAndEval("try(source(\""+StagingScript+"\"),silent=TRUE)");
					
//					input model parameter
					c.parseAndEval("model_parameter = \""+model_parameter+"\"");
					
					c.parseAndEval("output_parameter = "+output_parameter);
//					Run model script
					c.parseAndEval("try(source(\""+ExecutionScript+"\"),silent=TRUE)");
////					Run out filter
//					c.parseAndEval("output_parameter = "+output_parameter);
					
					REXP list_1 = (REXP) c.eval("df['Subcategory_Code']").asList().elementAt(0);
					REXP textlist_1 = (REXP) c.eval("df['JourneyStep']").asList().elementAt(0);
					REXP textlist_2 = (REXP) c.eval("df['Attr']").asList().elementAt(0);
					REXP list_2 = (REXP) c.eval("df['year_flag']").asList().elementAt(0);
					REXP list_3 = (REXP) c.eval("df['TotalSample']").asList().elementAt(0);
					REXP list_4 = (REXP) c.eval("df['Slope']").asList().elementAt(0);

					Subcategory_Code = list_1.asDoubles();	
					JourneyStep = textlist_1.asStrings();
					Attr = textlist_2.asStrings();
					year_flag = list_2.asDoubles();	
					TotalSample = list_3.asDoubles();	
					Slope = list_4.asDoubles();	
					
				
					ArrayList<Object[]> dataTemp = new ArrayList<Object[]>();
					int i, j;
//	How to deal with null values?
					for (i = 0; i < Subcategory_Code.length; i++) {		
						
						Object[] dt = new Object[9];
						j=0;
						for (ColumnMetaData column : columns){
							if (column.getColumnName().equals("Subcategory_Code")) {
								dt[j]=Subcategory_Code[i];
							} else if (column.getColumnName().equals("JourneyStep")){
								dt[j]=JourneyStep[i];
							} else if (column.getColumnName().equals("Attr")){
								dt[j]=Attr[i];
							} else if (column.getColumnName().equals("year_flag")){
								dt[j]=year_flag[i];
							} else if (column.getColumnName().equals("TotalSample")){
								dt[j]=TotalSample[i];
							} else if (column.getColumnName().equals("Slope")){
								dt[j]=Slope[i];
							} 
							j++;
						}
						dataTemp.add(dt);
	
					}
					Object[][] data = new Object[dataTemp.size()][columns.size()];
	
					for (i = 0; i < dataTemp.size(); i++) {
						for (j = 0; j < columns.size(); j++) {
							data[i][j] = dataTemp.get(i)[j];
						}
					}
	
					return data;
				} catch (Exception e) {
					log.error("failed to do R stuff");
					e.printStackTrace();
				}
				return new Object[9][columns.size()];
								
				
			}
			
			@Override
			public boolean getAllowsDuplicateColumns() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean getAllowsAggregateColumns() {
				// TODO Auto-generated method stub
				return true;
			}

		};

		return simpleDataSet;
	}

	public JDBCMetaData getDataSourceMetaData() {
		return new TestMetaData();
	}

	public boolean authenticate() {
		return true;
	}

	public void disconnect() {

	}
	public Map<String, Object> testConnection() {

		/*
		 * In this function you should define the actions that the connector should
		 * perform if the user clicks the 'Test Connection' button. If you want to tell
		 * Yellowfin that the connection was not successful then the Map that you return
		 * should contain a value with key "ERROR"
		 */
		Map<String, Object> p = new HashMap<String, Object>();
		String pin = (String) getAttribute("HOST");

		if (!pin.equals("HOST")) {
			p.put("Version", 1);
			p.put("connector", "test connector");
		} else {
			p.put("ERROR", "Plese insert 123 in PIN field to pass this validation");
		}

		return p;
	}

	public ScheduleDefinition getScheduleDefinition() {
		/*
		 * In this function define the frequency with which Yellowfin should execute the
		 * autorun function
		 */
		return new ScheduleDefinition(FrequencyTypeCode.MINUTES, null, 5);
	};
	public boolean autoRun() {
		/*
		 * This function is being automatically called by Yellowfin with a frequency
		 * defined in ScheduleDefinition() function. It can be helpful if you need to
		 * run a background job for the connector (for example update tokens)
		 */
		System.out.println("Auto running Test data source");

		/*
		 * saveBlob(String key, byte[] value) and loadBlob(key) functions allow to store
		 * and retrieve data from database at given keys
		 */
		String aToken = "aaa";
		saveBlob("ACCESS_TOKEN", aToken.getBytes());
		aToken = new String(loadBlob("ACCESS_TOKEN"));

		saveBlob("LASTRUN", new Date(System.currentTimeMillis()).toLocaleString().getBytes());

		return true;
	}
	protected byte[] loadBlobData(String key) {
		return loadBlob(key);
	}
}
