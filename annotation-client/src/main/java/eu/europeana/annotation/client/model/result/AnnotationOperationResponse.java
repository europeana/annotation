package eu.europeana.annotation.client.model.result;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationOperationResponse extends AbstractAnnotationApiResponse{

	private Annotation annotation;
	
	private String json;
	
	public Annotation getAnnotation() {
		return annotation;
	}
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
}
