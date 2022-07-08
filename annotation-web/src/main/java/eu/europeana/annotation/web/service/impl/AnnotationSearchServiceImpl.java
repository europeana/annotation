package eu.europeana.annotation.web.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import com.google.common.base.Strings;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.QueryImpl;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.search.result.impl.AnnotationPageImpl;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.impl.SolrAnnotationUtils;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.search.QueryFilteringFields;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.definitions.WebFields;
import eu.europeana.api.commons.web.exception.HttpException;

public class AnnotationSearchServiceImpl implements AnnotationSearchService {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	SolrAnnotationService solrService;

	@Resource
	PersistentAnnotationService mongoPersistance;

//	@Resource
//	AuthenticationService authenticationService;

	Logger logger = LogManager.getLogger(getClass());
	
//	public AuthenticationService getAuthenticationService() {
//		return authenticationService;
//	}
//
//	public void setAuthenticationService(AuthenticationService authenticationService) {
//		this.authenticationService = authenticationService;
//	}

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
		  if(SolrAnnotationUtils.checkSolrQueryMalformed(e.getCause())) {
		    throw new HttpException(null, I18nConstants.SOLR_MALFORMED_QUERY_EXCEPTION, null, HttpStatus.BAD_REQUEST, e);
		  }
		  else {
		    throw new HttpException(null, I18nConstants.SOLR_EXCEPTION, null, HttpStatus.GATEWAY_TIMEOUT, e);
		  }
		}
	}

	@Override
	public AnnotationPage search(Query query, HttpServletRequest request) throws HttpException {

		AnnotationPage protocol = new AnnotationPageImpl(query);
		ResultSet<? extends AnnotationView> resultSet = searchItems(query);
		// process resultset into protocol output

		protocol.setItems(resultSet);
		
		//if mongo query is needed
		if(isIncludeAnnotationsSearch(query) && resultSet.getResults().size() > 0){
			List<Long> annotationIds = new ArrayList<Long>(resultSet.getResults().size());
			//parse annotation urls to AnnotationId objects
			for (AnnotationView annotationView : resultSet.getResults()) {
				annotationIds.add(annotationView.getIdentifierAsNumber());		
			}
			
			//fetch annotation objects
			List<? extends Annotation> annotations = mongoPersistance.getAnnotationList(annotationIds);
			protocol.setAnnotations(annotations);
		}
		
		
		
		protocol.setTotalInPage(resultSet.getResults().size());
		protocol.setTotalInCollection(resultSet.getResultSize());

		String collectionUrl = buildCollectionUrl(query, request);
		protocol.setCollectionUri(collectionUrl);
		
		int currentPage = query.getPageNr();
		String currentPageUrl = buildPageUrl(collectionUrl, currentPage, query.getPageSize());
		protocol.setCurrentPageUri(currentPageUrl);

		if (currentPage > 0) {
			String prevPage = buildPageUrl(collectionUrl, currentPage - 1, query.getPageSize());
			protocol.setPrevPageUri(prevPage);	
		}
		
		//if current page is not the last one
		boolean isLastPage = protocol.getTotalInCollection() <= (currentPage + 1) * query.getPageSize(); 
		if(!isLastPage){
			String nextPage = buildPageUrl(collectionUrl, currentPage + 1, query.getPageSize());
			protocol.setNextPageUri(nextPage);
		}
		
		return protocol;
	}

	private boolean isIncludeAnnotationsSearch(Query query) {
		return SearchProfiles.STANDARD.equals(query.getSearchProfile());
	}

	private String buildPageUrl(String collectionUrl, int page, int pageSize) {
		StringBuilder builder = new StringBuilder(collectionUrl);
		builder.append(WebFields.AND).append(WebAnnotationFields.PARAM_PAGE)
			.append(WebFields.EQUALS).append(page);

		builder.append(WebFields.AND).append(WebAnnotationFields.PARAM_PAGE_SIZE)
		.append(WebFields.EQUALS).append(pageSize);

		return builder.toString();
	}

	private String buildCollectionUrl(Query query, HttpServletRequest request) {

		String queryString = request.getQueryString();
		
//		queryString = removeParam(WebAnnotationFields.PARAM_WSKEY, queryString);
		
		//remove out of scope parameters
		queryString = removeParam(WebAnnotationFields.PARAM_PAGE, queryString);
		queryString = removeParam(WebAnnotationFields.PARAM_PAGE_SIZE, queryString);
		
		//avoid duplication of query parameters
		queryString = removeParam(WebAnnotationFields.PARAM_PROFILE, queryString);
		
		//add mandatory parameters
		queryString += ("&" + WebAnnotationFields.PARAM_PROFILE + "=" + query.getSearchProfile().toString()); 

		String result = configuration.getAnnoApiEndpoint() + "/search?";

//		  try {
//		  result += URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString());
//        } catch (UnsupportedEncodingException e) {
//          logger.log(Level.ERROR, "The UnsupportedEncodingException during the URL encoding of the string.", e);
//          result += queryString;
//        }
		result += queryString;
		
		return result;
	}

	protected String removeParam(final String queryParam, String queryParams) {
		String tmp;
		//avoid name conflicts search "queryParam="
		int startPos = queryParams.indexOf(queryParam+WebFields.EQUALS);
		int startEndPos = queryParams.indexOf(WebFields.AND, startPos + 1);

		if (startPos >= 0) {
			//make sure to remove the "&" if not the first param
			if(startPos>0)
				startPos--;		 
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
		//String[] normalizedFacets = StringArrayUtils.splitWebParameter(facets);
		boolean isFacetsRequested = isFacetsRequest(facets);

		Query searchQuery = new QueryImpl();
		searchQuery.setQuery(queryString);
		if(pageNr < 0)
			searchQuery.setPageNr(Query.DEFAULT_PAGE);
		else
			searchQuery.setPageNr(pageNr);

		
		int rows = buildRealPageSize(pageSize, profile);
		searchQuery.setPageSize(rows);
		
		if (isFacetsRequested)
			searchQuery.setFacetFields(facets);

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

	protected int buildRealPageSize(int pageSize, SearchProfiles profile) {
		int rows = 0;
		int maxPageSize = getConfiguration().getMaxPageSize(profile.toString());
		if(pageSize < 0)
			rows = Query.DEFAULT_PAGE_SIZE;
		else if(pageSize > maxPageSize)
			rows = maxPageSize;
		else
			rows = pageSize;
		return rows;
	}

	private void setSearchFields(Query searchQuery, SearchProfiles profile) {
		switch (profile) {
		case FACET:
			// only facets, do not return results but how? in page based
			// searchQuery.setViewFields(new
			// String[]{SolrAnnotationFields.ANNOTATION_ID_URL});
			// searchQuery.setRows(0);
			break;

		case MINIMAL:
			searchQuery.setViewFields(new String[] { SolrAnnotationConstants.ANNO_URI });
			break;
	
		case STANDARD:
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
