package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;


/**
 * This class aims at testing of different exceptions related to annotation methods.
 * @author GrafR
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationUpdateIT extends AbstractIntegrationTest {
	
    @Deprecated
    public String CORRUPTED_UPDATE_BODY =
    		"\"bodyText\":=,\"Buccin Trombone\"";
    public long WRONG_GENERATED_IDENTIFIER = -1;    
    public String INVALID_USER_TOKEN = "invalid_user_token";
    
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
            START + CORRUPTED_UPDATE_BODY + "," + "\"target\":" + "\"" +
            get_TAG_STANDARD_TEST_VALUE_TARGET(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + 
            "\"" + END;
		Annotation anno = createTestAnnotation(TAG_STANDARD, false, null);
		addToCreatedAnnotations(anno.getIdentifier());
		ResponseEntity<String> response = updateAnnotation(
				anno.getIdentifier(), CORRUPTED_UPDATE_JSON, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());		
	}
		
	@Test
	@Disabled("This test is successfull only when the authorization is enabled")
	public void updateWebAnnotationWithWrongUserToken() throws Exception { 
		Annotation anno = createTestAnnotation(TAG_STANDARD, false, USER_ADMIN);
		addToCreatedAnnotations(anno.getIdentifier());
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TEST_VALUE);
		ResponseEntity<String> response = updateAnnotation(
				anno.getIdentifier(), requestBody, USER_REGULAR);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

    @Test
    public void updateAnnotation() throws Exception {
        Annotation anno = createTestAnnotation(TAG_STANDARD, true, null);
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TEST_VALUE);
        ResponseEntity<String> response = updateAnnotation(
                anno.getIdentifier(), requestBody, null);
        Annotation updatedAnnotation = AnnotationTestUtils.parseAndVerifyTestAnnotationUpdate(response);
        
        assertEquals( HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TAG_STANDARD_TEST_VALUE_BODY, updatedAnnotation.getBody().getValue());
        assertEquals(get_TAG_STANDARD_TEST_VALUE_TARGET(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), updatedAnnotation.getTarget().getHttpUri());
        
        addToCreatedAnnotations(anno.getIdentifier());
        //TODO: search annotation in solr and verify body and target values.
    }

    /*
     * The tests for the other scenario types would be very similar to this one, therefore
     * we do not create all of them, since a lot of the code would be dulpicated, and
     * the duplications are already tested in the browser.
     */
    @Test
    public void checkAnnotationDuplicatesUpdateTranscriptions() throws Exception {
        ResponseEntity<String> response1 = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        Annotation annotation1 = AnnotationTestUtils.parseAndVerifyTestAnnotation(response1);
        addToCreatedAnnotations(annotation1.getIdentifier());
        ResponseEntity<String> response2 = storeTestAnnotation(TRANSCRIPTION_MINIMAL_DUPLICATE_UPDATE, true, null);
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());
        Annotation annotation2 = AnnotationTestUtils.parseAndVerifyTestAnnotation(response2);
        addToCreatedAnnotations(annotation2.getIdentifier());
        //updated annotation value
        String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
        //update annotation by identifier URL
        ResponseEntity<String> updateResponse = updateAnnotation(
                annotation2.getIdentifier(), requestBody, null);
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }

}
