package eu.europeana.annotation.web.model;

import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class TagOperationResponse extends ApiResponse{
	
	SolrTag tag;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	
	
	public TagOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public SolrTag getTag() {
		return tag;
	}

	public void setTag(SolrTag tag) {
		this.tag = tag;
	}

}
