package eu.europeana.annotation.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class QueryUtils {
	
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

}
