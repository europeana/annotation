package eu.europeana.annotation.client.model.result;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationOperationResponse extends AbstractAnnotationApiResponse{

	private Annotation annotation;
	
	public Annotation getAnnotation() {
		return annotation;
	}
	public void setAnnotation(Annotation annotations) {
		this.annotation = annotations;
	}
	
	
}
