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
	public static final String DEFAULT_MESSAGE = "Unexpected exception occurured within AnnotationMongo component!";
	public static final String INVALID_LAST_INDEXING = "Invalid last indexing timestamp!";

	public AnnotationMongoRuntimeException(String message) {
		super(message);
	}

	public AnnotationMongoRuntimeException(String message, Throwable th) {
		super(message, th);
	}
	
	public AnnotationMongoRuntimeException(Throwable th) {
		this(DEFAULT_MESSAGE, th);
	}
}
