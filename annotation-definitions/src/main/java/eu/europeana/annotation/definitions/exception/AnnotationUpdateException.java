package eu.europeana.annotation.definitions.exception;

/**
 * This class is used to represent update errors for the annotation class hierarchy 
 * @author Roman Graf
 *
 */
public class AnnotationUpdateException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895963160368650224L;
	public static final String DEFAULT_MESSAGE = "Cannot update annotation with identifier: ";
	
	public AnnotationUpdateException(String identifierName){
		super(DEFAULT_MESSAGE + identifierName);
	}
	
	public AnnotationUpdateException(String identifierName , Throwable th){
		super(DEFAULT_MESSAGE + identifierName, th);
	}
	
}
