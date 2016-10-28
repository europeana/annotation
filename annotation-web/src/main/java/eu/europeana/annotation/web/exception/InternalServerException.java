package eu.europeana.annotation.web.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends HttpException{

	public static final String MESSAGE_UNEXPECTED_EXCEPTION = "An unexpected server exception occured!";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalServerException(String message, Throwable th){
		super(message, HttpStatus.INTERNAL_SERVER_ERROR, th);
	}

	public InternalServerException(Throwable th){
		this(MESSAGE_UNEXPECTED_EXCEPTION, th);
	}

}
