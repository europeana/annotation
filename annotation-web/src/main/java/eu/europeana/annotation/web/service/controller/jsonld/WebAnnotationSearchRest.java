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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.QueryImpl;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.jsonld.AnnotationSetSerializer;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationFields;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationProfiles;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.corelib.utils.StringArrayUtils;

/**
 * This class implements the Search Annotations - REST API
 */

@Controller
@Api(value = "web-annotation-search", description = "Search API")
public class WebAnnotationSearchRest extends BaseRest {

	@RequestMapping(value = { "/annotation/search",
			"/annotation/search.jsonld" }, method = {RequestMethod.GET,RequestMethod.POST}, produces = { "application/ld+json",
					MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(notes = SwaggerConstants.SEARCH_HELP_NOTE, value = "search")
	public ResponseEntity<String> search(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PARAM_QUERY) String query,
			@RequestParam(value = WebAnnotationFields.PARAM_QF, required = false) String[] filters,
			@RequestParam(value = WebAnnotationFields.PARAM_FACET, required = false) String[] facets,
			@RequestParam(value = WebAnnotationFields.PARAM_PROFILE, required = false, defaultValue = AnnotationProfiles.STANDARD) String profile,
			@RequestParam(value = WebAnnotationFields.PARAM_START, required = false, defaultValue = Query.DEFAULT_START) int start,
			@RequestParam(value = WebAnnotationFields.PARAM_ROWS, required = false, defaultValue = Query.DEFAULT_PAGE_SIZE) int rows) throws HttpException {

		String action = "get:/annotation/search{.format}";
		return searchAnnotation(wskey, query, filters, facets, profile, start, rows, action);
	}

	private ResponseEntity<String> searchAnnotation(String wskey, String queryString, String[] filters, String[] facets, String profile,
			int start, int rows, String action) throws HttpException {

		
		try {

			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			queryString = queryString.trim();
	        if (StringUtils.isBlank(queryString))
	        	throw new ParamValidationException(ParamValidationException.MESSAGE_BLANK_PARAMETER_VALUE,
	        			WebAnnotationFields.PARAM_QUERY, queryString);
	        
	        SearchProfiles searchProfile = SearchProfiles.valueOf(profile.toUpperCase());
			
	        Query searchQuery = buildSearchQuery(queryString, filters, facets, start, rows, searchProfile);
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

	protected Query buildSearchQuery(String queryString, String[] filters, String[] facets, int start, int rows, SearchProfiles profile) {
		
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
			searchQuery.setViewFields(new String[]{SolrAnnotationFields.ANNOTATION_ID_URL});
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
