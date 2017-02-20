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
	public static final String MESSAGE_ANNOTATION_STATE_NOT_ACCESSIBLE = "The user is not allowed to access the annotation in the current state!";
	
	private String paramValue; 
	
	public UserAuthorizationException(String message, String paramValue){
		this(message, paramValue, HttpStatus.UNAUTHORIZED, null);
	}
	
	public UserAuthorizationException(String message, String paramValue, Throwable th){
		this(message, paramValue, HttpStatus.UNAUTHORIZED, th);
	}
	
	public UserAuthorizationException(String message, String paramValue, HttpStatus status){
		this(message, paramValue, status, null);
	}

	public UserAuthorizationException(String message, String paramValue, HttpStatus status, Throwable th){
		super(message + " " + paramValue, status, th);
		this.setParamValue(paramValue);
	}
	
	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
