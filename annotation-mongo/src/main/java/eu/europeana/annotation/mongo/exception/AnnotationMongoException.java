package eu.europeana.annotation.mongo.exception;

/**
 * Base class for annotations thrown by this component 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationMongoException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7985477678792399001L;

	public AnnotationMongoException(String mesage) {
		super(mesage);
	}

	public AnnotationMongoException(String mesage, Throwable th) {
		super(mesage, th);
	}
}
