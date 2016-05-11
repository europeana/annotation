package eu.europeana.annotation.web.service.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.api2.utils.JsonWebUtils;

//@Controller
//@Api(value = "annotations", description = "Annotation JSON Rest Service")
@Deprecated
public class AnnotationRest extends BaseRest {

/*
//	@ApiOperation(value = "", position = 0)
	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String getComponentName() {
		return getConfiguration().getComponentName();
	}

//	@ApiOperation(value = "", position = 1)
	@RequestMapping(value = "/annotations/{collection}/{object}/{provider}/{annotationNr}.json"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotation (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier
		) throws AnnotationNotFoundException {

		//String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/collection/object/provider/annotationNr.json");

		if (annotation != null) {

			response = new AnnotationOperationResponse(
					apiKey, "/annotations/collection/object/provider/annotationNr.json");
			
			response.success = true;
//			response.requestNumber = 0L;

			response.setAnnotation(getAnnotationBuilder().copyIntoWebAnnotation(
					annotation));
		}else{
			String errorMessage = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
			String action = "get: /annotations/"+collection + WebAnnotationFields.SLASH
					+ object + WebAnnotationFields.SLASH + identifier+WebAnnotationFields.SLASH + provider + ".json";
			
			response = buildErrorResponse(errorMessage, action, apiKey);
		}
		
		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/annotations/{provider}/{annotationNr}.json"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationByProvider (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier
		) throws AnnotationNotFoundException {

		Annotation annotation = getAnnotationService().getAnnotationById(
				new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/provider/annotationNr.json");

		if (annotation != null) {

			response = new AnnotationOperationResponse(
					apiKey, "/annotations/provider/annotationNr.json");
			
			response.success = true;

			response.setAnnotation(getAnnotationBuilder().copyIntoWebAnnotation(
					annotation));
		}else{
			String errorMessage = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
			String action = "get: /annotations/"+ provider + WebAnnotationFields.SLASH + identifier + ".json";
			
			response = buildErrorResponse(errorMessage, action, apiKey);
		}
		
		return JsonWebUtils.toJson(response, null);
	}
	

//	@RequestMapping(value = "/annotations/{collection}/{object}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
////	@ApiOperation(value = "2", position = 2)
//	public ModelAndView getAnnotationList (
//		@RequestParam(value = "apiKey", required = false) String apiKey,
//		@RequestParam(value = "profile", required = false) String profile,
//		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
//		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object
//		) {
//		
//		String resourceId = toResourceId(collection, object);
//		List<? extends Annotation> annotations = getAnnotationService()
//				.getAnnotationList(resourceId);
//		
//		String action = "/annotations/collection/object.json";
//		
//		AnnotationSearchResults<AbstractAnnotation> response = buildSearchResponse(
//				annotations, apiKey, action);
//
//		return JsonWebUtils.toJson(response, null);
//	}

//	@RequestMapping(value = "/annotations/{collection}/{object}/{provider}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/annotations/col/{collection}/{object}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getAnnotationList (
//			public ModelAndView getAnnotationListByProvider (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
//		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider
		@RequestParam(value = "provider", required = false) String provider
		) {
		
		String resourceId = toResourceId(collection, object);
		List<? extends Annotation> annotations = getAnnotationService()
				.getAnnotationListByProvider(resourceId, provider);
		
		String action = "/annotations/collection/object/provider.json";
		
		AnnotationSearchResults<AbstractAnnotation> response = buildSearchResponse(
				annotations, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}

//	@RequestMapping(value = "/annotations/{collection}/{object}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/annotations/col/{collection}/{object}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
//	@ApiOperation(notes=SwaggerConstants.SAMPLES_JSON_LINK, value="")
	public ModelAndView createAnnotation (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
		@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
		@RequestParam(value = "identifier", required = false) String identifier,
		@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
		@RequestBody String annotation) {
//		@RequestBody @RequestParam(value = "annotation", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_JSON) String jsonAnno) {

		String action = "create:/annotations/collection/object.json";
		
		//parse
		Annotation webAnnotation = JsonUtils.toAnnotationObject(annotation);

		//validate input params
		if (!getAnnotationIdHelper().validateResouceId(webAnnotation, collection, object))
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_RESOURCE_ID_DOES_NOT_MATCH, null, false);
		if (!getAnnotationIdHelper().validateProvider(webAnnotation, provider)) 
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_PROVIDER_DOES_NOT_MATCH, null, false);
		AnnotationIdHelper r = getAnnotationIdHelper();

		//initialize
		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
//		.initializeAnnotationId(collection, object, provider, webAnnotation.getSameAs());
				
		webAnnotation.setAnnotationId(annoId);		
		
		if (webAnnotation.getBody() != null 
				&& webAnnotation.getBody().getLanguage() != null
				&& webAnnotation.getBody().getValue() != null
				&& webAnnotation.getBody().getHttpUri() == null // only for simple tag
				&& webAnnotation.getBody().getMultilingual().size() == 0) {
			webAnnotation.getBody().getMultilingual().put(
					webAnnotation.getBody().getLanguage(), webAnnotation.getBody().getValue());
		}
		
		//store				
		Annotation storedAnnotation = getAnnotationService().storeAnnotation(
				webAnnotation, indexing);

		//build response
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, action);
		response.success = true;
//		response.requestNumber = 0L;

		response.setAnnotation(getAnnotationBuilder().copyIntoWebAnnotation(
				storedAnnotation));

		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{provider}/{annotationNr}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
//	@ApiOperation(notes=SwaggerConstants.SAMPLES_JSON_LINK, value="")
	public ModelAndView createAnnotationByProvider (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
		@RequestParam(value = "identifier", required = false) String identifier,
		@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
		@RequestBody String annotation) {

		String action = "create:/annotations/provider/annotationNr.json";
		
		try {
			//parse
			Annotation webAnnotation = JsonUtils.toAnnotationObject(annotation);
	
			//validate input params
			AnnotationId annoId = buildAnnotationId(provider, identifier);
			
			// check whether annotation vor given provider and annotationNr already exist in database
			if (getAnnotationService().existsInDb(annoId)) 
				return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_ANNOTATION_EXISTS_IN_DB + annoId.toHttpUrl(), null, false);			
			
			
	//		if (!getAnnotationIdHelper().validateProvider(webAnnotation, provider)) 
	//			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_PROVIDER_DOES_NOT_MATCH);
	//
	//		//initialize
	//		AnnotationId annoId = getAnnotationIdHelper()
	//				.initializeAnnotationId(provider, annotationNr);
					
			webAnnotation.setAnnotationId(annoId);		
			
			if (webAnnotation.getBody() != null 
					&& webAnnotation.getBody().getLanguage() != null
					&& webAnnotation.getBody().getValue() != null
					&& webAnnotation.getBody().getHttpUri() == null // only for simple tag
					&& webAnnotation.getBody().getMultilingual().size() == 0) {
				webAnnotation.getBody().getMultilingual().put(
						webAnnotation.getBody().getLanguage(), webAnnotation.getBody().getValue());
			}
			
			//store				
			Annotation storedAnnotation = getAnnotationService().storeAnnotation(
					webAnnotation, indexing);
	
			//build response
			AnnotationOperationResponse response = new AnnotationOperationResponse(
					apiKey, action);
			response.success = true;
	
			response.setAnnotation(getAnnotationBuilder().copyIntoWebAnnotation(
					storedAnnotation));
	
			return JsonWebUtils.toJson(response, null);
		} catch (Exception e) {
			return getValidationReport(apiKey, action, e.getMessage(), e, false);		
		}
	}
*/
	
}
