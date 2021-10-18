package com.test.connector;

import com.hof.pool.DBType;
import com.hof.pool.JDBCMetaData;

public class RMetaDatamodel extends JDBCMetaData {

	
	public RMetaDatamodel() {
		
		super();
		
		sourceName = "Data Source";
		sourceCode = "DATA_SOURCE";
		driverName = "com.test.connector.RDataSourcemodel";
		sourceType = DBType.THIRD_PARTY;
		//sourceid = source;
		
	}
	
		
	
	 public  void initialiseParameters() 
	 {
	        super.initialiseParameters();
	        
	        addParameter(new Parameter("HOST", "Hostname",  "Provide the hostname for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));
	        addParameter(new Parameter("PORT", "Port",  "Provide the Port for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));
	        addParameter(new Parameter("SCRIPT", "Script File",  "Provide the hostname for the R Serve instance", TYPE_TEXT, DISPLAY_TEXT_LONG, null, true));

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