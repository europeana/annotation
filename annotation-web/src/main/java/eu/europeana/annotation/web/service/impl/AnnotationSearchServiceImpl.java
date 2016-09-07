package eu.europeana.annotation.web.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.UrlUtils;

import com.google.common.base.Strings;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.QueryImpl;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.search.QueryFilteringFields;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.protocol.model.AnnotationPage;
import eu.europeana.annotation.web.protocol.model.impl.AnnotationPageImpl;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.corelib.utils.StringArrayUtils;

public class AnnotationSearchServiceImpl implements AnnotationSearchService {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	SolrAnnotationService solrService;

	@Resource
	AuthenticationService authenticationService;

	Logger logger = Logger.getLogger(getClass());

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public String getComponentName() {
		return configuration.getComponentName();
	}

	protected AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public SolrAnnotationService getSolrService() {
		return solrService;
	}

	public void setSolrService(SolrAnnotationService solrService) {
		this.solrService = solrService;
	}

	protected ResultSet<? extends AnnotationView> searchItems(Query query) throws HttpException {

		try {
			return getSolrService().search(query);
		} catch (AnnotationServiceException e) {
			throw new HttpException("Solr Search Exception", HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	@Override
	public AnnotationPage search(Query query, HttpServletRequest request) throws HttpException {

		AnnotationPage protocol = new AnnotationPageImpl(query);
		ResultSet<? extends AnnotationView> resultSet = searchItems(query);
		// process resultset into protocol output

		protocol.setItems(resultSet);
		protocol.setTotalInPage(resultSet.getResults().size());
		protocol.setTotalInCollection(resultSet.getResultSize());

		String collectionUrl = buildCollectionUrl(request);
		protocol.setCollectionUri(collectionUrl);
		
		int currentPage = query.getPageNr();
		String currentPageUrl = buildPageUrl(collectionUrl, currentPage);
		protocol.setCurrentPageUri(currentPageUrl);

		if (currentPage > 0) {
			String prevPage = buildPageUrl(collectionUrl, currentPage - 1);
			protocol.setPrevPageUri(prevPage);	
		}
		
		//if current page is not the last one
		boolean isLastPage = protocol.getTotalInCollection() <= (currentPage + 1) * query.getPageSize(); 
		if(!isLastPage){
			String nextPage = buildPageUrl(collectionUrl, currentPage + 1);
			protocol.setNextPageUri(nextPage);
		}
		
		return protocol;
	}

	private String buildPageUrl(String collectionUrl, int page) {

		return collectionUrl + WebAnnotationFields.AND + WebAnnotationFields.PARAM_PAGE + WebAnnotationFields.EQUALS
				+ page;

	}

	private String buildCollectionUrl(HttpServletRequest request) {

		String queryString = request.getQueryString();
		queryString = removeParam(WebAnnotationFields.PARAM_WSKEY, queryString);
		queryString = removeParam(WebAnnotationFields.PARAM_PAGE, queryString);

		return UrlUtils.buildFullRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(),
				request.getRequestURI(), queryString);
	}

	protected String removeParam(final String queryParam, String queryParams) {
		String tmp;
		int startPos = queryParams.indexOf(queryParam);
		int startEndPos = queryParams.indexOf(WebAnnotationFields.AND, startPos + 1);

		if (startPos > 0) {
			tmp = queryParams.substring(0, startPos);
			if (startEndPos > 0)
				tmp += queryParams.substring(startEndPos);
		} else {
			tmp = queryParams;
		}
		return tmp;
	}

	@Override
	public Query buildSearchQuery(String queryString, String[] filters, String[] facets, String sort, String sortOrder,
			int pageNr, int pageSize, SearchProfiles profile) {

		// TODO: check if needed
		String[] normalizedFacets = StringArrayUtils.splitWebParameter(facets);
		boolean isFacetsRequested = isFacetsRequest(normalizedFacets);

		Query searchQuery = new QueryImpl();
		searchQuery.setQuery(queryString);
		if(pageNr < 0)
			searchQuery.setPageNr(Query.DEFAULT_PAGE);
		else
			searchQuery.setPageNr(pageNr);
		
		if(pageSize < 0)
			searchQuery.setPageSize(Query.DEFAULT_PAGE_SIZE);
		else
			searchQuery.setPageSize(pageSize);
		
		if (isFacetsRequested)
			searchQuery.setFacetFields(normalizedFacets);

		translateSearchFilters(filters);
		searchQuery.setFilters(filters);
		searchQuery.setSearchProfile(profile);

		setSearchFields(searchQuery, profile);
		if (!Strings.isNullOrEmpty(sort)) {
			searchQuery.setSort(sort);
			searchQuery.setSortOrder(sortOrder);
		}

		return searchQuery;
	}

	private void setSearchFields(Query searchQuery, SearchProfiles profile) {
		switch (profile) {
		case FACET:
			// only facets, do not return results but how? in page based
			// searchQuery.setViewFields(new
			// String[]{SolrAnnotationFields.ANNOTATION_ID_URL});
			// searchQuery.setRows(0);
			break;

		case STANDARD:
			searchQuery.setViewFields(new String[] { SolrAnnotationConstants.ANNOTATION_ID_URL });
			break;

		default:
			// TODO: consider throwing an exception
			break;
		}

	}

	protected boolean isFacetsRequest(String[] facets) {
		return facets != null && facets.length > 0;
	}

	protected void translateSearchFilters(String[] filters) {
		if (filters != null) {
			int count = 0;
			int FILTER_MODEL_POS = 0;
			int FILTER_VALUE_POS = 1;
			for (String filter : filters) {
				if (filter.contains(WebAnnotationFields.COLON)) {
					String[] filterElem = filter.split(WebAnnotationFields.COLON);
					if (QueryFilteringFields.contains(filterElem[FILTER_MODEL_POS])) {
						filters[count] = QueryFilteringFields.getSolrFieldByModel(filterElem[FILTER_MODEL_POS])
								+ WebAnnotationFields.COLON + filterElem[FILTER_VALUE_POS];
					}
				}
				count++;
			}
		}
	}
}
