package eu.europeana.annotation.mongo.exception;

/**
 * Base class for moderations thrown by this component 
 */
public class ModerationMongoException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5197396649045255052L;

	public ModerationMongoException(String mesage) {
		super(mesage);
	}

	public ModerationMongoException(String mesage, Throwable th) {
		super(mesage, th);
	}
}
