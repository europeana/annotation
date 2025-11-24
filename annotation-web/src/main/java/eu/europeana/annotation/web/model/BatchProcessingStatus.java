package eu.europeana.annotation.web.model;

import java.util.HashMap;
import java.util.Map;

public class BatchProcessingStatus implements BatchReportable {
	public static final String successCountStr="Success count: ";
	public static final String failureCountStr="failure count: ";
	public static final String indexingFailureCountStr="indexingFailure count: ";
	
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
		String errorsStr="";
		for (Map.Entry<String, String> error : errors.entrySet()) {
			errorsStr+=error.getKey() + " = " + error.getValue() + ";";
		}
		if(!errorsStr.isEmpty()) {
			errorsStr=errorsStr.substring(0, errorsStr.length()-1) + ".";
		}
		
		return successCountStr + successCount + ", " + failureCountStr + failureCount 
		+ ", " + indexingFailureCountStr + indexingFailureCount + ". Errors: " + errorsStr + ".";	
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
