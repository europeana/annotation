package eu.europeana.annotation.web.model;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class AnnotationOperationResponse extends ApiResponse{
	
	Annotation annotation;

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

}
