package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;


/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
public class WebNewAnnotationProtocolTest extends BaseWebAnnotationTest { 

		
	@Test
	public void createAnnotation() throws JsonParseException, IOException {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD);

		validateResponse(response);
		
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
	    removeAnnotation(storedAnno.getIdentifier());
	}


//	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
//		validateResponse(response, HttpStatus.CREATED);
//	}
//	
//	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
//		assertNotNull(response.getBody());
//		assertEquals(response.getStatusCode(), status);
//		
//		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
//		assertNotNull(storedAnno.getIdentifier());
//	}
	
	@Test
	public void getAnnotation() throws JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD); 
		Annotation annotation = parseAndVerifyTestAnnotation(createResponse);
		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = getApiProtocolClient().getAnnotation(
				getApiKey()
				, annotation.getIdentifier()
				);
		
		//validateResponse(response, HttpStatus.OK);
		Annotation storedAnno = parseAndVerifyTestAnnotation(response, HttpStatus.OK);
		validateOutputAgainstInput(storedAnno, annotation);
		
		removeAnnotation(storedAnno.getIdentifier());
	}	
					
	@Test
	public void updateAnnotation() throws JsonParseException, IOException {
						
//		store annotation
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD);
		
		Annotation annotation = parseAndVerifyTestAnnotation(response);
		
//		updated annotation value
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
				
//		update annotation by identifier URL
		ResponseEntity<String> updateResponse = getApiProtocolClient().updateAnnotation(
				annotation.getIdentifier(), requestBody, null);
		
		Annotation updatedAnnotation = parseAndVerifyTestAnnotationUpdate(updateResponse);
		
		assertEquals( HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(TAG_STANDARD_TEST_VALUE_BODY, updatedAnnotation.getBody().getValue());
		assertEquals(get_TAG_STANDARD_TEST_VALUE_TARGET(), updatedAnnotation.getTarget().getHttpUri());
		
		removeAnnotation(annotation.getIdentifier());
	}			
				
	@Test
	public void deleteAnnotation() throws JsonParseException, IOException {
				
//		store annotation and retrieve its identifier URL
		ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD); 
		Annotation annotation = parseAndVerifyTestAnnotation(createResponse);
		
//		delete annotation by identifier URL
		ResponseEntity<String> response = getApiProtocolClient().deleteAnnotation(
				annotation.getIdentifier());
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		removeAnnotation(annotation.getIdentifier());
	}
	
	/*
	 * For the below duplicate tests to pass please make sure that there are no annotations 
	 * stored in Solr that are the same as the ones defined in the test, prior to the test execution.
	 */
	@Test
	public void checkAnnotationDuplicatesCreateTranscriptions() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL);
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);	       
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(TRANSCRIPTION_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateCaptions() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL);
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(CAPTION_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateSubtitles() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		response = storeTestAnnotation(SUBTITLE_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateTags() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(TAG_MINIMAL);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		response = storeTestAnnotation(TAG_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateSemanticTags() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
    public void checkAnnotationDuplicatesCreateLinkForContributing() throws JsonParseException, IOException {
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
        response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        
        removeAnnotation(storedAnno.getIdentifier());
    }
	
	/*
	 * For this test to pass please comment out the validation part in the validateWebAnnotation() method
	 * in the BaseAnnotationValidator class, if the whitelists are not created
	 */
	@Disabled
	@Test
	public void checkAnnotationDuplicatesCreateObjectLinks() throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(LINK_MINIMAL);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(LINK_MINIMAL);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	/*
	 * The tests for the other scenario types would be very similar to this one, therefore
	 * we do not create all of them, since a lot of the code would be dulpicated, and
	 * the duplications are already tested in the browser.
	 */
	@Test
	public void checkAnnotationDuplicatesUpdateTranscriptions() throws JsonParseException, IOException {
		ResponseEntity<String> response1 = storeTestAnnotation(TRANSCRIPTION_MINIMAL);
		assertEquals(HttpStatus.CREATED, response1.getStatusCode());
		Annotation annotation1 = parseAndVerifyTestAnnotation(response1);
		ResponseEntity<String> response2 = storeTestAnnotation(TRANSCRIPTION_MINIMAL_DUPLICATE_UPDATE);
		assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		Annotation annotation2 = parseAndVerifyTestAnnotation(response2);
		//updated annotation value
		String requestBody = getJsonStringInput(TRANSCRIPTION_MINIMAL);
		//update annotation by identifier URL
		ResponseEntity<String> updateResponse = getApiProtocolClient().updateAnnotation(
				annotation2.getIdentifier(), requestBody, null);
		assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
		
		removeAnnotation(annotation1.getIdentifier());
		removeAnnotation(annotation2.getIdentifier());
	}
				
}
