package eu.europeana.annotation.definitions.model.search;

public interface Query {

	void setFacetFields(String[] facetFields);

	String[] getFacetFields();

	void setFilters(String[] filters);

	String[] getFilters();

	void setQuery(String query);

	String getQuery();

	void setViewFields(String[] viewFields);

	String[] getViewFields();

	public String getSort();

	public void setSort(String sort);
	
	public String getSortOrder();

	public void setSortOrder(String sortOrder);

	void setLimit(long limit);

	long getLimit();

	void setPageSize(int pageSize);

	int getPageSize();

	void setPageNr(int pageNr);

	int getPageNr();

	void setSearchProfile(SearchProfiles searchProfile);

	SearchProfiles getSearchProfile();

	/**
	 * Default start parameter for Solr
	 */
	public static final int DEFAULT_PAGE = 0;
	/**
	 * Default number of items in the SERP
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	public static final Integer MAX_PAGE_SIZE = 100;
	
	/**
	 * Use these instead of the ones provided in the apache Solr package
	 * in order to avoid introducing a dependency to that package in all modules
	 * they're public because they are read from SearchServiceImpl
	 */
	public static final Integer ORDER_DESC = 0;
	public static final Integer ORDER_ASC = 1;

}
