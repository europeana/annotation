package eu.europeana.annotation.web.model;

import java.util.HashMap;

public class BatchProcessingStatus implements BatchReportable {
	int failureCount = 0;
	int successCount = 0;
	int indexingFailureCount = 0;
		
	public int getIndexingFailureCount() {
		return indexingFailureCount;
	}
	public void incrementIndexingFailureCount() {
		this.indexingFailureCount++;
	}
	HashMap<String, String> errors;
	
	public BatchProcessingStatus() {
		errors = new HashMap<String, String>();
	}
	
	@Override
	public int getFailureCount() {
		return failureCount;
	}
	
	@Override
	public int getSuccessCount() {
		return successCount;
	}
	
	@Override
	public void incrementFailureCount() {
		this.failureCount++;
	}
	
	@Override
	public void incrementSuccessCount() {
		this.successCount++;
	}
	
	@Override
	public String toString() {
		return "success count: " + successCount 
		+ ", failure count: " + failureCount 
		+ ", indexingFailure count: " + indexingFailureCount;	
	}

	@Override
	public HashMap<String, String> getErrors() {
		return errors;
	}

	@Override
	public void addError(String id, String msg) {
		errors.put(id, msg);
	}

	@Override
	public boolean hasErrors() {
		return this.errors.size() > 0;
	}
}
