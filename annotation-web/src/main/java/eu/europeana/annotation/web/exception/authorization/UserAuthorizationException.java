package eu.europeana.annotation.web.exception.authorization;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class UserAuthorizationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8994054571719881829L;
	/**
	 * 
	 */
	public static final String MESSAGE_USER_NOT_LOGGED_IN = "The user must be logged in to performed the given action!";
	public static final String MESSAGE_INVALID_TOKEN = "The provided authentication token is invalid!";
	public static final String MESSAGE_USER_NOT_AUTHORIZED = "The user is not authorized to perform the given action!";
	
	
	public UserAuthorizationException(String message){
		this(message, null);
	}
	
	public UserAuthorizationException(String message, Throwable th){
		super(message, HttpStatus.UNAUTHORIZED, th);
	}
}
