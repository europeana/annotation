package eu.europeana.annotation.definitions.exception.search;

public class SearchRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3364526076494279093L;
	
	
	public SearchRuntimeException(String message){
		super(message);
	}
	
	public SearchRuntimeException(String message, Throwable th){
		super(message, th);
	}
}
