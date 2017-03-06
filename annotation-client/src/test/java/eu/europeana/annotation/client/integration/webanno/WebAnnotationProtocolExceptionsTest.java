package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseWebAnnotationProtocolTest {

	
    public String LINK_JSON_WITHOUT_BLANK = 
    		START + LINK_CORE +
		    "\"motivation\":\"oa:linking\"," +	
		    END;
	
    public String CORRUPTED_JSON = 
    		START + LINK_CORE +
		    "\"motivation\",=\"oa:linking\"," +	
		    END;
	
    public String LINK_JSON_WITH_WRONG_MOTIVATION = 
    		START + LINK_CORE +
		    "\"motiv\":\"oa:wrong\"," +	
		    END;
	
    public String CORRUPTED_UPDATE_BODY =
    		"\"bodyText\":=,\"Buccin Trombone\"";

    public String CORRUPTED_UPDATE_JSON =
    		START +
    		CORRUPTED_UPDATE_BODY + "," + 
    		"\"target\":" + "\"" + TAG_STANDARD_TEST_VALUE_TARGET+ "\"," +
    		END;
    
    public String WRONG_GENERATED_IDENTIFIER = "-1";
    
    public String UNKNOWN_WSKEY = "invalid_wskey";
    
    public String INVALID_ANNO_TYPE = "taglink";
    
    public String INVALID_USER_TOKEN = "invalid_user_token";

    public String UNKNOWN_PROVIDER = "unknown_provider";

    public String UNKNOWN_PROVIDED_IDENTIFIER = "unknown_provided_identifier";
       
	@Test
	public void createWebAnnotationWithWrongWskey() throws IOException {
		
		String requestBody = getJsonStringInput(TAG_STANDARD);
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				UNKNOWN_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, requestBody
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, null
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebAnnotationWithCorruptedBody() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, CORRUPTED_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, LINK_JSON_WITHOUT_BLANK
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, LINK_JSON_WITH_WRONG_MOTIVATION
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebAnnotationForUnknownProvider() throws IOException {
		
		String requestBody = getJsonStringInput(TAG_STANDARD);
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, UNKNOWN_PROVIDER
				, null
				, requestBody
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
	@Test
	public void getWebAnnotationWithWrongIdentifier() {
		
		ResponseEntity<String> response = getApiClient().getAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void createWebannoAnnotationByWrongAnnoTypeJsonld() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, TAG_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, INVALID_ANNO_TYPE
				);
		
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	

	
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER
				, requestBody
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
//	@Test TODO: rewrite test
	public void updateWebAnnotationWithWrongProvider() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, UNKNOWN_PROVIDER
				, UNKNOWN_PROVIDED_IDENTIFIER
				, requestBody
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifier() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER
				, requestBody
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws JsonParseException, IOException { 
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
		Annotation anno = createTestAnnotation();
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, CORRUPTED_UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
//	@Test
	public void updateWebAnnotationWithWrongUserToken() throws JsonParseException, IOException { 
		
		//TODO: implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, requestBody
				, INVALID_USER_TOKEN
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
		
	@Test()
	public void updateWebannoAnnotationWithUnknownWskey() throws JsonParseException, IOException { 
		
		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				UNKNOWN_WSKEY
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, requestBody
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		//TODO:parse and verify message
		//System.out.println(response.getBody());
		
	}
	
		
}
