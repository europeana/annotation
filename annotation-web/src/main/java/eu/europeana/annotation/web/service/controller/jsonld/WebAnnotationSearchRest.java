package eu.europeana.annotation.web.service.controller.jsonld;

import javax.servlet.http.HttpServletRequest;

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

import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.jsonld.AnnotationPageSerializer;
import eu.europeana.annotation.solr.vocabulary.search.SortFields;
import eu.europeana.annotation.solr.vocabulary.search.SortOrder;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationProfiles;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.protocol.model.AnnotationPage;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This class implements the Search Annotations - REST API
 */

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Search", description = " ")
public class WebAnnotationSearchRest extends BaseRest {

	@RequestMapping(value = { "/annotation/search", "/annotation/search.json", "/annotation/search.jsonld" }, 
			method = {RequestMethod.GET}, produces = { "application/ld+json", MediaType.APPLICATION_JSON_VALUE }
//			,consumes = "application/ld+json"
					)
	@ApiOperation(notes = SwaggerConstants.SEARCH_HELP_NOTE, value = "Search annotations", nickname = "search", response = java.lang.Void.class)
	public ResponseEntity<String> search(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PARAM_QUERY) String query,
			@RequestParam(value = WebAnnotationFields.PARAM_QF, required = false) String[] filters,
			@RequestParam(value = WebAnnotationFields.PARAM_FACET, required = false) String[] facets,
			@RequestParam(value = WebAnnotationFields.PARAM_SORT, required = false) SortFields sort,
			@RequestParam(value = WebAnnotationFields.PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
			@RequestParam(value = WebAnnotationFields.PARAM_PAGE, defaultValue = "" + Query.DEFAULT_PAGE) int page,
			@RequestParam(value = WebAnnotationFields.PARAM_PAGE_SIZE, defaultValue = ""
					+ Query.DEFAULT_PAGE_SIZE) int pageSize,
			// @RequestParam(value = WebAnnotationFields.PARAM_LIMIT) long
			// limit,
			@RequestParam(value = WebAnnotationFields.PARAM_PROFILE, defaultValue = AnnotationProfiles.STANDARD) String profile,
			HttpServletRequest request) throws HttpException {

		String action = "get:/annotation/search{.format}";

		return searchAnnotation(wskey, query, filters, facets, sort, sortOrder, page, pageSize, profile, request, action);
	}

	private ResponseEntity<String> searchAnnotation(String wskey, String queryString, String[] filters, String[] facets,
			SortFields sortField, SortOrder sortOrder, int page, int pageSize, String profile, HttpServletRequest request, String action)
					throws HttpException {

		try {

			//** 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);

			//** Process input params
			queryString = queryString.trim();
			if (StringUtils.isBlank(queryString))
				throw new ParamValidationException(ParamValidationException.MESSAGE_BLANK_PARAMETER_VALUE,
						WebAnnotationFields.PARAM_QUERY, queryString);

			String sortFieldStr = null;
			if (sortField != null)
				sortFieldStr = sortField.getSolrField();
			String sortOrderField = null; 
			if (sortFieldStr != null) //if sort field, set default value		
				sortOrderField = SortOrder.desc.name();
			if (sortOrder != null)
				sortOrderField = sortOrder.toString();
			
			SearchProfiles searchProfile = null;
			if(profile != null)
				searchProfile = SearchProfiles.valueOf(profile.toUpperCase());

			//** build search query
			Query searchQuery = getAnnotationSearchService().buildSearchQuery(queryString, filters, facets,
					sortFieldStr, sortOrderField, page, pageSize, searchProfile);

			//** do search
			AnnotationPage annotationPage = getAnnotationSearchService().search(searchQuery, request);
			
			//** serialize page
			AnnotationPageSerializer serializer = new AnnotationPageSerializer(annotationPage);
			String jsonLd = serializer.serialize(searchProfile);

			//** build response
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT + ", "+ HttpHeaders.PREFER);
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_CONTAINER);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GOH);
			headers.add(HttpHeaders.CONTENT_TYPE, "application/ld+json; profile=\"http://www.w3.org/ns/anno.jsonld\"");

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

}
