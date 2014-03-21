package eu.europeana.annotation.web.exception;

public class FunctionalRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	public static final String MESSAGE_EUROPEANAID_NO_MATCH = "Europeana ID doesn't match the RESTFull url!";
	public static final String MESSAGE_ANNOTATIONNR_NOT_NULL = "AnnotationNr must not be set when creating a new Annotation!";
	
	
	public FunctionalRuntimeException(String message){
		super(message);
	}
	
	public FunctionalRuntimeException(String message, Throwable th){
		super(message, th);
	}
}
