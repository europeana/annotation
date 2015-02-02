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
		, CREATOR("creator")
		, LANGUAGE("language")
		, BODY_VALE("body_value")
		, BODY_TYPE("body_type")
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
	
	/**
	 * Solr query
	 */
	public static final String ALL_SOLR_ENTRIES = "*:*";
	
}
