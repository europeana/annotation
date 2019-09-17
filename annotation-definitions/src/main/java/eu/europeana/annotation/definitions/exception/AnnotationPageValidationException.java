package eu.europeana.annotation.definitions.exception;

/**
 * This class is used to represent validation page creation errors 
 * @author Sven Schlarb
 *
 */
public class AnnotationPageValidationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4594358905747792730L;

	public AnnotationPageValidationException(String message){
		super(message);
	}
	
	public AnnotationPageValidationException(String message, Throwable th){
		super(message, th);
	}
	
	
}
