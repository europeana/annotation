package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the annotation class hierarchy 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationAttributeInstantiationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895963160368650224L;
	public static final String BASE_MESSAGE = "Cannot instantiate annotation attribute. ";
	public static final String DEFAULT_MESSAGE = "Cannot instantiate annotation attribute for type: ";
	public static final String MESSAGE_UNKNOWN_TYPE = "Cannot instantiate annotation attribute for type: ";
	
	public AnnotationAttributeInstantiationException(String attributeType){
		this(attributeType, DEFAULT_MESSAGE);
	}
	
	public AnnotationAttributeInstantiationException(String attributeType, String message){
		this(attributeType, message, null);
	}
	
	public AnnotationAttributeInstantiationException(String attributeType, String message, Throwable th){
		super(message + attributeType, th);
	}
	
}
