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
	public static final String MESSAGE_UNKNOWN_TYPE = "Unknown/unsurported property. Cannot instantiate annotation attribute for type: ";
	public static final String MESSAGE_UNKNOWN_KEYWORD = "Unknown/unsurported keyword. Cannot instantiate value of the annotation attribute using the keyword: ";
	public static final String MESSAGE_ID_NOT_URL = "ID value must be a valid URL";
	
	String propertyName;
	String propertyValue;
	
	public AnnotationAttributeInstantiationException(String propertyName){
		this(propertyName, DEFAULT_MESSAGE);
	}
	
	public AnnotationAttributeInstantiationException(String propertyName, String message){
		this(propertyName, null, message);
	}
	
	public AnnotationAttributeInstantiationException(String propertyName, String propertyValue, String message){
		this(propertyName, propertyValue, message, null);
	}
	
	public AnnotationAttributeInstantiationException(String propertyName, String propertyValue, String message, Throwable th){
		super(message + propertyName, th);
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}
	
}
