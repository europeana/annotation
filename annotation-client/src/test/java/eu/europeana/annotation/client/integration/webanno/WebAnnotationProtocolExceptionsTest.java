package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.definitions.model.Annotation;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseWebAnnotationProtocolTest {

    @Deprecated	
    public String LINK_JSON_WITHOUT_BLANK = 
    		START + LINK_CORE +
		    "\"motivation\":\"oa:linking\"," +	
		    END;
	
    @Deprecated
    public String CORRUPTED_JSON = 
    		START + LINK_CORE +
		    "\"motivation\",=\"oa:linking\"," +	
		    END;
	
    @Deprecated
    public String LINK_JSON_WITH_WRONG_MOTIVATION = 
    		START + LINK_CORE +
		    "\"motiv\":\"oa:wrong\"," +	
		    END;
    @Deprecated
    public String CORRUPTED_UPDATE_BODY =
    		"\"bodyText\":=,\"Buccin Trombone\"";

    @Deprecated
    public String CORRUPTED_UPDATE_JSON =
    		START +
    		CORRUPTED_UPDATE_BODY + "," + 
    		"\"target\":" + "\"" + TAG_STANDARD_TEST_VALUE_TARGET+ "\"," +
    		END;
    public String WRONG_GENERATED_IDENTIFIER = "-1";
    
    public String UNKNOWN_WSKEY = "invalid_wskey";
    
    public String INVALID_ANNO_TYPE = "taglink";
    
    public String INVALID_USER_TOKEN = "invalid_user_token";

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
			null, null, null);
		
		assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebAnnotationWithCorruptedBody() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
			false, CORRUPTED_JSON, null, null);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				true, LINK_JSON_WITHOUT_BLANK, null, null);
		
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				false, LINK_JSON_WITH_WRONG_MOTIVATION, null, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void getWebAnnotationWithWrongIdentifier() {
		
		ResponseEntity<String> response = getApiClient().getAnnotation(
				getApiKey()
				, WRONG_GENERATED_IDENTIFIER);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void getWebAnnotationWithWrongWskey() throws IOException {
		
		ResponseEntity<String> response = getApiClient().getAnnotation(
				UNKNOWN_WSKEY, 
				WRONG_GENERATED_IDENTIFIER
				);
		
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
	@Test
	public void createWebannoAnnotationByWrongAnnoTypeJsonld() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				false, TAG_JSON_BY_TYPE_JSONLD, INVALID_ANNO_TYPE, null);
		
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	

	
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				WRONG_GENERATED_IDENTIFIER, requestBody, null);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebannoAnnotationWithWrongIdentifier() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				WRONG_GENERATED_IDENTIFIER, requestBody, null);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws JsonParseException, IOException { 
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
		Annotation anno = createTestAnnotation(TAG_STANDARD, null);
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				anno.getAnnotationId().getIdentifier(), CORRUPTED_UPDATE_JSON, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebAnnotationWithWrongUserToken() throws JsonParseException, IOException { 
		
		//TODO: implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation(TAG_STANDARD, AnnotationApiConnection.USER_ADMIN);
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				anno.getAnnotationId().getIdentifier(), requestBody, AnnotationApiConnection.USER_REGULAR);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
		
		
}
