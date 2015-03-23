package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.TagSearchResults;
import eu.europeana.api2.utils.JsonWebUtils;

@Controller
//@EnableWebMvc
//@EnableSwagger
//@ComponentScan("com.myapp.packages")

//@Path("rest")
@Api(value = "annotations", description = "Annotation Rest Service")
public class AnnotationRest extends BaseRest {


//	@GET
//	@Path("/annotate")
//	@ApiOperation(value = "Get component name", response = String.class)
//	@Produces({ "text/*"})

//	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = "text/*")
	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName();
	}

	private String toResourceId(String collection, String object) {
		return "/"+ collection +"/" + object;
	}
	
	@RequestMapping(value = "/annotations/{collection}/{object}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getAnnotationList(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {
		
		String resourceId = toResourceId(collection, object);
		List<? extends Annotation> annotations = getAnnotationService()
				.getAnnotationList(resourceId);
		
		String action = "/annotations/collection/object.json";
		
		AnnotationSearchResults<AbstractAnnotation> response = buildSearchResponse(
				annotations, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}

	private AnnotationSearchResults<AbstractAnnotation> buildSearchResponse(
			List<? extends Annotation> annotations, String apiKey, String action) {
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, action);
		response.items = new ArrayList<AbstractAnnotation>(annotations.size());

		AbstractAnnotation webAnnotation;
		for (Annotation annotation : annotations) {
			webAnnotation = getControllerHelper().copyIntoWebAnnotation(annotation);
			response.items.add(webAnnotation);
		}
		response.itemsCount = response.items.size();
		response.totalResults = annotations.size();
		return response;
	}

	private AnnotationSearchResults<AbstractAnnotation> buildSearchErrorResponse(
			String apiKey, String action, Throwable th) {
		
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, action);
		response.success = false;
		response.error = th.getMessage();
//		response.requestNumber = 0L;
		
		return response;
	}
	
	
	@RequestMapping(value = "/annotations/{collection}/{object}/{annotationNr}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotation(@PathVariable String collection,
			@PathVariable String object, @PathVariable Integer annotationNr,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, annotationNr);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/collection/object/annotationNr.json");

		if (annotation != null) {

			response = new AnnotationOperationResponse(
					apiKey, "/annotations/collection/object/annotationNr.json");
			
			response.success = true;
//			response.requestNumber = 0L;

			response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
					annotation));
		}else{
			String errorMessage = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
			String action = "get: /annotations/"+collection+"/"
					+object+"/"+annotationNr+".json";
			
			response = buildErrorResponse(errorMessage, action, apiKey);
		}
		
		return JsonWebUtils.toJson(response, null);
	}

	AnnotationOperationResponse buildErrorResponse(String errorMessage,
			String action, String apiKey) {
		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, action);
		
		response.success = false;
		response.error = errorMessage;
		return response;
	}

	@RequestMapping(value = "/annotations/{collection}/{object}/{annotationNr}.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationLd(@PathVariable String collection,
			@PathVariable String object, @PathVariable Integer annotationNr,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, annotationNr);
		
		AnnotationLd annotationLd = new AnnotationLd(annotation);
        String jsonLd = annotationLd.toString(4);
       	
		return JsonWebUtils.toJson(jsonLd, null);
	}

	
	@RequestMapping(value = "/annotations/{collection}/{object}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotation(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestBody @RequestParam(value = "annotation", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_JSON) String jsonAnno) {

		Annotation webAnnotation = JsonUtils.toAnnotationObject(jsonAnno);
		if (webAnnotation.getBody() != null 
				&& webAnnotation.getBody().getLanguage() != null
				&& webAnnotation.getBody().getValue() != null
				&& webAnnotation.getBody().getHttpUri() == null // only for simple tag
				&& webAnnotation.getBody().getMultilingual().size() == 0) {
			webAnnotation.getBody().getMultilingual().put(
					webAnnotation.getBody().getLanguage(), webAnnotation.getBody().getValue());
		}
		Annotation persistantAnnotation = getControllerHelper()
				.copyIntoPersistantAnnotation(webAnnotation, apiKey);

		Annotation storedAnnotation = getAnnotationService().createAnnotation(
				persistantAnnotation);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "create:/annotations/collection/object.json");
		response.success = true;
//		response.requestNumber = 0L;

		response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
				storedAnnotation));

		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{collection}/{object}.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotationLd(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestBody @RequestParam(value = "annotation", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_JSON_LD) String jsonAnno) {

		Annotation storedAnnotation = getAnnotationService().createAnnotation(jsonAnno);

		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation resAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);

		AnnotationLd annotationLd = new AnnotationLd(resAnnotation);
        String jsonLd = annotationLd.toString(4);
	
		return JsonWebUtils.toJson(jsonLd, null);
	}

	//@ApiModelProperty(allowableValues="", access="hidden", hidden=true, required=false)
	//@ApiOperation()
	@ApiOperation(
			    value = "List all people",
			    notes = "List all people using paging",
			    response = Annotation.class,
			    responseContainer = "List"
			)
	@RequestMapping(value = "/annotations/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView searchAnnotationByField(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "value", required = true) String value,
			@RequestParam(value = "field", required = true, defaultValue = WebAnnotationFields.MULTILINGUAL) String field,
			@RequestParam(value = "language", required = true, defaultValue = WebAnnotationFields.REST_LANGUAGE) String language,
			@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
			@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit,
			@RequestParam(value = "facet", required = false) String facet) {

		value = getTypeUtils().removeTabs(value);
		value = JsonWebUtils.addFieldToQuery(value, field, language);
		
//		boolean withFacet = false;
//		if (StringUtils.isNotEmpty(facet) && !facet.equals(SolrAnnotationConst.ALL)) {
//			withFacet = true;
//			if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
//				String prefix = "";
//				if (field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
//					prefix = SolrAnnotationConst.DEFAULT_LANGUAGE + SolrAnnotationConst.UNDERSCORE;
//					if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
//						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
//					}
//				}
//				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
//			}
//		}

//		if (!withFacet) {
			List<? extends Annotation> annotationList;
			AnnotationSearchResults<AbstractAnnotation> response;
			
			try {
				annotationList = getAnnotationService().searchAnnotations(value, startOn, limit);
				response = buildSearchResponse(
						annotationList, apiKey, "/annotations/search");
				
			} catch (AnnotationServiceException e) {
//				Logger.getLogger(getClass().getName()).error(e);
				Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
				response = buildSearchErrorResponse(apiKey, "/annotations/search", e);
			}

			
//		} else {
//			List<String> queries = new ArrayList<String>();
//			queries.add(SolrAnnotationConst.SolrAnnotationFields.LABEL.getSolrAnnotationField() 
//					+ SolrAnnotationConst.DELIMETER
//					+ SolrAnnotationConst.STAR);
//			List<String> qfList = new ArrayList<String>();
//			qfList.add(facet);
//			String[] qf = qfList.toArray(new String[qfList.size()]);
//			Map<String,Integer> annotationMap = getAnnotationService().getAnnotationByFacetedQuery(qf, queries);
//			if (annotationMap != null && annotationMap.size() > 0) {
//
//				response.success = true;
//				response.requestNumber = 0L;
//				response.action = JsonUtils.mapToStringExt(annotationMap);
//			} else {
//				response.success = false;
//				response.action = "get: /annotations/search?"+ query;
//				
//				response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
//			}
//		}
				
		return JsonWebUtils.toJson(response, null);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tags/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView searchTagByField(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "value", required = true) String value,
			@RequestParam(value = "field", required = true, defaultValue = WebAnnotationFields.MULTILINGUAL) String field,
			@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
			@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit,
			@RequestParam(value = "language", required = true, defaultValue = WebAnnotationFields.REST_LANGUAGE) String language) {

		value = getTypeUtils().removeTabs(value);
		value = JsonWebUtils.addFieldToQuery(value, field, language);

		TagSearchResults<BaseTagResource> response;
		response = new TagSearchResults<BaseTagResource>(
				apiKey, "/tags/search");
		
		try{
			response.items = (List<BaseTagResource>) getAnnotationService().searchTags(value, startOn, limit);
			response.itemsCount = response.items.size();
			response.totalResults = response.items.size();
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return JsonWebUtils.toJson(response, null);
	}

}
