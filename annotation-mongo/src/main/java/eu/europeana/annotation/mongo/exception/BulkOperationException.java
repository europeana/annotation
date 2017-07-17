package eu.europeana.annotation.mongo.exception;

import java.util.List;

/**
 * Bulk operation exception 
 */
public class BulkOperationException extends Exception{

	private static final long serialVersionUID = -981750055613537609L;
	
	private List<Integer> failedIndices;
	
	public BulkOperationException() {}

	public BulkOperationException(String message) {
		super(message);
	}

	public BulkOperationException(String mesage, Throwable th) {
		super(mesage, th);
	}
	
	public BulkOperationException(String mesage, List<Integer> failedIndices, Throwable th) {
		super(mesage, th);
		this.failedIndices = failedIndices;
	}

	public List<Integer> getFailedIndices() {
		return failedIndices;
	}

	public void setFailedIndices(List<Integer> failedIndices) {
		this.failedIndices = failedIndices;
	}
	
}