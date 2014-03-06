package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationService {

	public String getComponentName();
	
	public List<? extends Annotation> getAnnotationList(String collection, String object);
	
	public Annotation createAnnotation(Annotation newAnnotation);
	
	public Annotation updateAnnotation(Annotation newAnnotation);
	
	public void deleteAnnotation(Annotation newAnnotation);
	
	public Annotation getAnnotationById(String collectionId, String objectHash, int annotationNr);
	
	
}
