package eu.europeana.annotation.web.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.api.commons.web.model.ApiResponse;

@JsonSerialize(include = Inclusion.NON_EMPTY)
public class ClientApplicationOperationResponse extends ApiResponse{
	
	Client clientApplication;
	
	public ClientApplicationOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}

	public Client getClientApplication() {
		return clientApplication;
	}

	public void setClientApplication(Client clientApplication) {
		this.clientApplication = clientApplication;
	}
	
}
