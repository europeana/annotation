package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the annotation class hierarchy 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationServiceInstantiationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895963160368650224L;
	public static final String DEFAULT_MESSAGE = "Cannot instantiate annotation service: ";
	
	public AnnotationServiceInstantiationException(String attributeName){
//		super(DEFAULT_MESSAGE + attributeName);
		super(attributeName);
	}
	
	public AnnotationServiceInstantiationException(String attributeName , Throwable th){
//		super(DEFAULT_MESSAGE + attributeName, th);
		super(attributeName, th);
	}
	
}
