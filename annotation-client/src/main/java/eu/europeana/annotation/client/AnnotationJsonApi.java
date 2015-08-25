package eu.europeana.annotation.client;

import java.util.List;

import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;

public interface AnnotationJsonApi {

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
	 * This method retrieves annotation by the given provider and identifier.
	 * @param wskey
	 * @param provider
	 * @param identifier
	 * @param byTypeJsonld This flag is true when jsonld type is required, false otherwise
	 * @return response entity containing body, headers and status code.
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String provider, String identifier, boolean byTypeJsonld);

		
	public String createAnnotation(Annotation annotation);

	public ImageAnnotation createImageAnnotation(ImageAnnotation annotation);

	//public SemanticTag createSemanticTag(SemanticTag annotation);

	/**
	 * @param collectionId
	 * @param objectHash
	 * @return
	 */
	public List<Annotation> getAnnotations(String collectionId, String objectHash);

	/**
	 * @param collectionId
	 * @param objectHash
	 * @param provider
	 * @return
	 */
	public String getAnnotations(String collectionId, String objectHash, String provider);
//	public List<Annotation> getAnnotations(String collectionId, String objectHash, String provider);

	/**
	 * @param europeanaId
	 * @param provider
	 * @param annotationNr
	 * @return
	 */
	public Annotation getAnnotation(String europeanaId, String provider, Integer annotationNr);
	
	/**
	 * @param collectionId
	 * @param objectHash
	 * @param provider
	 * @param annotationNr
	 * @return
	 */
	public Annotation getAnnotation(
			String collectionId, String objectHash, String provider, Integer annotationNr);	
}
