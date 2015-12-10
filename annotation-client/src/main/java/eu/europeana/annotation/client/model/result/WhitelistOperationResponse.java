package eu.europeana.annotation.client.model.result;

import java.util.List;

import eu.europeana.annotation.definitions.model.whitelist.*;

public class WhitelistOperationResponse extends AbstractAnnotationApiResponse{

	private WhitelistEntry whitelistEntry;
	
	private List<? extends WhitelistEntry> whitelistEntries;
	
	private String json;
	
	private int num;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public WhitelistEntry getWhitelistEntry() {
		return whitelistEntry;
	}
	public void setWhitelistEntry(WhitelistEntry whitelistEntry) {
		this.whitelistEntry = whitelistEntry;
	}
	public List<? extends WhitelistEntry> getWhitelistEntries() {
		return whitelistEntries;
	}
	public void setWhitelistEntries(List<? extends WhitelistEntry> whitelistEntries) {
		this.whitelistEntries = whitelistEntries;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
}
