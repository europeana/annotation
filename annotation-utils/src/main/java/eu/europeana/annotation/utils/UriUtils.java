package eu.europeana.annotation.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class UriUtils {

	/**
	 * Check if a string is a valid URL
	 * 
	 * @param value
	 *            URL String
	 * @return True if valid URL, false otherwise
	 */
	public static boolean isUrl(String value) {
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
