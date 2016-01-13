package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the concept class 
 */
public class ModerationRecordValidationException extends RuntimeException{

	private static final long serialVersionUID = -1978768191768690836L;
	public static final String ERROR_NULL_OBJECT_ID = "Object ID must be null";
	public static final String ERROR_NOT_NULL_URI = "Concept uri must not be null";
	public static final String ERROR_MISSING_ID = "Concept ID must not be null";

	public ModerationRecordValidationException(String message){
		super(message);
	}
	
	public ModerationRecordValidationException(String message, Throwable th){
		super(message, th);
	}
	
	
}
