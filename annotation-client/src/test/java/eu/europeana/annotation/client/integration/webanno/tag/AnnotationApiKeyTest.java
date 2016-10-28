/*
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;


/**
 * This class implements api key testing scenarios.
 */
public class AnnotationApiKeyTest extends BaseTaggingTest {
	
	public final String API_KEY_CONFIG_FOLDER = "/config"; 
	public final String API_KEY_STORAGE_FOLDER = "/authentication_templates"; 
	
	protected Logger log = Logger.getLogger(getClass());

	public static String TEST_USER_TOKEN = "admin";
	public static String JSON_FORMAT = "json";

	
    private static final Map<String, String> apiKeyMap = new HashMap<String, String>();
    
    private static void initApiKeyMap() {
    	apiKeyMap.put("apiadmin", WebAnnotationFields.PROVIDER_EUROPEANA_DEV);
    	apiKeyMap.put("apidemo", WebAnnotationFields.PROVIDER_WEBANNO);
    	apiKeyMap.put("hpdemo", WebAnnotationFields.PROVIDER_HISTORY_PIN);
    	apiKeyMap.put("punditdemo", WebAnnotationFields.PROVIDER_PUNDIT);
    	apiKeyMap.put("withdemo", WebAnnotationFields.PROVIDER_WITH);
    	apiKeyMap.put("phVKTQ8g9F", WebAnnotationFields.PROVIDER_COLLECTIONS);
    }
	
   
	@Before
    public void setUp() throws Exception {
		initApiKeyMap();
    }

	
	public String readApiKeyApplicationFromFile(String apiKey, String path, String fieldName) {  
		  
		String res = "";
		     
		try {  	    
		    BufferedReader br = new BufferedReader(  
		         new FileReader(path));  
		     
		    String jsonData = br.readLine();
		    br.close();
		    
		    log.info("Load apiKey: "+ apiKey);  		
		    
//		    JSONParser parser = new JSONParser();
//		    JSONObject json = (JSONObject) parser.parse(jsonData);		    
		    try {
				JSONObject json = new JSONObject(jsonData);
				if (fieldName.equals("userGroup")) {
					JSONObject adminJsonObject = (JSONObject) json.get("admin");
					res = (String) adminJsonObject.get(fieldName);
				} else {
					res = (String) json.get(fieldName);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {  
		    log.info("apiKey \"" + apiKey + "\" file not found. " + e.getMessage());
		}  
		return res;
    }  

	
	public String getApiKeyApplicationValueByFieldName(
			String apiKey, String organization, String fieldName) {

		String res = "";
		
		String configFolder = getClass().getResource(API_KEY_CONFIG_FOLDER).getFile();
        String pathToApiKeyFolder = configFolder + API_KEY_STORAGE_FOLDER;
		File folder = new File(pathToApiKeyFolder);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	        if (listOfFiles[i].isFile()) {
	        	String fileName = listOfFiles[i].getPath();
	        	if ((organization + "." +  JSON_FORMAT).equals(listOfFiles[i].getName())) {
	        		System.out.println("File " + fileName);
	        		res = readApiKeyApplicationFromFile(apiKey, fileName, fieldName);
	        		break;
	        	}
	        }
	    }
	    return res;
	}
	

	/**
     * This test performs storage of moderation reports for admin user 
     * for all api keys stored in JSON files in template folder.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void createTagMinimalWithModerationReportAndRemoval() throws IOException, JsonParseException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL);
		
    	for (Map.Entry<String, String> entry : apiKeyMap.entrySet()) {

//    		Annotation storedAnno = createTagWithProviderAndUserToken(
//    				requestBody, entry.getValue(), TEST_USER_TOKEN);
    		Annotation storedAnno = createTag(requestBody);
    		String provider = getApiKeyApplicationValueByFieldName(
    				entry.getKey(), entry.getValue(), "provider");
    		String userGroup = getApiKeyApplicationValueByFieldName(
    				entry.getKey(), entry.getValue(), "userGroup");
//    		String identifier = getApiKeyApplicationValueByFieldName(
//    				entry.getKey(), entry.getValue(), "identifier");
    		String identifier = storedAnno.getAnnotationId().getIdentifier();
    		if (userGroup.equals("ADMIN") 
    				&& provider.equals(storedAnno.getAnnotationId().getProvider())) {
//    		if (provider.equals(entry.getValue())) {
				log.info(
						"apiKey: " + entry.getKey() + 
	//					", provider: " + storedAnno.getAnnotationId().getProvider() +
	//					", identifier: " + storedAnno.getAnnotationId().getIdentifier() +
						", provider: " + provider +
						", identifier: " + identifier +
						", userToken: " + TEST_USER_TOKEN
						);
	    		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
	    				entry.getKey()
	//    				, storedAnno.getAnnotationId().getProvider()
	//    				, storedAnno.getAnnotationId().getIdentifier()
	    				, provider
	    				, identifier
	    				, TEST_USER_TOKEN);
	    		validateReportResponse(reportResponse, HttpStatus.CREATED);
    		
	    		ResponseEntity<String> response = getApiClient().deleteAnnotation(
	    				entry.getKey()
	    				, storedAnno.getAnnotationId().getProvider()
	//    				, storedAnno.getAnnotationId().getIdentifier()
	//    				, provider
	    				, identifier
	    				, TEST_USER_TOKEN
	    				, JSON_FORMAT
	    				);
	    		validateReportResponse(response, HttpStatus.NO_CONTENT);
    		}
    	}
		
	}
	
	
	protected void validateReportResponse(ResponseEntity<String> response, HttpStatus status) 
			throws JsonParseException {
		assertEquals(status, response.getStatusCode());
	}
	
		
}
