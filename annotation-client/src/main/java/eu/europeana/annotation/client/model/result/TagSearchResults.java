package eu.europeana.annotation.client.model.result;

import java.util.List;

import eu.europeana.annotation.definitions.model.resource.TagResource;

public class TagSearchResults extends AbstractAnnotationApiResponse{

	private String itemsCount;
	private String totalResults;
	private List<TagResource> items;
	
	public String getItemsCount() {
		return itemsCount;
	}
	public void setItemsCount(String itemsCount) {
		this.itemsCount = itemsCount;
	}
	public String getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(String totalResults) {
		this.totalResults = totalResults;
	}
	public List<TagResource> getItems() {
		return items;
	}
	public void setItems(List<TagResource> tags) {
		this.items = tags;
	}
	
	
}
