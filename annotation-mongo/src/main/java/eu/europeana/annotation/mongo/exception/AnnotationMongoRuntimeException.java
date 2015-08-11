package eu.europeana.annotation.mongo.exception;

/**
 * Base class for annotations thrown by this component 
 * @author Sergiu Gordea 
 *
 */
public class AnnotationMongoRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7985477678792399001L;
	private static final String DEFAULT_MESSAGE = "Unexpected exception occurured within AnnotationMongo component!";

	public AnnotationMongoRuntimeException(String mesage) {
		super(mesage);
	}

	public AnnotationMongoRuntimeException(String mesage, Throwable th) {
		super(mesage, th);
	}
	
	public AnnotationMongoRuntimeException(Throwable th) {
		this(DEFAULT_MESSAGE, th);
	}
}
