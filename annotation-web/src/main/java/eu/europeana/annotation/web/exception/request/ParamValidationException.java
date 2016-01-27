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
	public static final String MESSAGE_IDENTIFIER_WRONG = "Identifier must have at least a provider and an identifier number! E.g. 'http://data.europeana.eu/annotation/webanno/494'";
	public static final String MESSAGE_ANNOTATION_ID_EXISTS = "An annotation with the given identifier already exists in database! Overwrite not allowed in this method!";
	public static final String MESSAGE_MODERATION_ID_EXISTS = "A moderation record with the given identifier already exists in database! Overwrite not allowed in this method!";
	public static final String MESSAGE_ANNOTATION_ID_NOT_EXISTS = "An annotation with hte given identifier is not yet existing in database!";
	public static final String MESSAGE_FORMAT_NOT_SUPPORTED = "The requested format is not supported!";
	public static final String MESSAGE_INVALID_PARAMETER_VALUE = "Invalid request. Parameter value not supported or not allowed!";
	public static final String MESSAGE_BLANK_PARAMETER_VALUE = "Invalid request. Parameter value must not be null or empty!";
	public static final String MESSAGE_URL_NOT_VALID = "Given URL is not valid!";
	public static final String MESSAGE_MISSING_PROPERTY_FILE = "The property file was not found!";
	public static final String MESSAGE_INVALID_TAG_SIZE = "Invalid tag size. Must be shorter then 64 characters!";
	public static final String MESSAGE_INVALID_TAG_FORMAT = "Invalid tag format.";
	public static final String MESSAGE_INVALID_SIMPLE_TAG = MESSAGE_INVALID_TAG_FORMAT + " A tag can't contain URLs!";
	public static final String MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE = MESSAGE_INVALID_TAG_FORMAT + " The source of a SpecificResource must be an URL!";
	public static final String MESSAGE_MISSING_MANDATORY_FIELD = "Missing mandatory field!";
	
	public static final int MIN_IDENTIFIER_LEN = 2;
	
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
