package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.definitions.model.Annotation;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseWebAnnotationTest {

    private String get_LINK_JSON_WITHOUT_BLANK() {
      return START + get_LINK_CORE(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motivation\":\"oa:linking\"," +  END;
    }
	
    private String get_CORRUPTED_JSON() {
      return START + get_LINK_CORE(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motivation\",=\"oa:linking\"," + END;
    }
	
    private String get_LINK_JSON_WITH_WRONG_MOTIVATION() {
      return START + get_LINK_CORE(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motiv\":\"oa:wrong\"," + END;
    }

    @Deprecated
    public String CORRUPTED_UPDATE_BODY =
    		"\"bodyText\":=,\"Buccin Trombone\"";

    public long WRONG_GENERATED_IDENTIFIER = -1;
    
    public String UNKNOWN_WSKEY = "invalid_wskey";
    
    public String INVALID_ANNO_TYPE = "taglink";
    
    public String INVALID_USER_TOKEN = "invalid_user_token";

	@Test
	public void createWebannoAnnotationWithoutBody() {
		
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
			null, null, null);
		
		assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebAnnotationWithCorruptedBody() {
		
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
			false, get_CORRUPTED_JSON(), null, null);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() throws JsonParseException {
		
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
				true, get_LINK_JSON_WITHOUT_BLANK(), null, null);
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() {
		
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
				false, get_LINK_JSON_WITH_WRONG_MOTIVATION(), null, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void getWebAnnotationWithWrongIdentifier() {
		
		ResponseEntity<String> response = getApiProtocolClient().getAnnotation(
				getApiKey()
				, WRONG_GENERATED_IDENTIFIER);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void getWebAnnotationWithWrongWskey() throws IOException {
		
		ResponseEntity<String> response = getApiProtocolClient().getAnnotation(
				UNKNOWN_WSKEY, 
				WRONG_GENERATED_IDENTIFIER
				);
		
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void createWebannoAnnotationByWrongAnnoTypeJsonld() {
		
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
				false, get_TAG_JSON_BY_TYPE_JSONLD(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), INVALID_ANNO_TYPE, null);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
    @Test
    public void createDescribingAnnoWithoutBodyLanguage() throws IOException { 
        
        String requestBody = getJsonStringInput(DESCRIBING_WITHOUT_BODY_LANGUAGE);
        
        ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
            false, requestBody, null, null);
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutType() throws IOException { 
        
        String requestBody = getJsonStringInput(TAGGING_BODY_ADDRESS_NO_TYPE);
        
        ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
            false, requestBody, null, null);
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutStreetAddress() throws IOException { 
        
        String requestBody = getJsonStringInput(TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS);
        
        ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
            false, requestBody, null, null);
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() throws IOException { 
		
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiProtocolClient().updateAnnotation(
				WRONG_GENERATED_IDENTIFIER, requestBody, null);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws JsonParseException, IOException { 
		
		/**
		 * store annotation and retrieve its identifier URL
		 */
	    
        String CORRUPTED_UPDATE_JSON = 
            START + CORRUPTED_UPDATE_BODY + "," + "\"target\":" + "\"" + get_TAG_STANDARD_TEST_VALUE_TARGET(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"," + END;
	  
		Annotation anno = createTestAnnotation(TAG_STANDARD, null);
        createdAnnotations.add(anno.getIdentifier());
		ResponseEntity<String> response = getApiProtocolClient().updateAnnotation(
				anno.getIdentifier(), CORRUPTED_UPDATE_JSON, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
		
	@Test
	public void updateWebAnnotationWithWrongUserToken() throws JsonParseException, IOException { 
		
		//TODO: implement when authentication/authorization is available

		/**
		 * store annotation and retrieve its id
		 */
		Annotation anno = createTestAnnotation(TAG_STANDARD, AnnotationApiConnection.USER_ADMIN);
        createdAnnotations.add(anno.getIdentifier());

		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		
		ResponseEntity<String> response = getApiProtocolClient().updateAnnotation(
				anno.getIdentifier(), requestBody, AnnotationApiConnection.USER_REGULAR);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		
	}
	
		
		
}
