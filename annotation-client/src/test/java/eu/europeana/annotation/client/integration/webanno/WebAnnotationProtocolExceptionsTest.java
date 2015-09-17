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
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseWebAnnotationProtocolTest {

	
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

		ResponseEntity<String> response = getApiClient().createAnnotation(
				WRONG_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");

		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, null
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationWithoutCorruptedBody() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");

		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, CORRUPTED_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_WITHOUT_BLANK
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_WITH_WRONG_MOTIVATION
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationWithInvalidProvider() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Invalid provider!");
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, INVALID_PROVIDER
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
	@Test
	public void getWebannoAnnotationWithWrongIdentifier() {
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().getAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_NUMBER);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void createHistorypinAnnotationTagWithWrongIdentifierNumber() {
				
		exception.expect(Exception.class);
		exception.expectMessage("Identifier must not be null for the given provider!");
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, WRONG_IDENTIFIER_NUMBER
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	
	@Test
	public void createWebannoAnnotationTagByWrongTypeJsonld() {
		
		exception.expect(Exception.class);
		exception.expectMessage("Invalid request. Parameter value not supported!");

		ResponseEntity<String> response = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WRONG_ANNOTATION_TAG
				);
		
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	
	
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_NUMBER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongProvider() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_PROVIDER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifier() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("No annotation found with the given identifier!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_URL
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierUrl() { 
		
		exception.expect(Exception.class);
		exception.expectMessage("Identifier must have at least a provider and an identifier number!");
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, WRONG_IDENTIFIER_NUMBER
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws JsonParseException { 
		
		exception.expect(Exception.class);
		exception.expectMessage("Cannot parse body to annotation!");
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
		Annotation anno = createTestAnnotation();
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, CORRUPTED_UPDATE_JSON
				, TEST_USER_TOKEN
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
//	@Test
	public void updateWebannoAnnotationWithWrongUser() throws JsonParseException { 
		
		exception.expect(Exception.class);
		exception.expectMessage("The user is not authorized to perform the given action!");
		
		//implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				TEST_WSKEY
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, UPDATE_JSON
				, WRONG_USER_TOKEN
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
		
	@Test()
	public void updateWebannoAnnotationWithWrongWskey() throws JsonParseException { 
		
//		exception.expect(Exception.class);
//		exception.expectMessage("The user is not authorized to perform the given action!");
//		
		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation();
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				WRONG_WSKEY
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
