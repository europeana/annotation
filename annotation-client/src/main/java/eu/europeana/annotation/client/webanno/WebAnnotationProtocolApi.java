package eu.europeana.annotation.client.webanno;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;

public interface WebAnnotationProtocolApi {

	/**
	 * This method creates annotation describing it in body JSON string and
	 * providing it with associated wskey, provider name and identifier.
	 * When motivation is not given in JSON - annoType must be set up.
	 * @param wskey
	 * @param identifier
	 * @param indexOnCreate
	 * @param requestBody Contains the body JSON string
	 * @param userToken
	 * @param annoType
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> createAnnotation(Boolean indexOnCreate, 
			String requestBody, String annoType);
	
	public ResponseEntity<String> createAnnotation(
			String requestBody, String annoType);
	
	/**
	 * This method creates test annotation report object
	 * 
	 * @param apiKey
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */	
	public ResponseEntity<String> createAnnotationReport(
			String wskey, String identifier, String userToken);
	
	/**
	 * This method retrieves moderation report summary
	 * 
	 * @param apiKey
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */	
	public ResponseEntity<String> getModerationReport(
			String wskey, String identifier, String userToken);
	
	/**
	 * This method creates annotation describing it in body JSON string and
	 * providing it with associated wskey, provider name and identifier.
	 * When motivation is not given in JSON - annoType must be set up.
	 * @param wskey
	 * @param identifier
	 * @param indexOnCreate
	 * @param requestBody Contains the body JSON string
	 * @param userToken
	 * @param annoType
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> createTag(
			Boolean indexOnCreate, 
			String requestBody);
	
	
	/**
	 * This method deletes annotation by the given identifier
	 * @param wskey
	 * @param identifier - part of annotation id 
	 * @param userToken
	 * @return response entity containing headers and status code.
	 */
	public ResponseEntity<String> deleteAnnotation(
			String identifier);
	
	/**
	 * This method retrieves annotation by the given provider and identifier.
	 * @param wskey
	 * @param identifier
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String identifier);
	
	/**
	 * This method updates annotation by the given update string in JSON format
	 * @param wskey
	 * @param identifier - part of the annotationId 
	 * @param requestBody
	 * @param userToken
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> updateAnnotation(
			String identifier, String requestBody);


	/**
	 * Parses the body of the http response into the corresponding Annotation object
	 * @param response
	 * @return
	 * @throws JsonParseException
	 */
	public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException;

		
	public ResponseEntity<String> uploadAnnotations(String annotations, Boolean indexOnCreate);


}
