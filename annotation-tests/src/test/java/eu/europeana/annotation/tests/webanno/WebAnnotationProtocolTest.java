package eu.europeana.annotation.tests.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;


/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolTest extends AbstractIntegrationTest { 

		
	@Test
	public void createWebannoAnnotationTag() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);

		AnnotationTestUtils.validateResponse(response);
		
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		createdAnnotations.add(storedAnno.getIdentifier());
	}
		
	@Test
	public void createWebannoAnnotationLink() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);
		
		ResponseEntity<String> response = storeTestAnnotationByType(
		    false, requestBody, null, null);
		
		AnnotationTestUtils.validateResponse(response);
		
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
	}
		

	@Test
	public void createWebannoAnnotationTagByTypeJsonld() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotationByType(
				true, get_TAG_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
		
		AnnotationTestUtils.validateResponse(response);
		
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
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
	 * @throws Exception 
	 */
	@Test
	public void createWebannoAnnotationTagForValidation() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotationByType(
			true, get_TAG_JSON_VALIDATION(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
		
		AnnotationTestUtils.validateResponseForTrimming(response);
		
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
	}
	
	
	@Test
	public void createWebannoAnnotationLinkByTypeJsonld() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotationByType(
				true, get_LINK_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.LINK, null);
		
		AnnotationTestUtils.validateResponse(response);
		
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    createdAnnotations.add(storedAnno.getIdentifier());
	}
		

	@Test
	public void getAnnotation() throws Exception {
		
		ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD, true, null); 
		Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(createResponse);
		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = getAnnotation(
				AnnotationTestsConfiguration.getInstance().getApiKey(), annotation.getIdentifier(), false, null
				);
		
		//validateResponse(response, HttpStatus.OK);
		Annotation storedAnno = AnnotationTestUtils.parseAndVerifyTestAnnotation(response, HttpStatus.OK);
		AnnotationTestUtils.validateOutputAgainstInput(storedAnno, annotation);
		
		createdAnnotations.add(storedAnno.getIdentifier());
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
		
		createdAnnotations.add(anno.getIdentifier());
		//TODO: search annotation in solr and verify body and target values.
	}
	
				
	@Test
	public void deleteAnnotation() throws Exception {
				
//		store annotation and retrieve its identifier URL
		Annotation anno = createTestAnnotation(TAG_STANDARD, true, null);
		
//		delete annotation by identifier URL
		ResponseEntity<String> response = deleteAnnotation(anno.getIdentifier());
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		createdAnnotations.add(anno.getIdentifier());
	}
			
				
}
