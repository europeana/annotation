package eu.europeana.annotation.web.model;

public class BatchUploadStatus extends BatchProcessingStatus {
	
	public Integer totalNumberOfAnnotations;
	
	public Integer numberOfAnnotationsWithId;
	
	public Integer numberOfAnnotationsWithoutId;
	
	public BatchOperationStep step;

	@Override
	public String toString() {
		if(failureCount > 0)
			return step + " failed (" + failureCount + " errors).";
		else
			return step + " succeeded (" + successCount + " successes).";
	}

	public Integer getTotalNumberOfAnnotations() {
		return totalNumberOfAnnotations;
	}

	public void setTotalNumberOfAnnotations(Integer totalNumberOfAnnotations) {
		this.totalNumberOfAnnotations = totalNumberOfAnnotations;
	}

	public Integer getNumberOfAnnotationsWithId() {
		return numberOfAnnotationsWithId;
	}

	public void setNumberOfAnnotationsWithId(Integer numberOfAnnotationsWithId) {
		this.numberOfAnnotationsWithId = numberOfAnnotationsWithId;
	}

	public Integer getNumberOfAnnotationsWithoutId() {
		return numberOfAnnotationsWithoutId;
	}

	public void setNumberOfAnnotationsWithoutId(Integer numberOfAnnotationsWithoutId) {
		this.numberOfAnnotationsWithoutId = numberOfAnnotationsWithoutId;
	}

	public String getStep() {
		return step.toString();
	}

	public void setStep(BatchOperationStep step) {
		this.failureCount = 0;
		this.successCount = 0;
		this.step = step;
	}

}
