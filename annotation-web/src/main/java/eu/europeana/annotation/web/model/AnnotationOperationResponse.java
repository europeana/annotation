package eu.europeana.annotation.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.api.commons.web.model.ApiResponse;

@JsonInclude(Include.NON_NULL)
public class AnnotationOperationResponse extends ApiResponse{
	
	Annotation annotation;
	
	private BatchReportable operationReport;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	public static String ERROR_VISIBILITY_CHECK = "This annotation object is marked as not visible!";
	public static String ERROR_RESOURCE_ID_DOES_NOT_MATCH = 
			"Passed 'collection' or 'object' parameter does not match to the ResourceId given in the JSON string!";	
	public static String ERROR_PROVIDER_DOES_NOT_MATCH = 
			"Passed 'provider' parameter does not match to the provider given in the JSON string!";	
	
	public static String ERROR_ANNOTATION_EXISTS_IN_DB = 
			"Cannot store object! An object with the given id already exists in the database: ";
	
	public static String ERROR_STATUS_TYPE_NOT_REGISTERED = 
			"Cannot set annotation status! A given status type is not registered: ";
	
	public static String ERROR_STATUS_ALREADY_SET = 
			"A given status type is already set: ";

	
	public AnnotationOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public BatchReportable getOperationReport() {
		return operationReport;
	}

	public void setOperationReport(BatchReportable operationReport) {
		this.operationReport = operationReport;
	}

}
