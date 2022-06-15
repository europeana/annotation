package eu.europeana.annotation.tests.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolExceptionsTest extends BaseAnnotationTest {

    private String get_LINK_JSON_WITHOUT_BLANK() {
      return START + get_LINK_CORE() + "\"motivation\":\"oa:linking\"," +  END;
    }
	
    private String get_CORRUPTED_JSON() {
      return START + get_LINK_CORE() + "\"motivation\",=\"oa:linking\"," + END;
    }
	
    private String get_LINK_JSON_WITH_WRONG_MOTIVATION() {
      return START + get_LINK_CORE() + "\"motiv\":\"oa:wrong\"," + END;
    }

    @Deprecated
    public String CORRUPTED_UPDATE_BODY =
    		"\"bodyText\":=,\"Buccin Trombone\"";

    public long WRONG_GENERATED_IDENTIFIER = -1;
    
    public String UNKNOWN_WSKEY = "invalid_wskey";
    
    public String INVALID_ANNO_TYPE = "taglink";
    
    public String INVALID_USER_TOKEN = "invalid_user_token";

	@Test
	public void createWebannoAnnotationWithoutBody() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(null, false, null);
		assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebAnnotationWithCorruptedBody() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotationByType(
			false, get_CORRUPTED_JSON(), null, null);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithoutBlanksInMotivation() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotationByType(
				true, get_LINK_JSON_WITHOUT_BLANK(), null, null);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		removeAnnotationManually(storedAnno.getIdentifier());
	}
	

	@Test
	public void createWebannoAnnotationLinkWithWrongMotivation() throws Exception {
		ResponseEntity<String> response = storeTestAnnotationByType(
				false, get_LINK_JSON_WITH_WRONG_MOTIVATION(), null, null
				);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	

	@Test
	public void getWebAnnotationWithWrongIdentifier() throws Exception {
		ResponseEntity<String> response = getAnnotation(
				AnnotationTestsConfiguration.getInstance().getApiKey(), WRONG_GENERATED_IDENTIFIER, false, null);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void getWebAnnotationWithWrongWskey() throws Exception {
		ResponseEntity<String> response = getAnnotation(
				UNKNOWN_WSKEY, 
				WRONG_GENERATED_IDENTIFIER,
				false, 
				null
				);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	public void createWebannoAnnotationByWrongAnnoTypeJsonld() throws Exception {
		ResponseEntity<String> response = storeTestAnnotationByType(
				false, get_TAG_JSON_BY_TYPE_JSONLD(), INVALID_ANNO_TYPE, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
    @Test
    public void createDescribingAnnoWithoutBodyLanguage() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WITHOUT_BODY_LANGUAGE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutType() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_TYPE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutStreetAddress() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
	@Test
	public void updateWebannoAnnotationWithWrongIdentifierNumber() throws Exception { 
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		ResponseEntity<String> response = updateAnnotation(
				WRONG_GENERATED_IDENTIFIER, requestBody, null);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
	@Test
	public void updateWebannoAnnotationWithCorruptedUpdateBody() throws Exception { 

        String CORRUPTED_UPDATE_JSON = 
            START + CORRUPTED_UPDATE_BODY + "," + "\"target\":" + "\"" + get_TAG_STANDARD_TEST_VALUE_TARGET() + "\"," + END;
		Annotation anno = createTestAnnotation(TAG_STANDARD, false, null);
		ResponseEntity<String> response = updateAnnotation(
				anno.getIdentifier(), CORRUPTED_UPDATE_JSON, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		removeAnnotationManually(anno.getIdentifier());
	}
		
	@Test
	public void updateWebAnnotationWithWrongUserToken() throws Exception { 
		Annotation anno = createTestAnnotation(TAG_STANDARD, false, USER_ADMIN);
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		ResponseEntity<String> response = updateAnnotation(
				anno.getIdentifier(), requestBody, USER_REGULAR);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		removeAnnotationManually(anno.getIdentifier());
	}
	
}
