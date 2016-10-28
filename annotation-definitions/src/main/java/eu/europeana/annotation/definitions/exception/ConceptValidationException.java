package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the concept class 
 */
public class ConceptValidationException extends RuntimeException{

	private static final long serialVersionUID = 985374540927784875L;
	public static final String ERROR_NULL_OBJECT_ID = "Object ID must be null";
	public static final String ERROR_NOT_NULL_URI = "Concept uri must not be null";
	public static final String ERROR_MISSING_ID = "Concept ID must not be null";

	public ConceptValidationException(String message){
		super(message);
	}
	
	public ConceptValidationException(String message, Throwable th){
		super(message, th);
	}
	
	
}
