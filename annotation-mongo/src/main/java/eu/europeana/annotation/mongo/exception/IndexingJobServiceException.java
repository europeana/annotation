package eu.europeana.annotation.mongo.exception;

public class IndexingJobServiceException extends Exception {
	
	private static final long serialVersionUID = -7985477678792399001L;

	public IndexingJobServiceException(String mesage) {
		super(mesage);
	}

	public IndexingJobServiceException(String mesage, Throwable th) {
		super(mesage, th);
	}

}
