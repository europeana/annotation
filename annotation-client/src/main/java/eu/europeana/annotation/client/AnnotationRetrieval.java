package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationRetrieval {

	public List<Annotation> getAnnotations(String collectionId, String objectHash);
	
	public Annotation getAnnotation(String europeanaId, Integer annotationNr);
	
	public Annotation getAnnotation(String collectionId, String objectHash,
			Integer annotationNr);
}
