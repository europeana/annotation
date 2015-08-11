package eu.europeana.annotation.mongo.exception;

public class InvalidConceptException extends AnnotationMongoException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5281333628280692928L;
	
	public static final String MESSAGE_NULL_ATTRIBUTE = "Attribute must not be null: ";
	public static final String MESSAGE_WRONG_VALUE = "The value of the attribute is not allowed.";
	
	public InvalidConceptException(String mesage){
		super(mesage);
	}

	public InvalidConceptException(String mesage, Throwable th){
		super(mesage, th);
	}

}
