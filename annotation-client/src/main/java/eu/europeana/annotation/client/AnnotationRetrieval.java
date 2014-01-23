package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationRetrieval {

	public abstract List<Annotation> getAnnotations(String collectionId, String objectHash);
	
	
	
}
