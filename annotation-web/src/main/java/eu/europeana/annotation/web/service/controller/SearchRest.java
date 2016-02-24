package eu.europeana.annotation.web.service.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.StatusLogSearchResults;
import eu.europeana.annotation.web.model.TagSearchResults;
import eu.europeana.api2.utils.JsonWebUtils;

//@Controller
//@Api(value = "search", description = "Annotation Search Rest Service")
public class SearchRest extends BaseRest {

	@RequestMapping(value = "/annotations/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes = SwaggerConstants.SEARCH_FIELDS_LINK, value = "")
	public ModelAndView searchAnnotationByField(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "value", required = true) String value,
			@RequestParam(value = "field", required = true, defaultValue = WebAnnotationFields.MULTILINGUAL) String field,
			@RequestParam(value = "language", required = true, defaultValue = WebAnnotationFields.REST_LANGUAGE) String language,
			@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
			@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit,
			@RequestParam(value = "facet", required = false) String facet) {

		value = getTypeUtils().removeTabs(value);
		value = JsonWebUtils.addFieldToQuery(value, field, language);

		// boolean withFacet = false;
		// if (StringUtils.isNotEmpty(facet) &&
		// !facet.equals(SolrAnnotationConst.ALL)) {
		// withFacet = true;
		// if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
		// String prefix = "";
		// if
		// (field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField()))
		// {
		// prefix = SolrAnnotationConst.DEFAULT_LANGUAGE +
		// SolrAnnotationConst.UNDERSCORE;
		// if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
		// prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
		// }
		// }
		// query = prefix + field + SolrAnnotationConst.DELIMETER + query;
		// }
		// }

		// if (!withFacet) {
		List<? extends Annotation> annotationList;
		AnnotationSearchResults<AbstractAnnotation> response = null;

		try {
			annotationList = getAnnotationService().searchAnnotations(value, startOn, limit);
			//response = buildSearchResponse(annotationList.getResults(), apiKey, "/annotations/search");

		} catch (AnnotationServiceException e) {
			// Logger.getLogger(getClass().getName()).error(e);
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response = buildSearchErrorResponse(apiKey, "/annotations/search", e);
		}

		// } else {
		// List<String> queries = new ArrayList<String>();
		// queries.add(SolrAnnotationConst.SolrAnnotationFields.LABEL.getSolrAnnotationField()
		// + SolrAnnotationConst.DELIMETER
		// + SolrAnnotationConst.STAR);
		// List<String> qfList = new ArrayList<String>();
		// qfList.add(facet);
		// String[] qf = qfList.toArray(new String[qfList.size()]);
		// Map<String,Integer> annotationMap =
		// getAnnotationService().getAnnotationByFacetedQuery(qf, queries);
		// if (annotationMap != null && annotationMap.size() > 0) {
		//
		// response.success = true;
		// response.requestNumber = 0L;
		// response.action = JsonUtils.mapToStringExt(annotationMap);
		// } else {
		// response.success = false;
		// response.action = "get: /annotations/search?"+ query;
		//
		// response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
		// }
		// }

		return JsonWebUtils.toJson(response, null);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tags/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes = SwaggerConstants.SEARCH_FIELDS_LINK, value = "")
	public ModelAndView searchTagByField(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "value", required = true) String value,
			@RequestParam(value = "field", required = true, defaultValue = WebAnnotationFields.MULTILINGUAL) String field,
			@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
			@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit,
			@RequestParam(value = "language", required = true, defaultValue = WebAnnotationFields.REST_LANGUAGE) String language) {

		value = getTypeUtils().removeTabs(value);
		value = JsonWebUtils.addFieldToQuery(value, field, language);

		TagSearchResults<BaseTagResource> response;
		response = new TagSearchResults<BaseTagResource>(apiKey, "/tags/search");

		try {
			response.items = (List<BaseTagResource>) getAnnotationService().searchTags(value, startOn, limit);
			response.itemsCount = response.items.size();
			response.totalResults = response.items.size();
			response.success = true;
		} catch (Exception e) {
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return JsonWebUtils.toJson(response, null);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/statuslogs/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes = SwaggerConstants.SEARCH_STATUS_FIELDS_LINK, value = "")
	public ModelAndView showStatusLogsByStatus(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "status", required = true) String status,
			@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
			@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit) {

		StatusLogSearchResults<BaseStatusLog> response;
		response = new StatusLogSearchResults<BaseStatusLog>(apiKey, "/statuslogs/search");

		try {
			response.items = (List<BaseStatusLog>) getAnnotationService().searchStatusLogs(status, startOn, limit);
			response.itemsCount = response.items.size();
			response.totalResults = response.items.size();
			response.success = true;
		} catch (Exception e) {
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return JsonWebUtils.toJson(response, null);
	}

}
