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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.corelib.logging.Logger;

/**
 * This class implements different SKOS testing scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml" 
	})
public class AnnotationApiKeyToJsonTest {
	
	@Resource
	AuthenticationService authenticationService;
	
	public final String API_KEY_CONFIG_FOLDER = "/config"; 
	public final String API_KEY_STORAGE_FOLDER = "/authentication_templates"; 
	
	Logger logger = Logger.getLogger(getClass());


	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

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
	
	/**
     * This test performs storage of api key data in JSON format.
     */
    @Test
    public void testStoreApiKeysAsJson() {
    	  	
    	storeApiKeyAsJson("apiadmin", WebAnnotationFields.PROVIDER_EUROPEANA_DEV);
    	storeApiKeyAsJson("apidemo", WebAnnotationFields.PROVIDER_WEBANNO);
    	storeApiKeyAsJson("hpdemo", WebAnnotationFields.PROVIDER_HISTORY_PIN);
    	storeApiKeyAsJson("punditdemo", WebAnnotationFields.PROVIDER_PUNDIT);
    	storeApiKeyAsJson("withdemo", WebAnnotationFields.PROVIDER_WITH);
    	storeApiKeyAsJson("phVKTQ8g9F", WebAnnotationFields.PROVIDER_COLLECTIONS);
    }
    
    public void storeApiKeyAsJson(String key, String filename) {
    	
    	Application app;
		try {
			app = getAuthenticationService().findByApiKey(key);
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
    
}
