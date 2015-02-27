package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;

public interface AnnotationJsonApi {

	public Annotation createAnnotation(Annotation annotation);

	public ImageAnnotation createImageAnnotation(ImageAnnotation annotation);

	//public SemanticTag createSemanticTag(SemanticTag annotation);

	public List<Annotation> getAnnotations(String collectionId, String objectHash);
	
	public Annotation getAnnotation(String europeanaId, Integer annotationNr);
	
	public Annotation getAnnotation(String collectionId, String objectHash,
			Integer annotationNr);	
}
