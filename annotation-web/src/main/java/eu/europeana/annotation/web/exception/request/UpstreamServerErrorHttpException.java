package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

/**
 * Errors while communicating with the (upstream) server
 */
public class UpstreamServerErrorHttpException extends HttpException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2350644058852098324L;

	public UpstreamServerErrorHttpException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, null);
	}
	
	public UpstreamServerErrorHttpException(String message, String i18nKey, String[] i18nParams, Throwable th){
		this(message, i18nKey, i18nParams, HttpStatus.BAD_GATEWAY, th);
	}
	
	public UpstreamServerErrorHttpException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
