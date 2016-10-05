package eu.europeana.annotation.client.exception;

/**
 * This class is meant to be used for marking and handling technical exceptions that might occur within the system  
 * @author Sergiu Gordea
 *
 */
public class TechnicalRuntimeException extends RuntimeException{
	
	public TechnicalRuntimeException(String message, Exception e) {
		super(message, e);
	}

	public TechnicalRuntimeException(String message) {
		super(message);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3672999785376920974L;
}
