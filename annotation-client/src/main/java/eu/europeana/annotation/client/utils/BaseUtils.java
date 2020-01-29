package eu.europeana.annotation.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is a helper class with utils methods
 * 
 * @author GrafR
 *
 */
public class BaseUtils {
	
	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public String getJsonStringInput(String resource) throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream(
				resource);
		
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
		for(String line = br.readLine(); line != null; line = br.readLine()) 
		    out.append(line);
		br.close();
		return out.toString();
		
	}
	
}

