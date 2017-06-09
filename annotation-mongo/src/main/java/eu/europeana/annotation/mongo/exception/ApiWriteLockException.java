package eu.europeana.annotation.mongo.exception;

public class ApiWriteLockException extends Exception {

	private static final long serialVersionUID = -7985477678792399001L;

	public ApiWriteLockException(String mesage) {
		super(mesage);
	}

	public ApiWriteLockException(String mesage, Throwable th) {
		super(mesage, th);
	}

}
