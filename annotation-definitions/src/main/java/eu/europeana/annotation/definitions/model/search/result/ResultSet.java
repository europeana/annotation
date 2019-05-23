package eu.europeana.annotation.definitions.model.search.result;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.search.Query;

/**
 * taken from the corelib-definitions/corelib-search and eliminated explicit solr dependencies
 * @author Sergiu Gordea @ait
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
public class ResultSet<T> {

	/**
	 * The request query object
	 */
	private Query query;

	/**
	 * The list of result objects
	 */
	private List<T> results;

	/**
	 * The list of facets
	 */
	private List<FacetFieldView> facetFields;

	/**
	 * The query facets response
	 */
	private Map<String, Integer> queryFacets;

	// statistics

	/**
	 * The total number of results
	 */
	private long resultSize;

	/**
	 * The time in millisecond how long the search has been taken
	 */
	private long searchTime;

	/**
	 * GETTERS & SETTTERS
	 */

	public List<T> getResults() {
		return results;
	}

	public ResultSet<T> setResults(List<T> list) {
		this.results = list;
		return this;
	}

	public Query getQuery() {
		return query;
	}

	public ResultSet<T> setQuery(Query query) {
		this.query = query;
		return this;
	}

	public List<FacetFieldView> getFacetFields() {
		return facetFields;
	}

	public ResultSet<T> setFacetFields(List<FacetFieldView> facetFields) {
		this.facetFields = facetFields;
		return this;
	}

	/**
	 * Gets the total number of results
	 * @return
	 */
	public long getResultSize() {
		return resultSize;
	}

	public ResultSet<T> setResultSize(long resultSize) {
		this.resultSize = resultSize;
		return this;
	}

	public long getSearchTime() {
		return searchTime;
	}

	public ResultSet<T> setSearchTime(long l) {
		this.searchTime = l;
		return this;
	}

	public Map<String, Integer> getQueryFacets() {
		return queryFacets;
	}

	public ResultSet<T> setQueryFacets(Map<String, Integer> queryFacets) {
		this.queryFacets = queryFacets;
		return this;
	}

	@Override
	public String toString() {
		return "ResultSet [query=" + query + ", results=" + results
				+ ", facetFields=" + facetFields 
				+ ", resultSize=" + resultSize + ", searchTime=" + searchTime
				+ "]";
	}

}
