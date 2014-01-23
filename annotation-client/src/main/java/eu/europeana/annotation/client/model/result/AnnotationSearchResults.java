package eu.europeana.annotation.client.model.result;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationSearchResults extends AbstractAnnotationApiResponse{

	private String itemsCount;
	private String totalResults;
	private List<Annotation> items;
	
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
	public List<Annotation> getItems() {
		return items;
	}
	public void setItems(List<Annotation> annotations) {
		this.items = annotations;
	}
	
	
}
