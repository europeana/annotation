package eu.europeana.annotation.solr.model.internal;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public interface SolrAnnotationConst extends WebAnnotationFields {
	
	public enum SolrAnnotationFields {
		
		ANNOTATION_ID_STR("annotationId_string")
		, ANNOTATED_BY("annotatedBy_string")
		, RESOURCE_ID("resourceId")
		, LABEL("label")
		, TAG_ID("tagId")
		, CREATOR("creator")
		, LANGUAGE("language")
		, BODY_VALE("body_value")
		, BODY_TYPE("body_type")
		, MULTILINGUAL("multilingual")
		, ANNOTATION_TYPE("annotation_type");

			
		private String solrAnnotationField;
			
		SolrAnnotationFields(String solrAnnotationField){
			this.solrAnnotationField = solrAnnotationField;
		}
			
		public String getSolrAnnotationField(){
			return solrAnnotationField;
		}
		
		public static boolean contains(String value) {

		    for (SolrAnnotationFields c : SolrAnnotationFields.values()) {
		        if (c.name().equals(value) || c.getSolrAnnotationField().equals(value)) {
		            return true;
		        }
		    }

		    return false;
		}	
	
	}
	
	public enum SolrTagFields {
		
		LABEL("label")
		, TAG_ID("tagId")
		, MULTILINGUAL("multilingual");

			
		private String solrTagField;
			
		SolrTagFields(String solrTagField){
			this.solrTagField = solrTagField;
		}
			
		public String getSolrTagField(){
			return solrTagField;
		}
		
		public static boolean contains(String value) {

		    for (SolrTagFields c : SolrTagFields.values()) {
		        if (c.name().equals(value) || c.getSolrTagField().equals(value)) {
		            return true;
		        }
		    }

		    return false;
		}	
	
	}
	
	/**
	 * Search fields for Web Service
	 */
	public static final String ALL = "all";

	/**
	 * Logger name
	 */
	public static final String ROOT = "root";

	/**
	 * Solr query
	 */
	public static final String STAR = "*";
	public static final String AND = "&";
	public static final String START = "start";
	public static final String ROWS = "rows";
	public static final String EQUALS = "=";
	
	/**
	 * Facets
	 */
	/**
	 * Number of milliseconds before the query is aborted by SOLR
	 */
	public static final int TIME_ALLOWED = 30000;
}
