package eu.europeana.annotation.web.exception.response;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class AnnotationNotFoundException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3050674865876453650L;


	/**
	 * 
	 */
			
	public AnnotationNotFoundException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, null);
	}
	
	public AnnotationNotFoundException(String message, String i18nKey, String[] i18nParams, Throwable th){
		this(message, i18nKey, i18nParams, HttpStatus.NOT_FOUND, th);
	}
	
	public AnnotationNotFoundException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
