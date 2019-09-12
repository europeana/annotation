package eu.europeana.annotation.web.model;

import java.util.HashMap;

public interface BatchReportable {
	
	public int getFailureCount();
	
	public int getSuccessCount();
	
	public void incrementFailureCount();
	
	public void incrementSuccessCount();
	
	public HashMap<String, String> getErrors();
	
	public void addError(String id, String msg);
	
	public boolean hasErrors();
	
	public String toString();
	
}

