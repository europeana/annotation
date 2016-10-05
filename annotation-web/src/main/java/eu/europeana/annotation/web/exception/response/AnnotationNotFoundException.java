package eu.europeana.annotation.web.exception.response;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class AnnotationNotFoundException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3050674865876453650L;


	/**
	 * 
	 */
	public static final String MESSAGE_ANNOTATION_NO_FOUND = "No annotation found with the given identifier!";
	
	String identifier;
	
	
	public AnnotationNotFoundException(String message, String identifier){
		this(message, identifier, null);
	}
	public AnnotationNotFoundException(String message, String identifier, Throwable th){
		this(message, identifier, HttpStatus.NOT_FOUND, th);
	}
	public AnnotationNotFoundException(String message, String identifier, HttpStatus status, Throwable th){
		super(message + " " + identifier, status, th);
		this.identifier = identifier;
	}
}
