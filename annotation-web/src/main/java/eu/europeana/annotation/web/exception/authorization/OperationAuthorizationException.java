package eu.europeana.annotation.web.exception.authorization;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class OperationAuthorizationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8994054571719881829L;
	/**
	 * 
	 */
	public static final String MESSAGE_OPERATION_NOT_AUTHORIZED = "The user is not allowed to perform the given operation!";
	
	private String paramValue; 
	
	public OperationAuthorizationException(String message, String paramValue){
		this(message, paramValue, HttpStatus.METHOD_NOT_ALLOWED, null);
	}
	
	public OperationAuthorizationException(String message, String paramValue, Throwable th){
		this(message, paramValue, HttpStatus.METHOD_NOT_ALLOWED, th);
	}
	
	public OperationAuthorizationException(String message, String paramValue, HttpStatus status){
		this(message, paramValue, status, null);
	}

	public OperationAuthorizationException(String message, String paramValue, HttpStatus status, Throwable th){
		super(message + " " + paramValue, status, th);
		this.paramValue = paramValue;
	}
	
	public String getParamValue() {
		return paramValue;
	}

}
