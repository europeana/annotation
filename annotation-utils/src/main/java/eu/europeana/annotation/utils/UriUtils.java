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
			URL url = new URL(value);
			return !StringUtils.isBlank(url.getProtocol());
		} catch (MalformedURLException e) {
			return false;
		}
	}

}
