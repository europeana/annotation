package eu.europeana.annotation.definitions.model.search.result.impl;

import java.util.List;
import java.util.Map;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;

public class AnnotationPageImpl implements AnnotationPage{

	//input: search query & pageSize
	private Query searchQuery;
	
	//Results
	ResultSet<? extends AnnotationView> items;
	List<? extends Annotation> annotations;
	private long totalInPage;
	private long totalInCollection;
	
	//pagination
	private int currentPage;
	private String resultCollectionUri;
	private String currentPageUri;
	private String nextPageUri;
	private String prevPageUri;
	
	//facets
	Map<String,Map<String,Integer>> facets;
	
	public AnnotationPageImpl (){
	}
	
	public AnnotationPageImpl (Query searchQuery){
		this.searchQuery = searchQuery;		
	}
	
	@Override
	public Query getSearchQuery() {
		return searchQuery;
	}
	@Override
	public void setSearchQuery(Query searchQuery) {
		this.searchQuery = searchQuery;
	}
	@Override
	public ResultSet<? extends AnnotationView> getItems() {
		return items;
	}
	@Override
	public void setItems(ResultSet<? extends AnnotationView> items) {
		this.items = items;
	}
	
	@Override
	public List<? extends Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public void setAnnotations(List<? extends Annotation> annotations) {
		this.annotations = annotations;
	}
	
	@Override
	public int getCurrentPage() {
		return currentPage;
	}
	@Override
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	@Override
	public String getCollectionUri() {
		return resultCollectionUri;
	}
	@Override
	public void setCollectionUri(String resultCollectionUri) {
		this.resultCollectionUri = resultCollectionUri;
	}
	@Override
	public String getCurrentPageUri() {
		return currentPageUri;
	}
	@Override
	public void setCurrentPageUri(String currentPageUri) {
		this.currentPageUri = currentPageUri;
	}
	@Override
	public String getNextPageUri() {
		return nextPageUri;
	}
	@Override
	public void setNextPageUri(String nextPageUri) {
		this.nextPageUri = nextPageUri;
	}
	@Override
	public String getPrevPageUri() {
		return prevPageUri;
	}
	@Override
	public void setPrevPageUri(String prevPageUri) {
		this.prevPageUri = prevPageUri;
	}

	@Override
	public long getTotalInPage() {
		return totalInPage;
	}

	@Override
	public void setTotalInPage(long totalInPage) {
		this.totalInPage = totalInPage;
	}

	@Override
	public long getTotalInCollection() {
		return totalInCollection;
	}

	@Override
	public void setTotalInCollection(long totalInCollection) {
		this.totalInCollection = totalInCollection;
	}

    public Map<String, Map<String, Integer>> getFacets() {
      return facets;
    }
  
    public void setFacets(Map<String, Map<String, Integer>> facets) {
      this.facets = facets;
    }
	
}
