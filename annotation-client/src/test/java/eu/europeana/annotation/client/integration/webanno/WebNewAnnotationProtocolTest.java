package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;


/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
public class WebNewAnnotationProtocolTest extends BaseWebAnnotationProtocolTest { 

		
	@Test
	public void createAnnotation() throws JsonParseException, IOException {
		
		ResponseEntity<String> response = storeNewTestAnnotation();

		validateResponse(response);		
	}


	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
		validateResponse(response, HttpStatus.CREATED);
	}
	
	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), status);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getAnnotationId());
		assertTrue(storedAnno.getAnnotationId().toHttpUrl().startsWith("http://"));
	}
	
	@Test
	public void getAnnotation() throws JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		ResponseEntity<String> createResponse = storeNewTestAnnotation(); 
		Annotation annotation = parseAndVerifyTestAnnotation(createResponse);
		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = getApiClient().getAnnotation(
				getApiKey()
				, annotation.getAnnotationId().getIdentifier()
				);
		
		//validateResponse(response, HttpStatus.OK);
		Annotation storedAnno = parseAndVerifyTestAnnotation(response, HttpStatus.OK);
		validateOutputAgainstInput(storedAnno, annotation);
	}
	
					
	@Test
	public void updateAnnotation() throws JsonParseException, IOException {
						
//		store annotation
		ResponseEntity<String> response = storeNewTestAnnotation();
		
		Annotation annotation = parseAndVerifyTestAnnotation(response);
		
//		updated annotation value
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
				
//		update annotation by identifier URL
		ResponseEntity<String> updateResponse = getApiClient().updateAnnotation(
				getApiKey()
				, annotation.getAnnotationId().getIdentifier()
				, requestBody
				, TEST_USER_TOKEN
				);
		Annotation updatedAnnotation = parseAndVerifyTestAnnotationUpdate(updateResponse);
		
		assertEquals( HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(TAG_STANDARD_TEST_VALUE_BODY, updatedAnnotation.getBody().getValue());
		assertEquals(TAG_STANDARD_TEST_VALUE_TARGET, updatedAnnotation.getTarget().getHttpUri());
	}
			
				
	@Test
	public void deleteAnnotation() throws JsonParseException, IOException {
				
//		store annotation and retrieve its identifier URL
		ResponseEntity<String> createResponse = storeNewTestAnnotation(); 
		Annotation annotation = parseAndVerifyTestAnnotation(createResponse);
		
//		delete annotation by identifier URL
		ResponseEntity<String> response = getApiClient().deleteAnnotation(
				getApiKey(), annotation.getAnnotationId().getIdentifier(), TEST_USER_TOKEN, null
				);
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
			
				
}
