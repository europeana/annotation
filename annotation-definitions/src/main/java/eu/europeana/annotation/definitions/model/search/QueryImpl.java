package eu.europeana.annotation.definitions.model.search;

import java.util.ArrayList;
import java.util.Arrays;
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
	private String[] filters;
	private String[] facetFields;
	private String[] viewFields;

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

	@Override
	public String[] getViewFields() {
		return viewFields;
	}

	@Override
	public void setViewFields(String[] viewFields) {
		this.viewFields = viewFields;
	}
	
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

		if (getFacetFields() != null) 
			params.add("facet.fields=" + Arrays.toString(getFacetFields()));
		
		return StringUtils.join(params, "&");
	}
	
}
