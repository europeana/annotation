package eu.europeana.annotation.client.model.result;

import eu.europeana.annotation.definitions.model.whitelist.*;

public class WhitelistOperationResponse extends AbstractAnnotationApiResponse{

	private WhitelistEntry whitelistEntry;
	
	private String json;
	
	public WhitelistEntry getWhitelistEntry() {
		return whitelistEntry;
	}
	public void setWhitelistEntry(WhitelistEntry whitelistEntry) {
		this.whitelistEntry = whitelistEntry;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
}
