package eu.europeana.annotation.definitions.model.authentication.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.authentication.Client;

public class BaseClientImpl implements Client{

	String clientId;
	String authenticationConfigJson;
	private Date creationDate;
	private Date lastUpdate;
	
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}
	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	@Override
	public String getClientId() {
		return clientId;
	}
	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	@Override
	public String getAuthenticationConfigJson() {
		return authenticationConfigJson;
	}
	@Override
	public void setAuthenticationConfigJson(String authenticationConfigJson) {
		this.authenticationConfigJson = authenticationConfigJson;
	}
	
}
