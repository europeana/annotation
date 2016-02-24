package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationFields;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseWebAnnotationProtocolTest {

	
//	@Rule
//	public ExpectedException exception = ExpectedException.none();
	
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
    		"\"body\":=,\"Buccin Trombone\"";

    public String CORRUPTED_UPDATE_JSON =
    		START +
    		CORRUPTED_UPDATE_BODY + "," + 
    		UPDATE_TARGET +
    		END;
    
    public String WRONG_GENERATED_IDENTIFIER = "-1";
    
    
    
//    public String WRONG_IDENTIFIER_URL = "http://annotation/wrong";
    
    public String UNKNOWN_WSKEY = "invalid_wskey";
    
    public String INVALID_ANNO_TYPE = "taglink";
    
    public String INVALID_USER_TOKEN = "invalid_user_token";

    public String UNKNOWN_PROVIDER = "unknown_provider";

    public String UNKNOWN_PROVIDED_IDENTIFIER = "unknown_provided_identifier";
       
	@Test
	public void createWebAnnotationWithWrongWskey() {
		
//		exception.expect(Exception.class);
//		exception.expectMessage("The user is not authorized to perform the given action!");

		ResponseEntity<String> response = getApiClient().createAnnotation(
				UNKNOWN_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Cannot parse body to annotation!");

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
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Cannot parse body to annotation!");

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
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Cannot parse body to annotation!");
		
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
	public void createWebAnnotationForUnknownProvider() {
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Invalid provider!");
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, UNKNOWN_PROVIDER
				, null
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
	@Test
	public void getWebAnnotationWithWrongIdentifier() {
		
//		exception.expect(Exception.class);
//		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().getAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
//TODO: correct or remove invalid usecase as we cannot/don't verify historypin identifiers		
//	@Test
	public void createHistorypinTagWithWrongIdentifierNumber() {
				
//		exception.expect(Exception.class);
//		exception.expectMessage("Identifier must not be null for the given provider!");
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, UNKNOWN_PROVIDED_IDENTIFIER
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	
	@Test
	public void createWebannoAnnotationByWrongAnnoTypeJsonld() {
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Invalid request. Parameter value not supported!");

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
	public void updateWebannoAnnotationWithWrongIdentifierNumber() { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
//	@Test
	public void updateWebAnnotationWithWrongProvider() { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, UNKNOWN_PROVIDER
				, UNKNOWN_PROVIDED_IDENTIFIER
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifier() { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_GENERATED_IDENTIFIER
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
//TODO: invalid testcase, check if can be converted to a valid one		
//	@Test
//	public void updateWebannoAnnotationWithWrongIdentifierUrl() { 
//		
////		exception.expect(Exception.class);
////		exception.expectMessage("Identifier must have at least a provider and an identifier number!");
//		
//		ResponseEntity<String> response = getApiClient().updateAnnotation(
//				TEST_WSKEY
//				, WebAnnotationFields.PROVIDER_WEBANNO
//				, WRONG_IDENTIFIER_NUMBER
//				, UPDATE_JSON
//				, TEST_USER_TOKEN
//				);
//		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//	}
	
		
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws JsonParseException { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("Cannot parse body to annotation!");
		
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
	public void updateWebAnnotationWithWrongUserToken() throws JsonParseException { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("The user is not authorized to perform the given action!");
		
		//implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, UPDATE_JSON
				, INVALID_USER_TOKEN
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
		
	@Test()
	public void updateWebannoAnnotationWithUnknownWskey() throws JsonParseException { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("The user is not authorized to perform the given action!");
//		
		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				UNKNOWN_WSKEY
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		//TODO:parse and verify message
		//System.out.println(response.getBody());
		
	}
	
		
}
