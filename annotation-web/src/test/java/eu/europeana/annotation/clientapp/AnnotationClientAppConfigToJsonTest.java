package eu.europeana.annotation.clientapp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.gson.Gson;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.PersistentClientImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.mock.MockAuthenticationServiceImpl;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;

/**
 * This class implements different SKOS testing scenarios.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/annotation-web-context.xml" 
	})
public class AnnotationClientAppConfigToJsonTest{
	
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
    
    @Resource
	AuthenticationService authenticationService;
    
    public PersistentClientService getClientService() {
		return clientService;
	}

	public void setClientService(PersistentClientService clientService) {
		this.clientService = clientService;
	}

	private static void initApiKeyMap() {
    	apyKeyMap.put("withdemo", WebAnnotationFields.PROVIDER_WITH);
    }
	
	Logger logger = LogManager.getLogger(getClass());
	
	private Gson gson = null;

    
	@BeforeEach
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
    @SuppressWarnings("deprecation")
	public void storeApiKeysToMongo() throws ApplicationAuthenticationException, AnnotationMongoException{
    	
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
    	    	    	
    }

	protected AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	protected void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
    
}
