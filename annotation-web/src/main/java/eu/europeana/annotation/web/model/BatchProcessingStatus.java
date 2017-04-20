package eu.europeana.annotation.web.model;

public class BatchProcessingStatus {
	int failureCount = 0;
	int successCount = 0;
	int indexingFailureCount = 0;
		
	public int getIndexingFailureCount() {
		return indexingFailureCount;
	}
	public void incrementIndexingFailureCount() {
		this.indexingFailureCount++;
	}
	public int getFailureCount() {
		return failureCount;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void incrementFailureCount() {
		this.failureCount++;
	}
	public void incrementSuccessCount() {
		this.successCount++;
	}
	
	@Override
	public String toString() {
		return "success count: " + String.valueOf(successCount) 
		+ ", failure count: " + String.valueOf(failureCount) 
		+ ", indexingFailure count: " + String.valueOf(indexingFailureCount);	
	}
}
