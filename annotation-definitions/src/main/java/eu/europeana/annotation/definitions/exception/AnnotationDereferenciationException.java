package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the annotation class hierarchy 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationDereferenciationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895963160368650224L;
	public static final String DEFAULT_MESSAGE = "An error occured when accessing metis dereferenciation service";
	
	
	public AnnotationDereferenciationException(Throwable th){
		this(DEFAULT_MESSAGE, th);
	}
	
	
	public AnnotationDereferenciationException(String message, Throwable th){
		super(message, th);
	}
	
}
