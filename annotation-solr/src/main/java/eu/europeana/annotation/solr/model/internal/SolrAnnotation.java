package eu.europeana.annotation.solr.model.internal;

import eu.europeana.annotation.definitions.model.Annotation;


public interface SolrAnnotation extends Annotation {

	void setAnnotatedByString(String annotatedBy);

	/**
	 * The syntax for an annotationId string is '\resourceId\annotationId'
	 * @param annotationId
	 */
	void setAnnotationIdString(String annotationId);

	String getAnnotatedByString();

	String getAnnotationIdString();
	
	void setResourceId(String resourceId);
	
	String getResourceId();
	
	public void setLabel(String label);
	
	public String getLabel();
	
	public void setLanguage(String language);

	public String getLanguage();

	public void setHttpUri(String httpUri);

	public String getHttpUri();
	
	public void setAnnotationType(String annotation_type);
	
	public String getAnnotationType();
	
}
