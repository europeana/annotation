package eu.europeana.annotation.mongo.exception;

public class InvalidWhitelistException extends AnnotationMongoException{
	
	private static final long serialVersionUID = -2723927524529456283L;

	public static final String MESSAGE_NULL_ATTRIBUTE = "Attribute must not be null: ";
	public static final String MESSAGE_WRONG_VALUE = "The value of the attribute is not allowed.";
	
	public InvalidWhitelistException(String mesage){
		super(mesage);
	}

	public InvalidWhitelistException(String mesage, Throwable th){
		super(mesage, th);
	}

}
