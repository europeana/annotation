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
package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Vote;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.annotation.web.service.controller.jsonld.BaseJsonldRest;
import eu.europeana.corelib.logging.Logger;

/**
 * This class implements different SKOS testing scenarios.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml" 
	})
public class AnnotationApiKeyTest {
	
	public final String API_KEY_CONFIG_FOLDER = "/config"; 
	public final String API_KEY_STORAGE_FOLDER = "/authentication_templates"; 
	public final String USER_ADMIN = "admin";
	public final String TEST_IDENTIFIER = null;//"http://data.europeana.eu/annotation/webanno/494";
	
    private static final Map<String, String> apyKeyMap = new HashMap<String, String>();
    
    private static void initApiKeyMap() {
    	apyKeyMap.put("apiadmin", WebAnnotationFields.PROVIDER_EUROPEANA_DEV);
    	apyKeyMap.put("apidemo", WebAnnotationFields.PROVIDER_WEBANNO);
    	apyKeyMap.put("hpdemo", WebAnnotationFields.PROVIDER_HISTORY_PIN);
    	apyKeyMap.put("punditdemo", WebAnnotationFields.PROVIDER_PUNDIT);
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
     * This test performs storage of moderation reports for admin user 
     * for all api keys stored in JSON files in template folder.
	 * @throws HttpException 
     */
    @Test
    public void testCreateAnnotationRecordsForAdminUserFromConfigTemplate() throws HttpException {
    	  	
    	for (Map.Entry<String, String> entry : apyKeyMap.entrySet()) {
        	Application app = getAuthenticationService().findByApiKey(entry.getKey());
        	assertNotNull(app);
        	assertNotNull(app.getApiKey());
        	Agent adminUser = app.getAdminUser();
        	assertNotNull(adminUser.getName());
    		try {
    			String wsKey = entry.getKey();
    			getAuthenticationService().getByApiKey(wsKey);

    			AnnotationId annoId = validateInputsForUpdateDelete(
    					wsKey, app.getProvider(), TEST_IDENTIFIER, USER_ADMIN);

    			@SuppressWarnings("deprecation")
				Agent user = authorizeUser(USER_ADMIN, wsKey, annoId, Operations.REPORT);

    			Date reportDate = new Date();
    			Vote vote = buildVote(user, reportDate);
    			ModerationRecord moderationRecord = getAnnotationService().findModerationRecordById(annoId);
    			if (moderationRecord == null)
    				moderationRecord = buildNewModerationRecord(annoId, reportDate);

    			moderationRecord.addReport(vote);
    			moderationRecord.computeSummary();
    			moderationRecord.setLastUpdated(reportDate);

    			ModerationRecord storedModeration = getAnnotationService().storeModerationRecord(
    					moderationRecord);
            	assertNotNull(storedModeration);
            	assertNotNull(storedModeration.getAnnotationId());
            	assertNotNull(storedModeration.getAnnotationId().getProvider().equals(app.getProvider()));
    		} catch (HttpException e) {
    			// avoid wrapping HttpExceptions
    			throw e;
    		} catch (Exception e) {
    			throw new InternalServerException(e);
    		}
        	
    	}
    }  
    
	
}
