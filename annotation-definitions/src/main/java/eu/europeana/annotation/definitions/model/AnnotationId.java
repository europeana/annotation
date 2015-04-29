package eu.europeana.annotation.definitions.model;

import java.io.Serializable;

public interface AnnotationId extends Serializable {

	/**
	 * unanbiguous identifier of the resource (e.g. europeanaId)
	 * @return
	 */
	public String getResourceId();
	
	public void setResourceId(String resourceId);

	public void setAnnotationNr(Long nr);

	public Long getAnnotationNr();

	/**
	 * Provider name e.g. 'webanno' or 'historypin'
	 * @return
	 */
	public String getProvider();

	public void setProvider(String provider);
	
}
