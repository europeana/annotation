package eu.europeana.annotation.definitions.model.resource.state;

public interface State {

	public abstract void setAuthenticationRequired(boolean authenticationRequired);

	public abstract boolean isAuthenticationRequired();

	public abstract void setUserAgent(String userAgent);

	public abstract String getUserAgent();

	public abstract void setChecksum(String checksum);

	public abstract String getChecksum();

	public abstract void setFixity(String fixity);

	public abstract String getFixity();

	public abstract void setFormat(String format);

	public abstract String getFormat();

	public abstract void setVersionUri(String versionUri);

	public abstract String getVersionUri();

	public abstract void setTimestamp(long timestamp);

	public abstract long getTimestamp();

}
