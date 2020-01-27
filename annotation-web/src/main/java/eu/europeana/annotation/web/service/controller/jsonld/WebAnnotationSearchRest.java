package eu.europeana.annotation.web.service.controller.jsonld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.vocabulary.search.SortFields;
import eu.europeana.annotation.solr.vocabulary.search.SortOrder;
import eu.europeana.annotation.utils.serialize.AnnotationPageSerializer;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This class implements the Search Annotations - REST API
 */

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Search", description = " ")
public class WebAnnotationSearchRest extends BaseRest {
	
	private static Logger logger = LogManager.getRootLogger();

	@RequestMapping(value = { "/annotation/search", "/annotation/search.json", "/annotation/search.jsonld" }, 
			method = {RequestMethod.GET},
			produces = {  HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8 }
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
			@RequestParam(value = WebAnnotationFields.PARAM_PROFILE, required = false) String profile,
			@RequestParam(value = WebAnnotationFields.LANGUAGE, required = false) String language,
			HttpServletRequest request) throws HttpException {

		String action = "get:/annotation/search{.format}";

		return searchAnnotation(wskey, query, filters, facets, sort, sortOrder, page, pageSize, profile, request, action, language);
	}

	private ResponseEntity<String> searchAnnotation(String wskey, String queryString, String[] filters, String[] facets,
			SortFields sortField, SortOrder sortOrder, int page, int pageSize, String profile, HttpServletRequest request, 
			String action, String language)
					throws HttpException {

		try {

			//** 2. Check client access (a valid “wskey” must be provided)
			verifyReadAccess(request);
			
			//** Process input params
			queryString = queryString.trim();
			if (StringUtils.isBlank(queryString))
				throw new ParamValidationException(ParamValidationException.MESSAGE_BLANK_PARAMETER_VALUE,
						I18nConstants.ANNOTATION_VALIDATION,
						new String[]{WebAnnotationFields.PARAM_QUERY, queryString});

			String sortFieldStr = null;
			if (sortField != null)
				sortFieldStr = sortField.getSolrField();
			String sortOrderField = null; 
			if (sortFieldStr != null) //if sort field, set default value		
				sortOrderField = SortOrder.desc.name();
			if (sortOrder != null)
				sortOrderField = sortOrder.toString();
			
			SearchProfiles searchProfile = getProfile(profile, request);
			
			// here we need a query search profile - dereference is not a query search profile - we use default
			SearchProfiles querySearchProfile = searchProfile;
			if (SearchProfiles.DEREFERENCE.equals(searchProfile)) {
				querySearchProfile = SearchProfiles.STANDARD;
			}

			//** build search query
			Query searchQuery = getAnnotationSearchService().buildSearchQuery(queryString, filters, facets,
					sortFieldStr, sortOrderField, page, pageSize, querySearchProfile);

			//** do search
			AnnotationPage annotationPage = getAnnotationSearchService().search(searchQuery, request);
			
			applyDereferenceProfile(language, searchProfile, annotationPage);
			
			//** serialize page
			AnnotationPageSerializer serializer = new AnnotationPageSerializer(annotationPage);
			String jsonLd = serializer.serialize(searchProfile);

			//** build response
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT + ", "+ HttpHeaders.PREFER);
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
			headers.add(HttpHeaders.LINK, AnnotationHttpHeaders.VALUE_CONSTRAINTS);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
			headers.add(HttpHeaders.CONTENT_TYPE, AnnotationHttpHeaders.VALUE_LDP_CONTENT_TYPE);

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

	private void applyDereferenceProfile(String language, SearchProfiles searchProfile, AnnotationPage annotationPage)
			throws IOException, JsonParseException, HttpException {
		if (SearchProfiles.DEREFERENCE.equals(searchProfile)) {
			List<Annotation> annotationsWithProfile = new ArrayList<Annotation>();
			List<? extends Annotation> annotations = annotationPage.getAnnotations();
			for (Annotation annotation : annotations) {
				Annotation annotationWithProfile = getAnnotationService().addProfileData(annotation, searchProfile, language);
				annotationsWithProfile.add(annotationWithProfile);
			}
			annotationPage.setAnnotations(annotationsWithProfile);
		}
	}
	
	private SearchProfiles getProfile(String profile, HttpServletRequest request) {
		
		// if the profile parameter is given, the header preference is ignored
		if(profile != null) {
			logger.trace("Profile set by profile parameter: " + profile);
			return SearchProfiles.valueOf(profile.toUpperCase());
		}
		
		String preferHeader = request.getHeader(HttpHeaders.PREFER);
		if(preferHeader != null) {
			logger.trace("'Prefer' header value: " + preferHeader);
			preferHeader = preferHeader.replaceAll("\\s+","");
			logger.trace("Normalized 'Prefer' header value: " + preferHeader);
			if(preferHeader.equals(AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDIRIS)) {
				logger.trace("MINIMAL Profile set by 'Prefer' header value: " + AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDIRIS);
				return SearchProfiles.MINIMAL;
			} else if(preferHeader.equals(AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDDESCRIPTIONS)) {
				logger.trace("STANDARD Profile set by 'Prefer' header value: " + AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDDESCRIPTIONS);
				return SearchProfiles.STANDARD;
			} 
		}
		
		logger.trace("STANDARD Profile set by default");
		return SearchProfiles.STANDARD;
		
			
	}

}