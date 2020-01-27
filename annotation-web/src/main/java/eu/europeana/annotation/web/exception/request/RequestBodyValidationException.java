package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class RequestBodyValidationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	
	private String bodyValue;
	
	public static String MESSAGE_PARSE_BODY = " Cannot parse body to annotation! ";
	
	public RequestBodyValidationException(String message, String i18nKey, Throwable th){
		super(message, i18nKey, null, HttpStatus.BAD_REQUEST, th);
		this.bodyValue = message;
	}
	
	public RequestBodyValidationException(String message, String i18nKey, String[] i18nParams){
		super(message, i18nKey, i18nParams, HttpStatus.BAD_REQUEST, null);
		this.bodyValue = message;
	}
		
	public String getBodyValue() {
		return bodyValue;
	}
	protected void setBodyValue(String bodyValue) {
		this.bodyValue = bodyValue;
	}
}
