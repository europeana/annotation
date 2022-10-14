package eu.europeana.annotation.definitions.model.search.result;

import java.util.List;
import java.util.Map;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.view.AnnotationView;

public interface AnnotationPage {

	void setPrevPageUri(String prevPageUri);

	String getPrevPageUri();

	void setNextPageUri(String nextPageUri);

	String getNextPageUri();

	void setCurrentPageUri(String currentPageUri);

	String getCurrentPageUri();

	void setCollectionUri(String resultCollectionUri);

	String getCollectionUri();

	void setCurrentPage(int currentPage);

	int getCurrentPage();

	void setItems(ResultSet<? extends AnnotationView> items);

	ResultSet<? extends AnnotationView> getItems();

	void setSearchQuery(Query searchQuery);

	Query getSearchQuery();

	void setTotalInCollection(long totalInCollection);

	long getTotalInCollection();

	void setTotalInPage(long totalInPage);

	long getTotalInPage();

	void setAnnotations(List<? extends Annotation> annotations);

	List<? extends Annotation> getAnnotations();
	
    Map<String, Map<String, Integer>> getFacets();
    
    void setFacets(Map<String, Map<String, Integer>> facets);

}
