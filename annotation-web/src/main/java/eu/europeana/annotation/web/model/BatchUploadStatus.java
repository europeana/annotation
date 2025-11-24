package eu.europeana.annotation.web.model;

public class BatchUploadStatus extends BatchProcessingStatus {
	
	public Integer totalNumberOfAnnotations=0;
	
	public Integer numberOfAnnotationsWithId=0;
	
	public Integer numberOfAnnotationsWithoutId=0;
	
	public BatchOperationStep step;

	@Override
	public String toString() {
		return step + ". " + "Total number of annotations: " + totalNumberOfAnnotations +
				", number of annotations with id: " + numberOfAnnotationsWithId + 
				", number of annotations without id: " + numberOfAnnotationsWithoutId + ". " +
				super.toString();
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
