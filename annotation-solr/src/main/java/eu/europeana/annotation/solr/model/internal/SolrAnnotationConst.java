package eu.europeana.annotation.solr.model.internal;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public interface SolrAnnotationConst extends WebAnnotationFields{

	/**
	 * Helping constants for SolrTag and SolrAnnotation objects
	 */
//	public static final String ANNOTATION_ID_STR  = "annotationId_string";
//	public static final String ANNOTATED_BY       = "annotatedBy_string";
//	public static final String RESOURCE_ID        = "resourceId";
//	public static final String LABEL              = "label";
//	public static final String CREATOR            = "creator";
//	public static final String LANGUAGE           = "language";
//	public static final String ANNOTATION_TYPE    = "annotation_type";
	
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
	
	public enum SolrAnnotationLanguages {
		
		EN("en")
		, DE("de")
		, RO("ro");
			
		private String language;
			
		SolrAnnotationLanguages(String language){
			this.language = language;
		}
			
		public String getSolrAnnotationLanguage(){
			return language;
		}
		
		public static boolean contains(String value) {

		    for (SolrAnnotationLanguages c : SolrAnnotationLanguages.values()) {
		        if (c.name().equals(value) || c.getSolrAnnotationLanguage().equals(value)) {
		            return true;
		        }
		    }

		    return false;
		}	
	
		public static String getLanguageItemByValue(String value) {

			String res = value;
			
		    for (SolrAnnotationLanguages c : SolrAnnotationLanguages.values()) {
		        if (c.name().equals(value) || c.getSolrAnnotationLanguage().equals(value)) {
		            res = c.name();
		            break;
		        }
		    }

		    return res;
		}	
	
	}
	
	/**
	 * Search fields for Web Service
	 */
	public static final String ALL = "all";

	/**
	 * Solr query
	 */
	public static final String ALL_SOLR_ENTRIES = "*:*";
	public static final String DELIMETER = ":";
	public static final String DEFAULT_LANGUAGE = "EN";
	public static final String UNDERSCORE = "_";
	public static final String STAR = "*";
	
	/**
	 * Facets
	 */
	/**
	 * Number of milliseconds before the query is aborted by SOLR
	 */
	public static final int TIME_ALLOWED = 30000;
}
