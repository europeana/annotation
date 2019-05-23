package eu.europeana.api2.web.model.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
@JsonSerialize(include = Inclusion.NON_EMPTY)
public class ApiError extends ApiResponse {
	
	public boolean success = false;
	
	public ApiError(String apikey, String action, String error) {
		super(apikey, action);
		this.error = error;
	}

	public ApiError(String apikey, String action, String error, long requestNumber) {
		this(apikey, action, error);
		this.requestNumber = requestNumber;
	}
}
