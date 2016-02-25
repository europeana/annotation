package eu.europeana.annotation.solr.vocabulary.search;

import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * Fields for that sorting can be applied
 *
 */
public enum SortFields implements MappedSolrField {

	  created(SolrAnnotationConstants.CREATED,  SolrAnnotationConstants.CREATED_TIMESTAMP)
	, generated(SolrAnnotationConstants.GENERATED, SolrAnnotationConstants.GENERATED_TIMESTAMP)
	, modified(SolrAnnotationConstants.MODIFIED, SolrAnnotationConstants.UPDATED_TIMESTAMP); 

	
	private String modelField;
	private String solrField;
	
	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.vocabulary.search.MappedSolrFields#getModelField()
	 */
	@Override
	public String getModelField() {
		return modelField;
	}

	
	SortFields(String modelField, String solrField){
		this.modelField = modelField;
		this.solrField = solrField;
	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.vocabulary.search.MappedSolrFields#getSolrField()
	 */
	@Override
	public String getSolrField() {
		return solrField;
	}
	
	
	public static boolean contains(String query) {

	    for (SortFields field : SortFields.values()) {
	        if (field.getModelField().equals(query)) {
	            return true;
	        }
	    }

	    return false;
	}	
	
	
	public static String getSolrFieldByModel(String model) {

		String res = "";
	    for (SortFields field : SortFields.values()) {
	        if (field.getModelField().equals(model)) {
	            res = field.getSolrField();
	            break;
	        }
	    }

	    return res;
	}	
	
	
	@Override
	public String toString() {
		return getModelField();
	}
		
}
