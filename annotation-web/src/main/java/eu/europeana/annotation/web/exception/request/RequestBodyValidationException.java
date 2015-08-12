package eu.europeana.annotation.web.exception.request;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;

public class RequestBodyValidationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	
	private String bodyValue;
	
	public static String MESSAGE_PARSE_BODY = " Cannot parse body to annotation! ";
	
	
	public RequestBodyValidationException(String message, String bodyValue){
		this(message, bodyValue, null);
	}
	public RequestBodyValidationException(String message, String bodyValue, Throwable th){
		super(message + bodyValue, HttpStatus.BAD_REQUEST, th);
		this.bodyValue = bodyValue;
	}
	
	public RequestBodyValidationException(String bodyValue, Throwable th){
		super(MESSAGE_PARSE_BODY + "\n" + bodyValue, HttpStatus.BAD_REQUEST, th);
		this.bodyValue = bodyValue;
	}
	
	
	public String getBodyValue() {
		return bodyValue;
	}
	protected void setBodyValue(String bodyValue) {
		this.bodyValue = bodyValue;
	}
}
