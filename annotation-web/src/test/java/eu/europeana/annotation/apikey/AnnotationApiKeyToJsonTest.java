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
package eu.europeana.annotation.apikey;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.PersistentClientImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.service.authentication.mock.MockAuthenticationServiceImpl;
import eu.europeana.annotation.web.service.controller.jsonld.BaseJsonldRest;

/**
 * This class implements different SKOS testing scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml" 
	})
public class AnnotationApiKeyToJsonTest extends BaseJsonldRest {
	
	public final String API_KEY_CONFIG_FOLDER = "/config"; 
	public final String API_KEY_STORAGE_FOLDER = "/authentication_templates"; 
	public final String USER_ADMIN = "admin";
	public final String TEST_IDENTIFIER = null;//"http://data.europeana.eu/annotation/webanno/494";
	public static final String KEY_APIADMIN = "apiadmin";
	
    private static final Map<String, String> apyKeyMap = new HashMap<String, String>();
    
    @Resource
    PersistentClientService clientService;
    
    @Resource
    AnnotationConfiguration configuration;
    
    public PersistentClientService getClientService() {
		return clientService;
	}

	public void setClientService(PersistentClientService clientService) {
		this.clientService = clientService;
	}

	private static void initApiKeyMap() {
    	apyKeyMap.put(KEY_APIADMIN, WebAnnotationFields.PROVIDER_EUROPEANA_DEV);
    	apyKeyMap.put("apidemo", WebAnnotationFields.PROVIDER_WEBANNO);
    	apyKeyMap.put("hpdemo", WebAnnotationFields.PROVIDER_HISTORY_PIN);
    	apyKeyMap.put("pMFSDInF22", WebAnnotationFields.PROVIDER_PUNDIT);
    	apyKeyMap.put("withdemo", WebAnnotationFields.PROVIDER_WITH);
    	apyKeyMap.put("phVKTQ8g9F", WebAnnotationFields.PROVIDER_COLLECTIONS);
    }
	
	Logger logger = Logger.getLogger(getClass());
	
	private Gson gson = null;

    
	@Before
    public void setUp() throws Exception {
		setGson(new Gson());
		initApiKeyMap();
    }

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
	/**
     * This test performs storage of api key data in JSON format.
	 * @throws ApplicationAuthenticationException 
     */
    @Test
    public void testFindApiKey() throws ApplicationAuthenticationException {
    	  	
    	Application app = getAuthenticationService().getByApiKey("hpdemo");
    	assertNotNull(app);
    	assertNotNull(app.getApiKey());
    	assertTrue(app.getApiKey().equals("hpdemo"));
    }
   
    
//    @Test
    public void testStoreApiKeysAsJson() {
    	  	
    	for (Map.Entry<String, String> entry : apyKeyMap.entrySet()) {
        	storeApiKeyAsJson(entry.getKey(), entry.getValue());
    	}
    }
    
    public void storeApiKeyAsJson(String key, String filename) {
    	
    	Application app;
		try {
			app = getAuthenticationService().getByApiKey(key);
	    	String json = getGson().toJson(app);
			String configFolder = getClass().getResource(API_KEY_CONFIG_FOLDER).getFile();
			// create file to store api key 
            String path = configFolder + API_KEY_STORAGE_FOLDER + "/" + filename + ".json";
			File f = new File(path);
			f.getParentFile().mkdirs(); 
			f.createNewFile();
			// store api key data
	    	FileWriter file = new FileWriter(f);
			file.write(json);
			file.close();
	    	assertNotNull(app);
	    	assertNotNull(app.getApiKey());
	    	logger.info(app.getApiKey());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ApplicationAuthenticationException e) {
			e.printStackTrace();
		}
    }
    

//    @Test
    public void storeApiKeysToMongo() throws ApplicationAuthenticationException, AnnotationMongoException{
    	
    	// create list of all existing IDs, to avoid creating duplicates
//    	Iterable<PersistentClient> existingClients = clientService.findAll();
//    	List<String> existingIDs = new ArrayList<String>();
//    	for (PersistentClient storedClient : existingClients) {
//    		existingIDs.add(storedClient.getClientId());
//    	}
    	
    	getAuthenticationService().loadApiKeysFromFiles();
    	
    	Application app;
    	for (String key : apyKeyMap.keySet()) {
    		// create new entry only if its not a duplicate
//    		if (!existingIDs.contains(key)) {
	    		app = getAuthenticationService().getByApiKey(key);
	        	String json = getGson().toJson(app);
	        	
	        	Date creationDate = new java.util.Date();    	
	        	PersistentClient user = new PersistentClientImpl();       	
	        	        	
				// credentials
	        	user.setClientId(key);
	        	user.setAuthenticationConfigJson(json);
	        	user.setCreationDate(creationDate);
	        	user.setLastUpdate(creationDate);
	        	        	
	        	// write to MongoDB
	        	clientService.create(user);
//    		}
		}
    	
    	// check if successful
    	Iterable<PersistentClient> all = clientService.findAll();

    	Client webanno = null;
    	for (PersistentClient storedClient : all) {
    		if(KEY_APIADMIN.equals(storedClient.getClientId()));
    			webanno = storedClient;
    	}
    	
    	assertNotNull(webanno);
    	System.out.println(webanno.getAuthenticationConfigJson());
    	
    	MockAuthenticationServiceImpl authenticationService = new MockAuthenticationServiceImpl(configuration, clientService);
    	
    	app = authenticationService.parseApplication(webanno.getAuthenticationConfigJson());
    	assertNotNull(app);
    	
    	
    	
//    	assertNotNull(app.getAnonymousUser());
//    	assertNotNull(app.getAdminUser());
//    	assertNotNull(app.getAuthenticatedUsers());
    	
//    	System.out.println(fileJSON);
//    	assertTrue(fileJSON.equals(webanno.getAuthenticationConfigJson()));
    	    	
    }
    
}
