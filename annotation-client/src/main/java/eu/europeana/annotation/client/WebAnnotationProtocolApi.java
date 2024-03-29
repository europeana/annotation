package eu.europeana.annotation.client;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;

public interface WebAnnotationProtocolApi {

	/**
	 * This method creates annotation describing it in body JSON string and
	 * providing it with associated wskey, provider name and identifier.
	 * When motivation is not given in JSON - annoType must be set up.
	 * @param indexOnCreate
	 * @param requestBody Contains the body JSON string
	 * @param annoType
	 * @param user TODO
	 * @param wskey
	 * @param identifier
	 * @param userToken
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> createAnnotation(Boolean indexOnCreate, 
			String requestBody, String annoType, String user);
	
	public ResponseEntity<String> createAnnotation(
			String requestBody, String annoType, String user);
	
	/**
	 * This method creates test annotation report object
	 * 
	 * @param apiKey
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */	
	public ResponseEntity<String> createAnnotationReport(
			String wskey, long identifier, String userToken);
	
	/**
	 * This method retrieves moderation report summary
	 * 
	 * @param apiKey
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */	
	public ResponseEntity<String> getModerationReport(
			String wskey, long identifier, String userToken);
	
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
	 * @param identifier
	 * @param userToken
	 * @return response entity containing headers and status code.
	 */
	public ResponseEntity<String> deleteAnnotation(
			long identifier);
	
	public ResponseEntity<String> removeAnnotation(
           long identifier);
	
	/**
	 * This method retrieves annotation by the given provider and identifier.
	 * @param wskey
	 * @param identifier
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, long identifier);
	
	/**
	 * This method retrieves annotation by the given provider, identifier and search profile.
	 * @param wskey
	 * @param identifier
	 * @param searchProfile
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, long identifier, SearchProfiles searchProfile);
	
	/**
	 * This method retrieves annotation by the given provider, identifier and search profile using JWT token
	 * instead of wskey.
	 * @param identifier
	 * @param searchProfile
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			long identifier, SearchProfiles searchProfile);
	
	/**
	 * This method updates annotation by the given update string in JSON format
	 * @param identifier - part of the annotationId 
	 * @param requestBody
	 * @param user TODO
	 * @param wskey
	 * @param userToken
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> updateAnnotation(
			long identifier, String requestBody, String user);


	/**
	 * Parses the body of the http response into the corresponding Annotation object
	 * @param response
	 * @return
	 * @throws JsonParseException
	 */
	public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException;

		
	public ResponseEntity<String> uploadAnnotations(String annotations, Boolean indexOnCreate);

}
