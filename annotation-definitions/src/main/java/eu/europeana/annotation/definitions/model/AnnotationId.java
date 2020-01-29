package eu.europeana.annotation.definitions.model;

import java.io.Serializable;

public interface AnnotationId extends Serializable {

	String NOT_INITIALIZED_LONG_ID = "-1";
	
	/**
	 * unanbiguous identifier of the resource for a given provider
	 * @return
	 */
	public void setIdentifier(String identifier);

	public String getIdentifier();

	/**
	 * returns the URI of the annotation (/identifier)
	 * @see also {@link AnnotationId#toHttpUrl(String)}
	 * 
	 */
	public String toRelativeUri();
	
	/**
	 * returns the HTTP URL where the annotation can be accessed  ({baseUrl}/toUri())
	 * @see also {@link AnnotationId#toRelativeUri()}
	 * 
	 */
	public String toHttpUrl();

	void setBaseUrl(String baseUrl);

	String getBaseUrl();

	void copyFrom(AnnotationId volatileObject);

	void setHttpUrl(String httpUrl);

	String getHttpUrl();
	
}
