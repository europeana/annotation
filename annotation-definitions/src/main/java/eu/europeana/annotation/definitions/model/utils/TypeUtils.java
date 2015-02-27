package eu.europeana.annotation.definitions.model.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class TypeUtils {
	
	private static Logger log = Logger.getRootLogger();
	
	/**
	 * This method extracts euType from the input string with multiple types.
	 * The syntax of the euType is as follows: "euType:<Annotation Part>#<Object Type>"
	 * e.g. BODY#SEMANTIC_TAG
	 * @param typesString
	 * @return
	 */
	public String getEuTypeFromTypeArray(String typeArray) {
		String res = "";
		if (StringUtils.isNotEmpty(typeArray)) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.EU_TYPE + ":(.*?)]");
			Matcher matcher = pattern.matcher(typeArray);
			if (matcher.find()) {
			    res = matcher.group(1);
			}		
		}
		return res;
	}
	
	public static String getEuTypeFromTypeArrayStatic(String typeArray) {
	    return (new TypeUtils()).getEuTypeFromTypeArray(typeArray);
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
	
}
