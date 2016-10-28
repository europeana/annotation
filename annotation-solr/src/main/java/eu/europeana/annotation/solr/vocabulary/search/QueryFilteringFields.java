package eu.europeana.annotation.solr.vocabulary.search;

import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * Fields for that sorting can be applied
 *
 */
public enum QueryFilteringFields implements MappedSolrField {

	moderationScore(SolrAnnotationConstants.MODERATIONSCORE, SolrAnnotationConstants.MODERATION_SCORE); 

	private String modelField;
	private String solrField;
	
	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.vocabulary.search.MappedSolrFields#getModelField()
	 */
	@Override
	public String getModelField() {
		return modelField;
	}

	
	QueryFilteringFields(String modelField, String solrField){
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

	    for (QueryFilteringFields field : QueryFilteringFields.values()) {
	        if (field.getModelField().equals(query)) {
	            return true;
	        }
	    }

	    return false;
	}	
	
	
	public static String getSolrFieldByModel(String model) {

		String res = "";
	    for (QueryFilteringFields field : QueryFilteringFields.values()) {
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
