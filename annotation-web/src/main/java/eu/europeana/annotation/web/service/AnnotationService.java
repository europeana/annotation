package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationService {

	public String getComponentName();
	
	public List<? extends Annotation> getAnnotationList(String resourceId);
	
	public Annotation createAnnotation(Annotation newAnnotation);
	
	public Annotation updateAnnotation(Annotation newAnnotation);
	
	public void deleteAnnotation(String resourceId,
			int annotationNr);
	
	public Annotation getAnnotationById(String europeanaId, int annotationNr);
	
	
}
