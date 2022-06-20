package eu.europeana.annotation.tests.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;


/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
public class WebNewAnnotationProtocolTest extends AbstractIntegrationTest { 

		
	@Test
	public void createAnnotation() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);

		validateResponse(response);
		
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
	}


	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
		validateResponse(response, HttpStatus.CREATED);
	}
	
	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), status);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		assertNotNull(storedAnno.getIdentifier());
	}
	
	@Test
	public void getAnnotation() throws Exception {
		
		ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD, true, null); 
		Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(createResponse);
		createdAnnotations.add(annotation.getIdentifier());
		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = getAnnotation(
				AnnotationTestsConfiguration.getInstance().getApiKey()
				, annotation.getIdentifier()
				, false
				, null
				);
		
		//validateResponse(response, HttpStatus.OK);
		Annotation storedAnno = AnnotationTestUtils.parseAndVerifyTestAnnotation(response, HttpStatus.OK);
		AnnotationTestUtils.validateOutputAgainstInput(storedAnno, annotation);
	}	
					
	@Test
	public void updateAnnotation() throws Exception {
						
//		store annotation
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);
		
		Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
		createdAnnotations.add(annotation.getIdentifier());
//		updated annotation value
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TEST_VALUE);
				
//		update annotation by identifier URL
		ResponseEntity<String> updateResponse = updateAnnotation(
				annotation.getIdentifier(), requestBody, null);
		
		Annotation updatedAnnotation = AnnotationTestUtils.parseAndVerifyTestAnnotationUpdate(updateResponse);
		
		assertEquals( HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(TAG_STANDARD_TEST_VALUE_BODY, updatedAnnotation.getBody().getValue());
		assertEquals(get_TAG_STANDARD_TEST_VALUE_TARGET(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), updatedAnnotation.getTarget().getHttpUri());
	}			
				
	@Test
	public void deleteAnnotation() throws Exception {
				
//		store annotation and retrieve its identifier URL
		ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD, true, null); 
		Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(createResponse);
		createdAnnotations.add(annotation.getIdentifier());
//		delete annotation by identifier URL
		ResponseEntity<String> response = deleteAnnotation(
				annotation.getIdentifier());
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
	
	/*
	 * For the below duplicate tests to pass please make sure that there are no annotations 
	 * stored in Solr that are the same as the ones defined in the test, prior to the test execution.
	 */
	@Test
	public void checkAnnotationDuplicatesCreateTranscriptions() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);	
	    createdAnnotations.add(storedAnno.getIdentifier());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateCaptions() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateSubtitles() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
		response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateTags() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(TAG_MINIMAL, true, null);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
		response = storeTestAnnotation(TAG_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void checkAnnotationDuplicatesCreateSemanticTags() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true, null);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
		response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
    public void checkAnnotationDuplicatesCreateLinkForContributing() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        
    }
	
	/*
	 * For this test to pass please comment out the validation part in the validateWebAnnotation() method
	 * in the BaseAnnotationValidator class, if the whitelists are not created
	 */
	@Disabled
	@Test
	public void checkAnnotationDuplicatesCreateObjectLinks() throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(LINK_MINIMAL, true, null);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		response = storeTestAnnotation(LINK_MINIMAL, true, null);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
		createdAnnotations.add(annotation1.getIdentifier());
		ResponseEntity<String> response2 = storeTestAnnotation(TRANSCRIPTION_MINIMAL_DUPLICATE_UPDATE, true, null);
		assertEquals(HttpStatus.CREATED, response2.getStatusCode());
		Annotation annotation2 = AnnotationTestUtils.parseAndVerifyTestAnnotation(response2);
		createdAnnotations.add(annotation2.getIdentifier());
		//updated annotation value
		String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
		//update annotation by identifier URL
		ResponseEntity<String> updateResponse = updateAnnotation(
				annotation2.getIdentifier(), requestBody, null);
		assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
	}
				
}
