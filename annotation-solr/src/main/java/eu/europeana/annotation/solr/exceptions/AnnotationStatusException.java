package eu.europeana.annotation.solr.exceptions;


public class AnnotationStatusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8584223161972902453L;

	public AnnotationStatusException(String message, Throwable th) {
		super(message, th);
	}

	public AnnotationStatusException(String message) {
		super(message);
	}
	
	
}
