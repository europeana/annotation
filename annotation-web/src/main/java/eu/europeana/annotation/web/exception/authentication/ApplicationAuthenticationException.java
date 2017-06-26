package eu.europeana.annotation.web.exception.authentication;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class ApplicationAuthenticationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8994054571719881829L;
	/**
	 * 
	 */
	public static final String MESSAGE_APIKEY_FILE_NOT_FOUND = 
			"No apiKey file found in /config/authentication_templates folder! ";
		
	private String paramValue; 
	
	public ApplicationAuthenticationException(String message, String paramValue){
		this(message, paramValue, null);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey, String[] i18nParams) {
		this(message, i18nKey, i18nParams, HttpStatus.UNAUTHORIZED, null);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey, String[] i18nParams, Throwable th) {
		this(message, i18nKey, i18nParams, HttpStatus.UNAUTHORIZED, th);
	}

	public ApplicationAuthenticationException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
	
	public String getParamValue() {
		return paramValue;
	}

}
