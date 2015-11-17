package eu.europeana.annotation.solr.vocabulary;

public enum SolrTagFields {
	
	LABEL("label")
	, TAG_ID("tagId")
	, MULTILINGUAL("multilingual")
	, CREATOR("creator");

		
	private String solrField;
		
	SolrTagFields(String solrField){
		this.solrField = solrField;
	}
		
	public String getSolrField(){
		return solrField;
	}
	
	public static boolean contains(String value) {

	    for (SolrTagFields c : SolrTagFields.values()) {
	        if (c.name().equals(value) || c.getSolrField().equals(value)) {
	            return true;
	        }
	    }

	    return false;
	}	

}