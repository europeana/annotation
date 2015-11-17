package eu.europeana.annotation.definitions.model.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class QueryImpl implements Cloneable, Query{

	

//	static {
//		defaultFacets = new ArrayList<>();
//		for (Facet facet : Facet.values()) {
//			defaultFacets.add(facet.toString());
//		}
//	}
	
	private int start;
	private int rows;
	//private String sort;
	private String query;
	@Override
	public int getStart() {
		return start;
	}

	@Override
	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String[] getFilters() {
		return filters;
	}

	@Override
	public void setFilters(String[] filters) {
		this.filters = filters;
	}

	@Override
	public String[] getFacetFields() {
		return facetFields;
	}

	@Override
	public void setFacetFields(String[] facetFields) {
		this.facetFields = facetFields;
	}

	private String[] filters;
	private String[] facetFields;
//	private Map<String, String> parameters = new HashMap<>();
//
//	public Map<String, String> getParameters() {
//		return parameters;
//	}
//
//	public boolean hasParameter(String key) {
//		return parameters.containsKey(key);
//	}

	/**
	 * Adds Solr parameters to the Query object
	 *
	 * @param key
	 *   The parameter name
	 * @param value
	 *   The value of the parameter
	 * @return 
	 *   The Query object
	 */
//	public Query setParameter(String key, String value) {
//		parameters.put(key, value);
//		return this;
//	}

	@Override
	public Query clone() throws CloneNotSupportedException {
		return (Query) super.clone();
	}

	@Override
	public String toString() {
		List<String> params = new ArrayList<>();
		params.add("q=" + query);
		params.add("start=" + start);
		params.add("rows=" + rows);
//
//		if (sort != null && sort.length() > 0){
//			params.add("sort=" + sort + " " + (sortOrder == ORDER_DESC ? "desc" : "asc"));
//		}
//
		if (filters != null) {
			for (String filter : filters) {
				params.add("qf=" + filter);
			}
		}

		if (facetFields != null) {
			for (String facetField : facetFields) {
				params.add("facet.field=" + facetField);
			}
		}

//		if (parameters != null) {
//			for (Entry<String, String> parameter : parameters.entrySet()) {
//				params.add(parameter.getKey() + "=" + parameter.getValue());
//			}
//		}

//		if (getFacetQueries() != null) {
//			for (String query : getFacetQueries()) {
//				params.add("facet.query=" + query);
//			}
//		}

		return StringUtils.join(params, "&");
	}
	
}
