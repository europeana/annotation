package eu.europeana.annotation.definitions.model.vocabulary.search;

/**
 * Fields for that sorting can be applied
 *
 */
public enum SortFields {

	  annotatedAt("annotatedAt", "annotated_at_timestamp")
	, serializedAt("serializedAt", "serialized_at_timestamp")
	, modified("modified", "updated_timestamp"); 

	
	private String sortField;
	private String solrType;
	
	public String getSortField() {
		return sortField;
	}

	
	SortFields(String sortField, String solrType){
		this.sortField = sortField;
		this.solrType = solrType;
	}
	
	public String getSolrType() {
		return solrType;
	}
	
	@Override
	public String toString() {
		return getSortField();
	}
	
	
	public static SortFields getType(String field){
		return valueOf(field.toUpperCase());
	}
	
	
}
