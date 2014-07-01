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
	public static final String DEFAULT_MESSAGE = "Cannot instantiate annotation type: ";
	
	public AnnotationAttributeInstantiationException(String annotationType){
		super(DEFAULT_MESSAGE + annotationType);
	}
	
	public AnnotationAttributeInstantiationException(String annotationType , Throwable th){
		super(DEFAULT_MESSAGE + annotationType, th);
	}
	
}
