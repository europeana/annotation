package eu.europeana.annotation.web.exception.authorization;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class AuthorizationExtractionException extends HttpException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5809618780329170439L;

	public AuthorizationExtractionException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, HttpStatus.UNAUTHORIZED, null);
	}
	
	public AuthorizationExtractionException(String message, String i18nKey, String[] i18nParams, Throwable th){
		this(message, i18nKey, i18nParams, HttpStatus.UNAUTHORIZED, th);
	}
	
	public AuthorizationExtractionException(String message, String i18nKey, String[] i18nParams, HttpStatus status){
		this(message, i18nKey, i18nParams, status, null);
	}

	public AuthorizationExtractionException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}

}
