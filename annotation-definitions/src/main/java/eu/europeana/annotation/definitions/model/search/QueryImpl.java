package eu.europeana.annotation.definitions.model.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class QueryImpl implements Cloneable, Query{

	private int pageNr;
	private int pageSize;
	private long limit;
	private String sort;
	private String sortOrder;
	private String query;
	private String[] filters;
	private String[] facetFields;
	private String[] viewFields;
	private SearchProfiles searchProfile;

	@Override
	public int getPageNr() {
		return pageNr;
	}

	@Override
	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	@Override
	public void setLimit(long limit) {
		this.limit = limit;
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

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		List<String> params = new ArrayList<>();
		params.add("q=" + query);
		params.add("page=" + getPageNr());
		params.add("pageSize=" + getPageSize());
		params.add("limit=" + getLimit());
		
		if (sort != null){
			String sortParam = "sort=" + sort;
			if(sortOrder != null)
				sortParam += (" " + sortOrder);
		
			params.add(sortParam);
			
		}

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

	@Override
	public SearchProfiles getSearchProfile() {
		return searchProfile;
	}

	@Override
	public void setSearchProfile(SearchProfiles searchProfile) {
		this.searchProfile = searchProfile;
	}

	
}
