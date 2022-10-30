package eu.europeana.annotation.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GeneralUtils {
  /**
   * Splitting a string into a list. It also strips whitespace from the start and end of every String in a list.
   * A null separator splits on whitespace.
   * @param concatenatedStrings
   * @param separator
   * @return
   */
  public static List<String> splitStringIntoList(String concatenatedStrings, String separator) {
      if (StringUtils.isEmpty(concatenatedStrings))
          return Collections.<String>emptyList();
      String[] array = StringUtils.splitByWholeSeparator(concatenatedStrings, separator);
      array=StringUtils.stripAll(array);
      return new ArrayList<String>(Arrays.asList(array));
  }
  
  public static String getQueryParamValue(String queryUrl, String paramName) throws MalformedURLException {
      URL aURL = new URL(queryUrl);
      String queryPart = aURL.getQuery();
      String[] params = queryPart.split("&");
      for (String param : params) {
        String[] paramValuePair = param.split("=");
        if(paramValuePair.length != 2)
          throw new IllegalStateException("Not a parameter/value pair!");
        String name = paramValuePair[0];
        if (name.equalsIgnoreCase(paramName)) {
          String value = param.split("=")[1];
          return value;
        }
      }
      return null;
  }
	
  public static Integer getQueryParamNumValue(String queryUrl, String paramName) throws MalformedURLException {
      String paramVal = getQueryParamValue(queryUrl, paramName);
      Integer numParamVal = Integer.parseInt(paramVal);
      return numParamVal;
  }
  
  /**
   * Check if a string is a valid URL
   * 
   * @param value
   *            URL String
   * @return True if valid URL, false otherwise
   */
  public static boolean isUrl(String value) {
      if(StringUtils.isEmpty(value)) {
        return false;
      }
      try {
          //only http URLs are supported in targets so do quickcheck first
          if(!value.startsWith("http")) {
            return false;
          }
          URL url = new URL(value);
          return StringUtils.isNotBlank(url.getProtocol());
      } catch (MalformedURLException e) {
          return false;
      }
  }
  
  public static boolean urlStartsWithHttps(String value) {
      //only URLs that start with "https" are allowed
      if(value.startsWith("https")) {
        return true;
      }
      else {
        return false;
      }
  }


}
