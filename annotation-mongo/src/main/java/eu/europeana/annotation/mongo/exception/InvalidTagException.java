package eu.europeana.annotation.mongo.exception;

public class InvalidTagException extends AnnotationMongoException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5281333628280692928L;
	
	public static final String MESSAGE_NULL_ATTRIBUTE = "Attribute must not be null: ";
	public static final String MESSAGE_WRONG_VALUE = "The value of the attribute is not allowed.";
	
	public InvalidTagException(String mesage){
		super(mesage);
	}

	public InvalidTagException(String mesage, Throwable th){
		super(mesage, th);
	}

}
