package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;

public interface AnnotationJsonApi {

//	public Annotation createAnnotation(Annotation annotation);
	public String createAnnotation(Annotation annotation);
//	public String createAnnotation(String annotation);

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
