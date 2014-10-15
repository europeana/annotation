package eu.europeana.annotation.solr.exceptions;


public class TagServiceException extends Exception {

	public TagServiceException(String message, Throwable th) {
		super(message, th);
	}

	public TagServiceException(String message) {
		super(message);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3860855231246777668L;

	
}
