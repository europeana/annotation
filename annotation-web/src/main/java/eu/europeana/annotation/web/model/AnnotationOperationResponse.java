package eu.europeana.annotation.web.model;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class AnnotationOperationResponse extends ApiResponse{
	
	Annotation annotation;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	public static String ERROR_RESOURCE_ID_DOES_NOT_MATCH = 
		"Passed 'collection' or 'object' parameter does not match to the ResourceId given in the JSON string!";	
	
	public AnnotationOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

}
