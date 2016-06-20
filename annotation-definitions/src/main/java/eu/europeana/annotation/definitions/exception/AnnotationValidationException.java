package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the annotation class hierarchy 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationValidationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895963160368650224L;
	public static final String ERROR_NULL_EUROPEANA_ID = "europeanaId must not be null";
	public static final String ERROR_NOT_NULL_OBJECT_ID = "Object ID must be null";
	public static final String ERROR_NULL_BODY = "Annotation Body must not be null";
	public static final String ERROR_NULL_TAG_ID = "TAG_ID must not be null in TAG Bodys";
	public static final String ERROR_NULL_TARGET = "Annotation Target must not be null";
	public static final String ERROR_NULL_ANNOTATED_BY = "Annotated By must not be null";
	public static final String ERROR_NULL_ANNOTATION_ID = "Annotation ID must not be null";
	public static final String ERROR_INVALID_MOTIVATION = "Annotation Motivation not provided or invalid!";
	public static final String ERROR_INVALID_BODY = "Invalid values in annotation body!";

	public AnnotationValidationException(String message){
		super(message);
	}
	
	public AnnotationValidationException(String message, Throwable th){
		super(message, th);
	}
	
	
}
