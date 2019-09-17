package eu.europeana.annotation.web.model;

import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.api.commons.web.model.ApiResponse;

public class ProviderOperationResponse extends ApiResponse{
	
	Provider provider;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	public static String ERROR_ID_GENERATION_TYPE_DOES_NOT_MATCH = 
			"Passed 'idGeneration' type parameter does not match to the registered Id generation types given in the data model!";	
	
	public static String ERROR_PROVIDER_EXISTS_IN_DB = 
			"Cannot store object! An object with the given id already exists in the database: ";
	
	public ProviderOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
