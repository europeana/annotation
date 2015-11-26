package eu.europeana.annotation.web.model;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class WhitelistOperationResponse extends ApiResponse {
	
	Whitelist whitelist;
	List<? extends Whitelist> whitelistEntries = new ArrayList<Whitelist>();

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	
	public static String ERROR_WHITELIST_OBJECT_EXISTS_IN_DB = 
			"Cannot store whitelist object! An object with the given url already exists in the database: ";
	
	public WhitelistOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public Whitelist getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(Whitelist whitelist) {
		this.whitelist = whitelist;
	}

	public void setWhitelistEntries(List<? extends Whitelist> whitelist2) {
		this.whitelistEntries = whitelist2;
	}

}
