package eu.europeana.annotation.definitions.model;



public interface StatusLog {

	public void setUser(String user);
	
	public String getUser();

	public void setStatus(String status);
	
	public String getStatus();

	public void setDate(long date);

	public abstract long getDate();

    public long getIdentifier();

    public void setIdentifier(long identifier);
	
}