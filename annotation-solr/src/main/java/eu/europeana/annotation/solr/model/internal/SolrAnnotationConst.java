package eu.europeana.annotation.solr.model.internal;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public interface SolrAnnotationConst extends WebAnnotationFields{

	/**
	 * Helping constants for SolrTag and SolrAnnotation objects
	 */
	public static final String ANNOTATION_ID_STR  = "annotationId_string";
	public static final String RESOURCE_ID        = "resourceId";
	public static final String LABEL              = "label";
	public static final String CREATOR            = "creator";
	public static final String LANGUAGE           = "language";
	
	/**
	 * Solr query
	 */
	public static final String ALL_SOLR_ENTRIES = "*:*";
	
}
