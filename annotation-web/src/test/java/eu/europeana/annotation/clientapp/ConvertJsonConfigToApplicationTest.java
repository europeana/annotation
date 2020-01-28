package eu.europeana.annotation.clientapp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.service.authentication.mock.MockAuthenticationServiceImpl;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;

/**
 * This class implements conversion from annotation api key to client application.
 */
@ExtendWith(SpringExtension.class)
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

	Logger logger = LogManager.getLogger(getClass());
	
	private Gson gson = null;

    
	@BeforeEach
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

		@SuppressWarnings("deprecation")
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
