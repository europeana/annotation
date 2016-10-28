package eu.europeana.annotation.solr.exceptions;


public class StatusLogServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2276943801835713511L;

	public StatusLogServiceException(String message, Throwable th) {
		super(message, th);
	}

	public StatusLogServiceException(String message) {
		super(message);
	}
	
	
}
