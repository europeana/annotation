package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
public class WebAnnotationProtocolTest extends BaseWebAnnotationProtocolTest { 

		
	@Test
	public void createWebannoAnnotationTag() {
		
		ResponseEntity<String> response = storeTestAnnotation();
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	
	@Test
	public void createWebannoAnnotationLink() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
		

	@Test
	public void createWebannoAnnotationTagByTypeJsonld() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WebAnnotationFields.TAG
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	
	@Test
	public void createWebannoAnnotationLinkByTypeJsonld() {
		
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WebAnnotationFields.LINK
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
		

	@Test
	public void getAnnotation() throws JsonParseException {
		
		getAnnotationWorkflow();
	}

	
	/**
	 * Parameter isJsonld makes a difference between the two 
	 * getAnnotation and getAnnotationJsonld methods.
	 * @param isJsonld
	 * @throws JsonParseException 
	 */
	protected void getAnnotationWorkflow() throws JsonParseException {
		
		ResponseEntity<String> createResponse = storeTestAnnotation(); 
		Annotation annotation = parseAndVerifyTestAnnotation(createResponse);
		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = getApiClient().getAnnotation(
				getApiKey()
				, WebAnnotationFields.PROVIDER_WEBANNO
				, annotation.getAnnotationId().getIdentifier()
				);
		
		assertNotNull(response.getBody());
		//TODO: Quick sting based check, still not reliable implementation. Needs to implement equalsContent in Annotation objects. 
		assertEquals(createResponse.getBody(), response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	
					
	@Test
	public void updateAnnotation() throws JsonParseException {
				
		/**
		 * store annotation
		 */
		Annotation anno = createTestAnnotation();
		/**
		 * update annotation by identifier URL
		 */
		ResponseEntity<String> response = getApiClient().updateAnnotation(
				getApiKey()
				, anno.getAnnotationId().getProvider()
				, anno.getAnnotationId().getIdentifier()
				, UPDATE_JSON
				, TEST_USER_TOKEN
				);
		
		assertNotNull(response.getBody());
		assertTrue(response.getBody().contains(UPDATE_BODY));
		assertTrue(response.getBody().contains(UPDATE_TARGET));
		assertEquals( HttpStatus.OK, response.getStatusCode());
	}
			
				
	@Test
	public void deleteAnnotation() throws JsonParseException {
				
		/**
		 * store annotation and retrieve its identifier URL
		 */
		Annotation anno = createTestAnnotation();
		
		/**
		 * delete annotation by identifier URL
		 */
		ResponseEntity<String> response = getApiClient().deleteAnnotation(
				getApiKey(), anno.getAnnotationId().getProvider(),
				anno.getAnnotationId().getIdentifier(), TEST_USER_TOKEN, null
				);
		
	//	assertTrue(response.getBody().equals(""));
	//	System.out.println(response.getBody());
		log.debug("Response body: " + response.getBody());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
			
				
}
