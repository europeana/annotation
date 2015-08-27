package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationRestProtocolExceptionsTest extends WebAnnotationRestProtocolTest {

	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
    public String LINK_JSON_WITHOUT_BLANK = 
    		START + LINK_CORE +
		    "\"motivation\":\"oa:Linking\"," +	
		    END;
	
    public String CORRUPTED_JSON = 
    		START + LINK_CORE +
		    "\"motivation\",=\"oa:Linking\"," +	
		    END;
	
    public String LINK_JSON_WITH_WRONG_MOTIVATION = 
    		START + LINK_CORE +
		    "\"motiv\":\"oa:Wrong\"," +	
		    END;
	
    public String CORRUPTED_UPDATE_BODY =
    		"\"body\":=,\"Buccin Trombone\"";

    public String CORRUPTED_UPDATE_JSON =
    		START +
    		CORRUPTED_UPDATE_BODY + "," + 
    		UPDATE_TARGET +
    		END;
    
    public String WRONG_IDENTIFIER_NUMBER = "-1";
    
    public String WRONG_IDENTIFIER_NUMBER_URL = "http://data.europeana.eu/annotation/webanno/-1";
    
    public String WRONG_IDENTIFIER_PROVIDER_URL = "http://data.europeana.eu/annotation/wrong/496";
    
    public String WRONG_IDENTIFIER_URL = "http://annotation/wrong";
    
    public String WRONG_WSKEY = "wskey";
    
    public String WRONG_ANNOTATION_TAG = "taglink";
    
    public String WRONG_USER_TOKEN = "guest";

    public String INVALID_PROVIDER = "provider";

    
	@Test
	public void createWebannoAnnotationWithoutWrongWskey() {
		
		exception.expect(Exception.class);
		exception.expectMessage("The user is not authorized to perform the given action!");

		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				WRONG_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");

		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, null
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	

	@Test
	public void createWebannoAnnotationWithoutCorruptedBody() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");

		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, CORRUPTED_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() {
		
		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_WITHOUT_BLANK
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_WITH_WRONG_MOTIVATION
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	

	@Test
	public void createWebannoAnnotationWithInvalidProvider() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Invalid provider!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, INVALID_PROVIDER
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
		
	@Test
	public void getWebannoAnnotationWithWrongIdentifier() {
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().getAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_NUMBER
				, false
				);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
		
	@Test
	public void createHistorypinAnnotationTagWithWrongIdentifierNumber() {
				
		exception.expect(Exception.class);
		exception.expectMessage("Identifier must not be null for the given provider!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, WRONG_IDENTIFIER_NUMBER
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
	
	@Test
	public void createWebannoAnnotationTagByWrongTypeJsonld() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Invalid request. Parameter value not supported!");

		ResponseEntity<String> response = getAnnotationJsonApi().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WRONG_ANNOTATION_TAG
				);
		
		assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
	}
	
	
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, WRONG_IDENTIFIER_NUMBER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongProvider() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, WRONG_IDENTIFIER_PROVIDER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifier() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, WRONG_IDENTIFIER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierUrl() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("Identifier must have at least a provider and an identifier number!");
		
		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, WRONG_IDENTIFIER_NUMBER
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}
	
		
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
		ResponseEntity<String> storedResponse = storeTestAnnotation();
		String identifier = extractAnnotationIdFromAnnotationJson(storedResponse);

		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, identifier
				, CORRUPTED_UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	
		
//	@Test
	public void updateWebannoAnnotationWithWrongUser() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("The user is not authorized to perform the given action!");
		
		//implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its identifier URL
		 */
		ResponseEntity<String> storedResponse = storeTestAnnotation();
		String identifier = extractAnnotationIdFromAnnotationJson(storedResponse);

		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				TEST_WSKEY
				, identifier
				, UPDATE_JSON
				, WRONG_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongWskey() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("The user is not authorized to perform the given action!");
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
		ResponseEntity<String> storedResponse = storeTestAnnotation();
		String identifier = extractAnnotationIdFromAnnotationJson(storedResponse);

		ResponseEntity<String> response = getAnnotationJsonApi().updateAnnotation(
				WRONG_WSKEY
				, identifier
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
		
}
