package eu.europeana.annotation.web.service;

public interface AnnotationConfiguration {

	public static final String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
	
	public String getComponentName();
	
	/**
	 * uses annotation.indexing.enabled property
	 */
	public boolean isIndexingEnabled();
}
