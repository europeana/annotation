package eu.europeana.annotation.definitions.model.resource.state.impl;

import eu.europeana.annotation.definitions.model.resource.state.State;

public class BaseState implements State {
	long timestamp;
	String versionUri;
	String format;
	String fixity;
	String checksum;
	String userAgent;
	boolean authenticationRequired;
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	@Override
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String getVersionUri() {
		return versionUri;
	}
	@Override
	public void setVersionUri(String versionUri) {
		this.versionUri = versionUri;
	}
	@Override
	public String getFormat() {
		return format;
	}
	@Override
	public void setFormat(String format) {
		this.format = format;
	}
	@Override
	public String getFixity() {
		return fixity;
	}
	@Override
	public void setFixity(String fixity) {
		this.fixity = fixity;
	}
	@Override
	public String getChecksum() {
		return checksum;
	}
	@Override
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	@Override
	public String getUserAgent() {
		return userAgent;
	}
	@Override
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	@Override
	public boolean isAuthenticationRequired() {
		return authenticationRequired;
	}
	@Override
	public void setAuthenticationRequired(boolean authenticationRequired) {
		this.authenticationRequired = authenticationRequired;
	}
	
}
