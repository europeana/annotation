package eu.europeana.annotation.client.webanno;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;

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
	public ResponseEntity<String> createAnnotation(
			String wskey, String identifier, Boolean indexOnCreate, 
			String requestBody, String userToken, String annoType);
	
	public ResponseEntity<String> createAnnotation(
			String wskey, String identifier, 
			String requestBody, String userToken, String annoType);
	
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
			String identifier, Boolean indexOnCreate, 
			String requestBody, String apiKey, String userToken);
	
	
	/**
	 * This method deletes annotation by the given identifier
	 * @param wskey
	 * @param identifier - part of annotation id 
	 * @param userToken
	 * @return response entity containing headers and status code.
	 */
	public ResponseEntity<String> deleteAnnotation(
			String wskey, String identifier, String userToken, String format);
	
	/**
	 * This method retrieves annotation by the given provider and identifier.
	 * @param wskey
	 * @param identifier
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String identifier);
	
	/**
	 * This method retrieves annotation by the given provider, identifier and search profile.
	 * @param wskey
	 * @param identifier
	 * @param searchProfile
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String identifier, SearchProfiles searchProfile);
	
	/**
	 * This method updates annotation by the given update string in JSON format
	 * @param wskey
	 * @param identifier - part of the annotationId 
	 * @param requestBody
	 * @param userToken
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> updateAnnotation(
			String wskey, String identifier, String requestBody, String userToken);


	/**
	 * Parses the body of the http response into the corresponding Annotation object
	 * @param response
	 * @return
	 * @throws JsonParseException
	 */
	public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException;

	ResponseEntity<String> createTag(String identifier, Boolean indexOnCreate, String annotation,
			String userToken);
		
	public ResponseEntity<String> uploadAnnotations(String wskey, String userToken, String tag, Boolean indexOnCreate, String provider);


}
