package customAdv;


import com.hof.mi.interfaces.AnalyticalFunction;
import com.hof.mi.interfaces.UserInputParameters;
import com.hof.mi.interfaces.UserInputParameters.Parameter;
import com.hof.util.Const;
import com.hof.util.UtilString;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RAdvancedFunction
  extends AnalyticalFunction
{
	
	
  private HashMap<String, BigDecimal> groupTotals;  
  private double[] results;
  
  public String getName()
  {
    return "R Advanced Function Example";
  }
  
  public String getDescription()
  {
    return  "Connects to R serve and runs a script.";
  }
  //param string = current name of column
  public String getColumnHeading(String paramString)
  {
    return "Derived "+paramString;
  }
  
  public String getCategory()
  {
    return "Fusion Custom Functions";
  }
  //WE CAN ONLY RETURN 1 of these!!!
  public int getReturnType()
  {
    return TYPE_NUMERIC;
  }
  // we can accept multiple differnt types of fields
  public boolean acceptsNativeType(int paramInt)
  {
	if (paramInt  == TYPE_NUMERIC) {
		return true;
	}
	else if (paramInt == TYPE_TEXT) {
		return true;
	}
	  
    return paramInt == TYPE_NUMERIC;
  }
  
  protected void setupParameters()
  {
    Parameter p = new Parameter();
    p.setUniqueKey("REFCOL");
    p.setDisplayName(UtilString.getResourceString("mi.text.analytic.function.dividebycolumn.parameter.column.name"));
    p.setDescription("Column 1 - Number to be subtracted");
    p.setDataType(100);
    p.setAcceptsFieldType(TYPE_NUMERIC, true);
    p.setDisplayType(6);
    addParameter(p);
    
    p = new Parameter();
    p.setUniqueKey("SCRIPT");
    p.setDisplayName("Script File");
    p.setDescription("Location of the script file on the R Server");
	p.setDataType(UserInputParameters.TYPE_TEXT);
	p.setDisplayType(UserInputParameters.DISPLAY_TEXT_LONG);
	p.setDefaultValue(new String(""));
	addParameter(p);

	p = new Parameter();
    p.setUniqueKey("HOST");
    p.setDisplayName("HOST IP");
    p.setDescription("The Host ip for Rserve");
	p.setDataType(UserInputParameters.TYPE_TEXT);
	p.setDisplayType(UserInputParameters.DISPLAY_TEXT_LONG);
	p.setDefaultValue("0.0.0.0");
	addParameter(p);
    
	p = new Parameter();
    p.setUniqueKey("PORT");
    p.setDisplayName("HOST IP PORT");
    p.setDescription("The HOST IP PORT for Rserve");
	p.setDataType(UserInputParameters.TYPE_TEXT);
	p.setDisplayType(UserInputParameters.DISPLAY_TEXT_LONG);
	p.setDefaultValue("6311");
	addParameter(p);
	
//    p = new Parameter();
//    p.setUniqueKey("REFCOLTWO");
//    p.setDisplayName(UtilString.getResourceString("mi.text.analytic.function.dividebycolumn.parameter.column.name"));
//    p.setDescription("Column 2 - Numeric And Text");
//    p.setDataType(100);
//    p.setAcceptsFieldType(TYPE_NUMERIC, true);
//    p.setAcceptsFieldType(TYPE_TEXT, true);
//    p.setDisplayType(6);
//    addParameter(p);
       
  }
  public void preAnalyticFunction(Object[] selectedCol) {
	Object[] referenceCol = (Object[]) getParameterValue("REFCOL");
	
	String scriptFile = (String)getParameterValue("SCRIPT");	
	String ipAddress = (String)getParameterValue("HOST");	
	
	Integer Port = Integer.valueOf((String)getParameterValue("PORT")); 
	//c = new RConnection("hostname", "Port");	
	
	try {
		
		RConnection c = new RConnection(ipAddress, Port);
		System.out.println(createArrayString(selectedCol));
		REXP rResponseObject = c.parseAndEval("testDF <- data.frame(colOne="+createArrayString(selectedCol)+")");
		
		if (rResponseObject.inherits("try-error")) {
			System.out.println(rResponseObject.asString());
		}
		
		rResponseObject = c.parseAndEval("testDF['ColTwo'] ="+createArrayString(referenceCol));
		
		if (rResponseObject.inherits("try-error")) {
			System.out.println(rResponseObject.asString());
		}
		
		
		//rResponseObject = c.parseAndEval();
		//C:/Users/NathanSchroeder/Documents/test.r
		//scriptFile
		rResponseObject =  c.parseAndEval("try(source(\""+scriptFile+"\"),silent=TRUE)");
		
		if (rResponseObject.inherits("try-error")) {
			System.out.println(rResponseObject.asString());
		}
//		c.eval("testDF['ColThree']=read.csv(");
		System.out.println("Script Executed");
		REXP list = (REXP) c.eval("testDF['ColThree']").asList().elementAt(0);

		if (list instanceof REXPDouble) {
			results = list.asDoubles();	
		}
		
		for (int i=0;i<results.length;i++) {
			System.out.println(results[i]);
		}
		c.eval("print(testDF)");
		c.eval("print(\"Hithere2\")");
       
	} catch (Exception e) {
		e.printStackTrace();
	}	

	
//	groupTotals = new HashMap<String, BigDecimal>();
//	for (int i=0;i<selectedCol.length;i++){
//		if (selectedCol[i]!=null && referenceCol[i]!= null){
//			if (groupTotals.keySet().contains(referenceCol[i])){
//				BigDecimal total = groupTotals.get(referenceCol[i]);
//				groupTotals.put(referenceCol[i].toString(), total.add(new BigDecimal(selectedCol[i].toString())));
//			}else{
//				groupTotals.put(referenceCol[i].toString(), new BigDecimal(selectedCol[i].toString()));
//			}
//		}
//	}
//	for (String key : groupTotals.keySet()){
//		System.out.println(key + ":"  +groupTotals.get(key));
//
//	}

  }
  public Object applyAnalyticFunction(int i, Object val)
  {
	  System.out.println(val.toString());
	  return results[i];
  }
  
  
  
  
  
  
	public String createArrayString(Object[] column) {
		JSONArray columnAsString;
		try {
			columnAsString = new JSONArray(column);
	
			String rlist = columnAsString.toString().replace("\"null\"", "NA").replace("null", "NA").replaceFirst("\\[",
					"c(");
	
			if (column.length > 0 && column[0] instanceof BigDecimal) {
				rlist = rlist.replace("\"", "");
			}
			return replaceLast(rlist, "]", ")");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	String replaceLast(String string, String substring, String replacement) {
		int index = string.lastIndexOf(substring);
		if (index == -1)
			return string;
		return string.substring(0, index) + replacement + string.substring(index + substring.length());
	}



}