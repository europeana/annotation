package eu.europeana.annotation.client.webanno;

import org.springframework.http.ResponseEntity;

public interface WebAnnotationProtocolApi {

	/**
	 * This method creates annotation describing it in body JSON string and
	 * providing it with associated wskey, provider name and identifier.
	 * When motivation is not given in JSON - annoType must be set up.
	 * @param wskey
	 * @param provider
	 * @param identifier
	 * @param indexOnCreate
	 * @param annotation Contains the body JSON string
	 * @param userToken
	 * @param annoType
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> createAnnotation(
			String wskey, String provider, String identifier, boolean indexOnCreate, 
			String annotation, String userToken, String annoType);
	
	/**
	 * This method deletes annotation by the given identifier
	 * @param wskey
	 * @param provider - part of annotation id
	 * @param identifier - part of annotation id 
	 * @param userToken
	 * @return response entity containing headers and status code.
	 */
	public ResponseEntity<String> deleteAnnotation(
			String wskey, String provider, String identifier, String userToken, String format);
	
	/**
	 * This method retrieves annotation by the given provider and identifier.
	 * @param wskey
	 * @param provider
	 * @param identifier
	* @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String provider, String identifier);
	
	/**
	 * This method updates annotation by the given update string in JSON format
	 * @param wskey
	 * @param provider - part of the annotationId 
	 * @param identifier - part of the annotationId 
	 * @param annotation
	 * @param userToken
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> updateAnnotation(
			String wskey, String provider, String identifier, String annotation, String userToken);


}
