package eu.europeana.annotation.definitions.model.whitelist;

import java.util.Date;

/**
 * This interface defines method for the Whitelist objects.
 */
public interface WhitelistEntry {

	public String getHttpUrl();
	
	public void setHttpUrl(String url);

	public String getName();
	
	public void setName(String name);
	
	public Date getLastUpdate();

	public void setLastUpdate(Date lastUpdateTimestamp);

	public Date getCreationDate();

	public void setCreationDate(Date creationDate);
	
	public abstract String getStatus();
	
	public void setStatus(String status);
	
	public Date getEnableFrom();

	public void setEnableFrom(Date enableFromTimestamp);

	public Date getDisableTo();

	public void setDisableTo(Date disableToTimestamp);

}
