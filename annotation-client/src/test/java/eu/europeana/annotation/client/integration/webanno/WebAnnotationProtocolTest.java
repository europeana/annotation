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
public class WebAnnotationProtocolTest extends BaseWebAnnotationProtocolTest { 

		
	@Test
	public void createWebannoAnnotationTag() throws JsonParseException, IOException {
		
		ResponseEntity<String> response = storeTestAnnotation();

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
	
	protected void validateResponseForTrimming(ResponseEntity<String> response) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getBody());
		assertTrue(storedAnno.getBody().getValue().length() == BODY_VALUE_AFTER_TRIMMING.length());
	}
	
	@Test
	public void createWebannoAnnotationLink() throws JsonParseException, IOException {
		
		String requestBody = getJsonStringInput(LINK_STANDARD);
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				false, requestBody, null);
		
		validateResponse(response);
	}
		

	@Test
	public void createWebannoAnnotationTagByTypeJsonld() throws JsonParseException {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				true, TAG_JSON_BY_TYPE_JSONLD, WebAnnotationFields.TAG);
		
		validateResponse(response);
	}
	
	
	/**
	 * {
     *     "@context": "http://www.europeana.eu/annotation/context.jsonld",
     *     "@type": "oa:Annotation",
     *     "motivation": "oa:tagging",
     *     "annotatedBy": {
     *         "@id": "https://www.historypin.org/en/person/55376/",
     *         "@type": "foaf:Person",
     *         "name": "John Smith"
     *     },
     *     "annotatedAt": "2015-02-27T12:00:43Z",
     *     "serializedAt": "2015-02-28T13:00:34Z",
     *     "serializedBy": "http://www.historypin.org",
     *     "body": " überhaupt ",
     *     "target": "http://data.europeana.eu/item/123/xyz",
     *     "oa:equivalentTo": "https://www.historypin.org/en/item/456"
     * }
	 * @throws JsonParseException
	 */
	@Test
	public void createWebannoAnnotationTagForValidation() throws JsonParseException {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
			true, TAG_JSON_VALIDATION, WebAnnotationFields.TAG);
		
		validateResponseForTrimming(response);
	}
	
	
	@Test
	public void createWebannoAnnotationLinkByTypeJsonld() throws JsonParseException {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				true, LINK_JSON_BY_TYPE_JSONLD, WebAnnotationFields.LINK);
		
		validateResponse(response);
	}
		

	@Test
	public void getAnnotation() throws JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		ResponseEntity<String> createResponse = storeTestAnnotation(); 
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
		Annotation anno = createTestAnnotation();
		
//		updated annotation value
		String requestBody = getJsonStringInput(TAG_STANDARD_TEST_VALUE);
				
//		update annotation by identifier URL
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				anno.getAnnotationId().getIdentifier(), requestBody);
		Annotation updatedAnnotation = parseAndVerifyTestAnnotationUpdate(response);
		
		assertEquals( HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(TAG_STANDARD_TEST_VALUE_BODY, updatedAnnotation.getBody().getValue());
		assertEquals(TAG_STANDARD_TEST_VALUE_TARGET, updatedAnnotation.getTarget().getHttpUri());
		
		//TODO: search annotation in solr and verify body and target values.
	}
	
				
	@Test
	public void deleteAnnotation() throws JsonParseException, IOException {
				
//		store annotation and retrieve its identifier URL
		Annotation anno = createTestAnnotation();
		
//		delete annotation by identifier URL
		ResponseEntity<String> response = getApiClient().deleteAnnotation(
				anno.getAnnotationId().getIdentifier());
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
			
				
}
