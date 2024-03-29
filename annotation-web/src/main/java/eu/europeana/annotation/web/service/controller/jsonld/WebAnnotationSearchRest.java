package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;
import eu.europeana.annotation.solr.vocabulary.search.SortFields;
import eu.europeana.annotation.solr.vocabulary.search.SortOrder;
import eu.europeana.annotation.utils.GeneralUtils;
import eu.europeana.annotation.utils.serialize.AnnotationPageSerializer;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.I18nConstantsAnnotation;
import eu.europeana.api.commons.definitions.config.i18n.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.api.commons.web.exception.ParamValidationException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This class implements the Search Annotations - REST API
 */

@RestController
@Api(tags = "Web Annotation Search", description = " ")
public class WebAnnotationSearchRest extends BaseRest {

    private static Logger logger = LogManager.getRootLogger();

    @RequestMapping(value = { "/annotation/search", "/annotation/search.json", "/annotation/search.jsonld" }, method = 
	    RequestMethod.GET, produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8 }
//			,consumes = "application/ld+json"
    )
    @ApiOperation(notes = SwaggerConstants.SEARCH_HELP_NOTE, 
    value = "Search annotations for the given text query. By default the search will return all annotation fields. "
        + "The facet param refers to a list of fields the facets is calculated upon. "
        + "The filters and lang params are used to reduce the amount of data included in the response. ",
    nickname = "search", response = Void.class)
    public ResponseEntity<String> search(
	    @RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String wskey,
	    @RequestParam(value = WebAnnotationFields.PARAM_QUERY) String query,
	    @RequestParam(value = WebAnnotationFields.PARAM_QF, required = false) String[] filters,
	    @RequestParam(value = WebAnnotationFields.PARAM_FACET, required = false) String[] facets,
	    @RequestParam(value = WebAnnotationFields.PARAM_SORT, required = false) SortFields sort,
	    @RequestParam(value = WebAnnotationFields.PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
	    @RequestParam(value = WebAnnotationFields.PARAM_PAGE, defaultValue = "" + Query.DEFAULT_PAGE) int page,
	    @RequestParam(value = WebAnnotationFields.PARAM_PAGE_SIZE, defaultValue = ""
		    + Query.DEFAULT_PAGE_SIZE) int pageSize,
	    @RequestParam(value = WebAnnotationFields.PARAM_PROFILE, required = false) String profile,
	    @RequestParam(value = WebAnnotationFields.LANGUAGE, required = false) String language,
	    HttpServletRequest request) throws HttpException {
	//		String action = "get:/annotation/search{.format}";
	// ** 2. Check client access (a valid “wskey” must be provided)
	verifyReadAccess(request);

	return searchAnnotation(wskey, query, filters, facets, sort, sortOrder, page, pageSize, profile, request,
		language);
    }

    private ResponseEntity<String> searchAnnotation(String wskey, String queryString, String[] filters, String[] facets,
	    SortFields sortField, SortOrder sortOrder, int page, int pageSize, String profile,
	    HttpServletRequest request, String language) throws HttpException {

	try {

	    // ** Process input params
	    queryString = queryString.trim();
	    if (StringUtils.isBlank(queryString))
		throw new ParamValidationI18NException(ParamValidationI18NException.MESSAGE_BLANK_PARAMETER_VALUE,
			I18nConstantsAnnotation.ANNOTATION_VALIDATION,
			new String[] { WebAnnotationFields.PARAM_QUERY, queryString });

	    SearchProfiles searchProfile = getProfile(profile, request);
	    // here we need a query search profile - dereference is not a query search
	    // profile - we use default
	    SearchProfiles querySearchProfile = searchProfile;
	    if (SearchProfiles.DEREFERENCE.equals(searchProfile)) {
		querySearchProfile = SearchProfiles.STANDARD;
	    }
	    
	    //process facets profile
	    if(!StringUtils.contains(profile, SearchProfiles.FACETS.toString())) {
	      facets=null;
	    }

	    String sortFieldStr = null;
	    if (sortField != null)
		sortFieldStr = sortField.getSolrField();
	    String sortOrderField = null;
	    if (sortFieldStr != null) // if sort field, set default value
		sortOrderField = SortOrder.desc.name();
	    if (sortOrder != null)
		sortOrderField = sortOrder.toString();

	    // ** build search query
	    Query searchQuery = getAnnotationSearchService().buildSearchQuery(queryString, filters, facets,
		    sortFieldStr, sortOrderField, page, pageSize, querySearchProfile);

	    // ** do search
	    AnnotationPage annotationPage = getAnnotationSearchService().search(searchQuery, request);

	    if(annotationPage.getAnnotations() != null && SearchProfiles.DEREFERENCE.equals(searchProfile)) {
		getAnnotationService().dereferenceSemanticTags(annotationPage.getAnnotations(), SearchProfiles.DEREFERENCE, language);
	    }

	    // ** serialize page
        AnnotationPageSerializer serializer = new AnnotationPageSerializer(annotationPage, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = serializer.serialize(querySearchProfile);
        
	    // ** build response
	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT + ", " + HttpHeaders.PREFER);
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

    private void validateProfiles(List<String> profiles) throws HttpException {
      /*
       *  Examples of profile combinations that are possible: profile=facets OR profile=facets,minimal OR profile=standard,facets
       *  OR profile=minimal,facets,debug. 
       */
      if ((profiles.contains(SearchProfiles.MINIMAL.toString()) && profiles.contains(SearchProfiles.STANDARD.toString())) ||
          (profiles.contains(SearchProfiles.MINIMAL.toString()) && profiles.contains(SearchProfiles.DEREFERENCE.toString())) ||
          (profiles.contains(SearchProfiles.STANDARD.toString()) && profiles.contains(SearchProfiles.DEREFERENCE.toString()))
          ) {
          throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, I18nConstants.INVALID_PARAM_VALUE,
                  new String[]{"These profiles are not supported together ", StringUtils.join(profiles, ",")});
      }
      //check the profile names
      for(String profile : profiles) {
        if(!SearchProfiles.contains(profile)) {
          throw new ParamValidationI18NException(I18nConstants.INVALID_PARAM_VALUE,
              I18nConstants.INVALID_PARAM_VALUE,
              new String[] { WebAnnotationFields.PARAM_PROFILE, profile });
        }
      }
  }

    private SearchProfiles getProfile(String profile, HttpServletRequest request) throws HttpException {
      List<String> allProfiles = GeneralUtils.splitStringIntoList(profile, WebAnnotationModelFields.COMMA);
      validateProfiles(allProfiles);      
      allProfiles.remove(SearchProfiles.FACETS.toString());
      allProfiles.remove(SearchProfiles.DEBUG.toString());
      if(!allProfiles.isEmpty()) {
        return SearchProfiles.getByStr(allProfiles.get(0));
      }

      String preferHeader = request.getHeader(HttpHeaders.PREFER);
      if (preferHeader != null) {
        preferHeader = preferHeader.replaceAll("\\s+", "");
    	if (preferHeader.equals(AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDIRIS)) {
    	  return SearchProfiles.MINIMAL;
    	} else if (preferHeader.equals(AnnotationHttpHeaders.VALUE_PREFER_CONTAINEDDESCRIPTIONS)) {
    	  return SearchProfiles.STANDARD;
    	}
      }

      logger.trace("STANDARD Profile set by default");
      return SearchProfiles.STANDARD;
    }
}
