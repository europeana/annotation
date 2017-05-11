package eu.europeana.annotation.web.model;

import java.util.HashMap;
import java.util.List;

public class BatchProcessingStatus implements BatchReportable {
	int failureCount = 0;
	int successCount = 0;
	HashMap<Integer, String> errors;
	
	public BatchProcessingStatus() {
		errors = new HashMap<Integer, String>();
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
		return "success count: " + String.valueOf(successCount) + ", failure count: " + String.valueOf(failureCount);	
	}

	@Override
	public HashMap<Integer, String> getErrors() {
		return errors;
	}

	@Override
	public void addError(int pos, String msg) {
		errors.put(pos, msg);
	}
}
