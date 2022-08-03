package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;


/**
 * @author GordeaS
 *
 */
public class ParamValidationI18NException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	

	public static final String MESSAGE_IDENTIFIER_NOT_NULL = "Identifier must not be set when creating a new Annotation for the given provider!";
	public static final String MESSAGE_ANNOTATION_ID_EXISTS = "An annotation with the given identifier already exists in database! Overwrite not allowed in this method!";
	public static final String MESSAGE_ANNOTATION_IDENTIFIER_PROVIDED_UPON_CREATION = "The annotation identifier cannot be provided in the input upon the anntation creation!";
	public static final String MESSAGE_INVALID_PARAMETER_VALUE = "Invalid request. Parameter value not supported or not allowed!{0}";
	public static final String MESSAGE_BLANK_PARAMETER_VALUE = "Invalid request. Parameter value must not be null or empty!";
	public static final String MESSAGE_URL_NOT_VALID = "Given URL is not valid!";
	public static final String MESSAGE_INVALID_TAG_SIZE = "Invalid tag size. Must be shorter then 64 characters!";
	public static final String MESSAGE_INVALID_TAG_FORMAT = "Invalid tag format.";
	public static final String MESSAGE_INVALID_SIMPLE_TAG = MESSAGE_INVALID_TAG_FORMAT + " A tag can't contain URLs!";
	public static final String MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE = MESSAGE_INVALID_TAG_FORMAT + " The source of a SpecificResource must be an URL!";
	public static final String MESSAGE_INVALID_TAG_ID_FORMAT = MESSAGE_INVALID_TAG_FORMAT + " The internal id must be an URL!";

	
	public ParamValidationI18NException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, null);
	}
	
	public ParamValidationI18NException(String message, String i18nKey, String[] i18nParams, Throwable th){
		this(message, i18nKey, i18nParams, HttpStatus.BAD_REQUEST, th);
	}
	
	public ParamValidationI18NException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
