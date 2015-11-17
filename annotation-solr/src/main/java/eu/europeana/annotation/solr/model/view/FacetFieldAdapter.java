package eu.europeana.annotation.solr.model.view;

import org.apache.solr.client.solrj.response.FacetField;

public class FacetFieldAdapter implements eu.europeana.annotation.definitions.model.search.result.FacetFieldView {

	private FacetField solrFacetField; 
	
	
	public FacetFieldAdapter(FacetField solrFacetField) {
		this.solrFacetField = solrFacetField;
	}

	@Override
	public long getCount() {
		return solrFacetField.getValueCount();
	}
	

	@Override
	public String getName() {
		return solrFacetField.getName();
	}

}
