package com.fusion.connector;

import com.hof.pool.DBType;
import com.hof.pool.JDBCMetaData;

public class TestMetaData extends JDBCMetaData {

	
	public TestMetaData() {
		
		super();
		
		sourceName = "Test Data Source";
		sourceCode = "Test_DATA_SOURCE";
		driverName = "com.fusion.connector.TestDataSource";
		sourceType = DBType.THIRD_PARTY;
		
		
	}
	
		
	
	 public  void initialiseParameters() 
	 {
	        super.initialiseParameters();
	        
	        addParameter(new Parameter("HOST", "Hostname",  "Provide the hostname for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));
	        addParameter(new Parameter("PORT", "Port",  "Provide the Port for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));
	        addParameter(new Parameter("STAGINGSCRIPT", "StagingScript File",  "Provide the Staging script for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));
	        addParameter(new Parameter("EXECUTIONSCRIPT", "Execution File",  "Provide the execution script for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));

	 }	    
	   
	 public String buttonPressed(String buttonName) throws Exception 
	 {    
        /*In this function you should define the actions that should be performed in case if some button was pressed. 
         *String buttonName contains the ID of the button that was pressed */
		 if (buttonName.equals("POSTPIN"))
	     {
			 setParameterValue("ACCESSTOKEN", "PIN: "+getParameterValue("PIN"));
		     setParameterValue("ACCESSTOKENSECRET", "PIN: " + getParameterValue("PIN"));
	     }
		 return null;
	 }
	 
	 @Override
		public byte[] getDatasourceIcon() {
		 /*This function should return Base-64 encrypted version of icon file*/
			String str ="abcdedf";
			return str.getBytes();
		}
	    
		
		@Override
		public String getDatasourceShortDescription(){
			return "Short Description of Connector";
		}

		@Override
		public String getDatasourceLongDescription(){
			return "Long Description of Connector";
		}
}
