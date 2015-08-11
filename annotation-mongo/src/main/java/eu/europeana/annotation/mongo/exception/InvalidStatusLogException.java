package eu.europeana.annotation.mongo.exception;

public class InvalidStatusLogException extends AnnotationMongoException{

	private static final long serialVersionUID = 7779320478183759093L;
	public static final String MESSAGE_NULL_ATTRIBUTE = "Attribute must not be null: ";
	public static final String MESSAGE_WRONG_VALUE = "The value of the attribute is not allowed.";
	
	public InvalidStatusLogException(String mesage){
		super(mesage);
	}

	public InvalidStatusLogException(String mesage, Throwable th){
		super(mesage, th);
	}

}
