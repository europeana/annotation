package eu.europeana.annotation.web.service.controller.jsonld;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.QueryImpl;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.jsonld.AnnotationSetSerializer;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.search.QueryFilteringFields;
import eu.europeana.annotation.solr.vocabulary.search.SortFields;
import eu.europeana.annotation.solr.vocabulary.search.SortOrder;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationProfiles;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.corelib.utils.StringArrayUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This class implements the Search Annotations - REST API
 */

//@Api(value = "web-annotation-search", description = "Search API")
@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Search", description=" ")
public class WebAnnotationSearchRest extends BaseRest {

	@RequestMapping(value = { "/annotation/search",
			"/annotation/search.json", "/annotation/search.jsonld" }, method = {RequestMethod.GET,RequestMethod.POST}, produces = { "application/ld+json",
					MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(notes = SwaggerConstants.SEARCH_HELP_NOTE, value = "Search annotations", nickname = "search", response = java.lang.Void.class)
	public ResponseEntity<String> search(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PARAM_QUERY) String query,
			@RequestParam(value = WebAnnotationFields.PARAM_QF, required = false) String[] filters,
			@RequestParam(value = WebAnnotationFields.PARAM_FACET, required = false) String[] facets,
			@RequestParam(value = WebAnnotationFields.PARAM_PROFILE, defaultValue = AnnotationProfiles.STANDARD) String profile,
			@RequestParam(value = WebAnnotationFields.PARAM_START, defaultValue = Query.DEFAULT_START) int start,
			@RequestParam(value = WebAnnotationFields.PARAM_ROWS, defaultValue = Query.DEFAULT_PAGE_SIZE) int rows,
			@RequestParam(value = WebAnnotationFields.PARAM_SORT, required = false) SortFields sort,
			@RequestParam(value = WebAnnotationFields.PARAM_SORT_ORDER, required = false) SortOrder sortOrder
			) throws HttpException {

		String action = "get:/annotation/search{.format}";		

		return searchAnnotation(wskey, query, filters, facets, profile, start, rows, action, sort, sortOrder);
	}
	
	private ResponseEntity<String> searchAnnotation(String wskey, String queryString, String[] filters, String[] facets, String profile,
			int start, int rows, String action, SortFields sortField, SortOrder sortOrder
			) throws HttpException {
					
		try {

			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			queryString = queryString.trim();
	        if (StringUtils.isBlank(queryString))
	        	throw new ParamValidationException(ParamValidationException.MESSAGE_BLANK_PARAMETER_VALUE,
	        			WebAnnotationFields.PARAM_QUERY, queryString);
	        
	        SearchProfiles searchProfile = SearchProfiles.valueOf(profile.toUpperCase());

	        String sortFieldStr = null;
			if (sortField != null)
				sortFieldStr = sortField.getSolrField();				
			//set default value
			String sortOrderField = SortOrder.desc.name();
			if (sortOrder != null)
				sortOrderField = sortOrder.toString();		
			
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

	        Query searchQuery = buildSearchQuery(
	        		queryString, filters, facets, start, rows, searchProfile, sortFieldStr, sortOrderField);
	        ResultSet<? extends AnnotationView> results = getAnnotationSearchService().search(searchQuery);
	        AnnotationSetSerializer serializer = new AnnotationSetSerializer(results);
	        String jsonLd = serializer.serialize(searchProfile);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_CONTAINER);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET+"," +  HttpHeaders.ALLOW_POST);

			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

			return response;

		} catch (RuntimeException e) {
			// not found ..
			throw new InternalServerException(e);
		} catch (HttpException e) {
			// avoid wrapping http exception
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}

	protected Query buildSearchQuery(String queryString, String[] filters, String[] facets, int start, int rows
			, SearchProfiles profile, String sort, String sortOrder) {
		
		//TODO: check if needed
        String[] normalizedFacets = StringArrayUtils.splitWebParameter(facets);
		boolean isFacetsRequested = isFacetsRequest(normalizedFacets);

		Query searchQuery = new QueryImpl();
		searchQuery.setQuery(queryString);
		//start 1 in query = start 0 in solr 
		searchQuery.setStart(start>0? start -1: start);
		searchQuery.setRows(Math.min(rows, Query.MAX_PAGE_SIZE));
		if(isFacetsRequested)
			searchQuery.setFacetFields(normalizedFacets);
		
		searchQuery.setFilters(filters);
		
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
			//only facets, do not return results  
			//searchQuery.setViewFields(new String[]{SolrAnnotationFields.ANNOTATION_ID_URL});
			searchQuery.setRows(0);
			break;

		case STANDARD:
			searchQuery.setViewFields(new String[]{SolrAnnotationConstants.ANNOTATION_ID_URL});
			break;

		default:
			//TODO: consider throwing an exception
			break;
		}
		
	}

	protected boolean isFacetsRequest(String[] facets) {
		return facets != null && facets.length > 0;
	}

}
