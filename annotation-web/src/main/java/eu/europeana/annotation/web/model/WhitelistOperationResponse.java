package eu.europeana.annotation.web.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

@JsonSerialize(include = Inclusion.NON_NULL)
public class WhitelistOperationResponse extends ApiResponse {
	
	WhitelistEntry whitelistEntry;
	List<? extends WhitelistEntry> whitelist = new ArrayList<WhitelistEntry>();

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	
	public static String ERROR_WHITELIST_OBJECT_EXISTS_IN_DB = 
			"Cannot store whitelist object! An object with the given url already exists in the database: ";
	
	public WhitelistOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public WhitelistEntry getWhitelistEntry() {
		return whitelistEntry;
	}

	public void setWhitelistEntry(WhitelistEntry whitelistEntry) {
		this.whitelistEntry = whitelistEntry;
	}

	public void setWhitelist(List<? extends WhitelistEntry> whitelist) {
		this.whitelist = whitelist;
	}

}
