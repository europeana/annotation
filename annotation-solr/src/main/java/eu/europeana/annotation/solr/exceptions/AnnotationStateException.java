package eu.europeana.annotation.solr.exceptions;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;

public class AnnotationStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8584223161972902453L;
	public  static final String MESSAGE_NOT_ACCESSIBLE = "Annotation is not accessible!";
	
	private AnnotationStates state = null;
	
	public AnnotationStateException(String message, AnnotationStates state, Throwable th) {
		super(message + " " + state.name(), th);
		this.state = state;
	}

	public AnnotationStateException(String message, AnnotationStates state) {
		this(message, state, null);
	}

	public AnnotationStates getState() {
		return state;
	}

}
