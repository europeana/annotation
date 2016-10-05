package eu.europeana.annotation.web.exception.response;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class ModerationNotFoundException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -765146597097281654L;

	/**
	 * 
	 */
	public static final String MESSAGE_MODERATION_NO_FOUND = "No moderation record found with the given identifier!";
	
	String identifier;
	
	
	public ModerationNotFoundException(String message, String identifier){
		this(message, identifier, null);
	}
	public ModerationNotFoundException(String message, String identifier, Throwable th){
		this(message, identifier, HttpStatus.NOT_FOUND, th);
	}
	public ModerationNotFoundException(String message, String identifier, HttpStatus status, Throwable th){
		super(message + " " + identifier, status, th);
		this.identifier = identifier;
	}
}
