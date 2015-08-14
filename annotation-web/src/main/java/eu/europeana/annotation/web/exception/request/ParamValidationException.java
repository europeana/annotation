package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class ParamValidationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	public static final String MESSAGE_EUROPEANAID_NO_MATCH = "Europeana ID doesn't match the RESTFull url!";
	public static final String MESSAGE_IDENTIFIER_NOT_NULL = "Identifier must not be set when creating a new Annotation for the given provider!";
	public static final String MESSAGE_IDENTIFIER_NULL = "Identifier must not be null for the given provider!";
	public static final String MESSAGE_ANNOTATION_ID_EXISTS = "An annotation with hte given identifier already exists in database! Overwrite not allowed in this method!";
	public static final String MESSAGE_FORMAT_NOT_SUPPORTED = "The requested format is not supported!";
	public static final String MESSAGE_INVALID_PARAMETER_VALUE = "Invalid request. Parameter value not supported!";
	
	
	String parameterName;
	String parameterValue;
	
	
//	public ParamValidationException(String message){
//		super(message);
//	}
	
	public ParamValidationException(String message, String parameterName, String parameterValue){
		this(message, parameterName, parameterValue, null);
	}
	public ParamValidationException(String message, String parameterName, String parameterValue, Throwable th){
		this(message, parameterName, parameterValue, HttpStatus.BAD_REQUEST, th);
	}
	public ParamValidationException(String message, String parameterName, String parameterValue, HttpStatus status, Throwable th){
		super(message + " " + parameterName + ":" + parameterValue, status, th);
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}
}
