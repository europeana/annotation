package eu.europeana.annotation.definitions.model;

import java.util.Date;


public interface StatusLog {

	public void setUser(String user);
	
	public String getUser();

	public void setStatus(String status);
	
	public String getStatus();

	public void setDate(Date date);

	public abstract Date getDate();

	public void setAnnotationId(AnnotationId annotationId);
	
	public AnnotationId getAnnotationId();
	
}