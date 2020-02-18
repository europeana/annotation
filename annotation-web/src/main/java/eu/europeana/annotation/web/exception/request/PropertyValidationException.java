package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class PropertyValidationException extends HttpException{

	/**
     * 
     */
    private static final long serialVersionUID = 3761020205153361298L;
	
	public PropertyValidationException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, null);
	}
	
	public PropertyValidationException(String message, String i18nKey, String[] i18nParams, Throwable th){
		this(message, i18nKey, i18nParams, HttpStatus.BAD_REQUEST, th);
	}
	
	public PropertyValidationException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
