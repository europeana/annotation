package eu.europeana.annotation.web.exception;

import org.springframework.http.HttpStatus;

public class HttpException extends Exception{

	private HttpStatus status;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HttpException(String message, HttpStatus status){
		super(message);
		this.status = status;
	}
	
	public HttpException(String message, HttpStatus status, Throwable th){
		super(message, th);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	protected void setStatus(HttpStatus status) {
		this.status = status;
	}
}
