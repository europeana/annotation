package eu.europeana.annotation.definitions.model.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class TypeUtils {
	
	private static Logger log = Logger.getRootLogger();
	
	/**
	 * This method extracts internalType from the input string with multiple types.
	 * The syntax of the internalType is as follows: "<internalType key>:<Object Type>"
	 * e.g. 'oa:Tag' or 'foaf:Person'.
	 * @param typesString
	 * @return
	 */
	public String getInternalTypeFromTypeArray(String typeArray) {
		String res = "";
		if (StringUtils.isNotEmpty(typeArray)) {
		    for (WebAnnotationFields.TypeNamespaces tn : WebAnnotationFields.TypeNamespaces.values()) {
				res = matchType(typeArray, tn.name());	
				if (StringUtils.isNotBlank(res)) 
		            return res;
		    }
			
//			res = matchType(typeArray, WebAnnotationFields.INTERNAL_TYPE);	
//			/**
//			 * When no entry for 'oa' try 'foaf'
//			 */
//			if (res.equals("")) 
//			res = matchType(typeArray, WebAnnotationFields.FOAF);					
		}
		return res;
	}

	/**
	 * @param typeArray
	 * @param key
	 * @return
	 */
	private String matchType(String typeArray, String key) {
		String res = "";
		//			Pattern pattern = Pattern.compile(WebAnnotationFields.INTERNAL_TYPE + ":(.*?)]");
//		String regex = ":(.*?)[,]";
//		if (!typeArray.contains(","))
//			regex = ":(.*)$";
		String regex = ":(.*?)(?:[,]|$)";
		Pattern pattern = Pattern.compile(key + regex);
		Matcher matcher = pattern.matcher(typeArray);
		if (matcher.find()) {
		    res = matcher.group(1);
		}
		return res;
	}
	
	public static String getInternalTypeFromTypeArrayStatic(String typeArray) {
	    return (new TypeUtils()).getInternalTypeFromTypeArray(typeArray);
	}
	
	/**
	 * This method removes tabs for Web Service parameters.
	 * @param value
	 * @return value without tabs
	 */
	public String removeTabs(String value) {
		return value.replaceAll("\t", "");
	}
	
    public static String convertDateToStr(Date date) {
    	String res = "";    	
    	DateFormat df = new SimpleDateFormat(WebAnnotationFields.DATE_FORMAT);
    	res = df.format(date);    	
    	return res;
    }

    public static Date convertStrToDate(String str) {
    	Date res = null; 
    	DateFormat formatter = new SimpleDateFormat(WebAnnotationFields.DATE_FORMAT);
    	try {
			res = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return res;
    }
    
    public static Date convertUnixTimestampStrToDate(String unixTimestamp) {
    	return new Date(Long.valueOf(unixTimestamp)*1000);
    }
    
    /**
     * This method performs a conversion of date in string format 'dd-MM-yyyy' to unix date without 
     * modifying the date.
     * @param curDate
     * @return long value of the unix date in string format
     */
    public static String getUnixDateStringFromDate(String curDate) {
    	String res = "";
		try {
	    	log.debug("getUnixDateStringFromDate curDate: " + curDate);
			Date resDate = new SimpleDateFormat(ModelConst.DATE_FORMAT).parse(curDate);
			Long longTime = new Long(resDate.getTime()/1000);
			log.info("long time: " + longTime);
			res = String.valueOf(longTime);
			log.info("res date: " + res);
			log.debug("check stored date - convert back to human date: " + getDateFromUnixDate(res));
		} catch (ParseException e) {
			log.debug("Conversion of date in string format dd-MM-yyyy to unix date: " + e);
		}
        return res;
    }
    
    /**
     * This method performs a conversion of date in string format 'dd-MM-yyyy' to unix date without 
     * modifying the date.
     * @param curDate
     * @return long value of the unix date in string format
     */
    public static String getUnixDateStringFromDate(Date curDate) {
    	String res = "";
		try {
	    	log.debug("getUnixDateStringFromDate curDate: " + curDate);
			Long longTime = new Long(curDate.getTime()/1000);
			log.info("long time: " + longTime);
			res = String.valueOf(longTime);
			log.info("res date: " + res);
			log.debug("check stored date - convert back to human date: " + getDateFromUnixDate(res));
		} catch (Exception e) {
			log.debug("Conversion of date in string format dd-MM-yyyy to unix date: " + e);
		}
        return res;
    }
    
    /**
     * This method converts unix date to date.
     * @param unixDate
     * @return date as a string
     */
    public static String getDateFromUnixDate(String unixDate) {
    	String res = "";
    	if (unixDate != null && unixDate.length() > 0) {
	    	long unixSeconds = Long.valueOf(unixDate);
	    	Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
	    	SimpleDateFormat sdf = new SimpleDateFormat(ModelConst.DATE_FORMAT); // the format of your date
	    	sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
	    	res = sdf.format(date);
    	}
    	return res;
    }
      
    public static String getDateFromUnixDateExt(String unixDate) {
    	String res = "";
    	if (unixDate != null && unixDate.length() > 0) {
	    	long unixSeconds = Long.valueOf(unixDate);
//	    	Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
	    	Date date = new Date(unixSeconds); // *1000 is to convert seconds to milliseconds
	    	SimpleDateFormat sdf = new SimpleDateFormat(WebAnnotationFields.DATE_FORMAT); // the format of your date
	    	sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
	    	res = sdf.format(date);
    	}
    	return res;
    }
      
	public static String validateDates(String jsonPost) {
		String res = jsonPost;
		res = validateDate(ModelConst.ANNOTATED_AT, res);
		res = validateDate(ModelConst.SERIALIZED_AT, res);
		return res;
	}

	public static String validateDate(String fieldName, String jsonPost) {
		String res = jsonPost;
		String annotatedDateStr = getDateFromJsonString(fieldName, jsonPost);
		Date normalizedDate = TypeUtils.convertStrToDate(annotatedDateStr); 
		String unixDateStr = TypeUtils.getUnixDateStringFromDate(normalizedDate);
		if (StringUtils.isNotBlank(annotatedDateStr)) 
			res = res.replace(annotatedDateStr, unixDateStr);
		return res;
	}
    
	public static String validateDateExt(String fieldName, String jsonPost) {
		String res = jsonPost;
		String unixDateStr = getDateFromJsonStringExt(fieldName, jsonPost);
		String normalizedDate = TypeUtils.getDateFromUnixDateExt(unixDateStr); //convertUnixTimestampStrToDate(unixDateStr); 
		if (StringUtils.isNotBlank(unixDateStr)) 
			res = res.replace(unixDateStr, normalizedDate);
		return res;
	}
    
	/**
	 * This method extracts date value from the JSON string for particular date field.
	 * The syntax of the euType is as follows: "fieldName":"value"
	 * @param fieldName
	 * @param jsonStr
	 * @return
	 */
	public static String getDateFromJsonString(String fieldName, String jsonStr) {
		String res = "";
		if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(jsonStr) && jsonStr.contains(fieldName)) {
			Pattern pattern = Pattern.compile(fieldName + "\":\"(.*?)\",");
			Matcher matcher = pattern.matcher(jsonStr);
			if (matcher.find()) {
			    res = matcher.group(1);
			}		
		}
		return res;
	}
	
	public static String getDateFromJsonStringExt(String fieldName, String jsonStr) {
		String res = "";
		if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(jsonStr) && jsonStr.contains(fieldName)) {
			Pattern pattern = Pattern.compile(fieldName + "\":(.*?),");
			Matcher matcher = pattern.matcher(jsonStr);
			if (matcher.find()) {
			    res = matcher.group(1);
			}		
		}
		return res;
	}
	
	/**
	 * This method presents a list as a string in JSON-LD format.
	 * @param typeList
	 * @return type list string
	 */
	public static String getTypeListAsStr(List<String> typeList) {
		String listStr = "";
		if (typeList.size() > 0) {
			listStr = "[";
			for (String s : typeList)
			{
				if (listStr.equals("[")) {
				    listStr += s;
				} else {
					listStr += "," + s;
				}
			}
			listStr += "]";
		}
		return listStr;
	}
	
	/**
	 * This method converts JSON-LD string to a Java list.
	 * @param typeStr
	 * @return list of strings.
	 */
	public static List<String> convertStringToList(String typeStr) {
		List<String> res = new ArrayList<String>(2);
	    if (!StringUtils.isBlank(typeStr)) { 
	    	typeStr = typeStr.replace("[", "").replace("]", "").replace(" ", "");
	        String[] tokens = typeStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	        for(String t : tokens) {
	        	res.add(t);
	        }
		}
	    return res;		
	}

	/**
	 * This method presents a map as a string in JSON-LD format.
	 * @param mp
	 * @return
	 */
	public static String getTypeMapAsString(Map<String,String> mp) {
		String res = "";
	    Iterator<Map.Entry<String, String>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
	        if (res.length() > 0) {
	        	res = res + ",";
	        }
	        res = res + "\"" + pairs.getKey() + "\"" + WebAnnotationFields.SEPARATOR_SEMICOLON 
	        		+ "\"" + pairs.getValue() + "\"";
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    if (res.length() > 0) {
	    	res = "{" + res + "}";
	    }
	    return res;
	}	
		
	/**
	 * This method checks whether a query type is in a type list.
	 * @param queryTypeName
	 * @param types
	 * @return true if type is in list.
	 */
	public static boolean isTypeInList(String queryTypeName, List<String> types){
		boolean res = false;
		Iterator<String> itr = types.iterator();
		while (itr.hasNext()) {
			String type = itr.next();
			if (queryTypeName.equals(type)) {
				res = true;
				break;
			}				
		}
		return res;
	}
	
	/**
	 * This method compares two maps.
	 * @param m1
	 * @param m2
	 * @return true if maps are equal
	 */
	public static boolean areEqualMaps(Map<String,String>m1, Map<String,String>m2) {
	    if (m1.size() != m2.size())
	        return false;
	    for (String key: m1.keySet())
	        if (!m1.get(key).equals(m2.get(key)))
	            return false;
	    return true;
	}	
}
