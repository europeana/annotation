package eu.europeana.annotation.client.model.result;

public abstract class AbstractAnnotationApiResponse {

	private String action;
	private String success;
	private String error;
	

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
