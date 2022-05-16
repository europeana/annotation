package eu.europeana.annotation.web.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.service.AdminService;
//import eu.europeana.apikey.client.ValidationRequest;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;


/**
 * Unit test for the Web Annotation validation service
 * using API key client server.
 * 
 * @author GrafR
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/annotation-web-context.xml"
	})
public class AnnotationClientApiKeyValidationTest extends AnnotationTestObjectBuilder{

	@Resource
	private AdminService adminService;

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@Resource
	AnnotationConfiguration configuration;
	
	private final static String API_KEY = "ApiKey1";
//	private final static String API_METHOD = "read";
	private final static String NOT_EXISTING_API_KEY = "NotExistingApiKey";
	
    private static final Map<String, String> apyKeyMap = new HashMap<String, String>();
    
    //TODO: load apikeys from config files, add test api key
	private static void initApiKeyMap() {
    	apyKeyMap.put("withdemo", WebAnnotationFields.PROVIDER_WITH); 	
    }

	
	@BeforeEach
    public void setUp() throws Exception {
		initApiKeyMap();
    }
	
	
	//@Test
	public void testValidateClientApiKey() 
			throws MalformedURLException, IOException, ApplicationAuthenticationException {
						
        boolean validationRes = validateApiKey(API_KEY);

		assertTrue(validationRes);	
	}

	
	//@Test
	public void testValidateNotExistingClientApiKey() 
			throws MalformedURLException, IOException, ApplicationAuthenticationException {
								  
		boolean validationRes = validateApiKey(NOT_EXISTING_API_KEY);

		assertTrue(!validationRes);	
	}

	private boolean validateApiKey(String apiKey) throws ApplicationAuthenticationException {
//		ValidationRequest request = new ValidationRequest(
//        		configuration.getValidationAdminApiKey()
//        		, configuration.getValidationAdminSecretKey()
//        		, apiKey
//        		, configuration.getValidationApi()
//        		);
//        if (StringUtils.isNotBlank(API_METHOD)) request.setMethod(API_METHOD);
//		
//		boolean validationRes = getAdminService().validateApiKey(request, API_METHOD);
//		return validationRes;
		return false;
	}

	
    //@Test
    public void testAllRegisteredApiKeysUsingApiKeyClient() throws ApplicationAuthenticationException {
  	  	
  	    for (Map.Entry<String, String> entry : apyKeyMap.entrySet()) {
  	        boolean validationRes = validateApiKey(entry.getKey());
  			assertTrue(validationRes);	
  	    }
    }
	
		
}
