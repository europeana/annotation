package eu.europeana.annotation.definitions.exception;

public class UpstreamServerErrorRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6900695205556134865L;
	public static final String DEFAULT_MESSAGE = "An error occured when accessing an upstream server.";
	
	public UpstreamServerErrorRuntimeException(String message){
		super(message);
	}

	public UpstreamServerErrorRuntimeException(Throwable th){
		this(DEFAULT_MESSAGE, th);
	}	
	
	public UpstreamServerErrorRuntimeException(String message, Throwable th){
		super(message, th);
	}
	
}
