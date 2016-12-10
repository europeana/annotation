package eu.europeana.annotation.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class StringUtils {

	/**
	 * Check if a string is a valid URL
	 * 
	 * @param value
	 *            URL String
	 * @return True if valid URL, false otherwise
	 */
	public static boolean isUrl(String value) {
		try {
			new URL(value);
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}

}
