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
package eu.europeana.annotation.clientapp;

import static org.junit.Assert.assertNotNull;

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
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.service.authentication.mock.MockAuthenticationServiceImpl;

/**
 * This class implements conversion from annotation api key to client application.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml" 
	})
public class ConvertJsonConfigToApplicationTest{
	
	public final String API_KEY_CONFIG_FOLDER = "/config"; 
	public final String API_KEY_STORAGE_FOLDER = "/authentication_templates"; 

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

	Logger logger = Logger.getLogger(getClass());
	
	private Gson gson = null;

    
	@Before
    public void setUp() throws Exception {
		setGson(new Gson());
    }

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	
	
//  @Test
  public void storeApiKeysApplicationsToMongo() throws ApplicationAuthenticationException, AnnotationMongoException{
  	
  	MockAuthenticationServiceImpl authenticationService = new MockAuthenticationServiceImpl(
  			configuration, clientService);

  	// read from MongoDB
	Iterable<PersistentClient> allStoredClients = clientService.findAll();

	for (PersistentClient storedClient : allStoredClients) {

		Application clientApplication = authenticationService.parseApplication(
				storedClient.getAuthenticationConfigJson());

		// put application in the client
		storedClient.setClientApplication(clientApplication);
		
    	// write to MongoDB
    	clientService.update(storedClient);		
	}		
  }
  
  
  @Test
  public void retrieveLoadedApiKeysByApplicationObjects() throws ApplicationAuthenticationException, AnnotationMongoException{
  	
  	MockAuthenticationServiceImpl authenticationService = new MockAuthenticationServiceImpl(
  			configuration, clientService);
  	authenticationService.loadApiKeys();
  	for (Map.Entry<String, Application> entry : authenticationService.getCachedClients().entrySet())
  	{
  		String key = entry.getKey();
        Application app = entry.getValue();
  		System.out.println(key + "/" + entry.getValue());
    	assertNotNull(app);

		String json = getGson().toJson(app);
    	assertNotNull(json);
  	}
  }  
  
}
