package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
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
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConst;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;


@Controller
@Api(value = "Web Annotation Admin", description = "Annotation Management Rest Service")
public class ManagementRest extends BaseRest {

//	@GET
//	@RequestMapping(value = "/admin/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
//	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "-admin";
	}

//	@DELETE
//	@RequestMapping(value = "/admin/annotation/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public AnnotationOperationResponse deleteAnnotationById(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "identifier", required = true) String identifier,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/annotation/delete");
			
		try{
			getAnnotationService().deleteAnnotation(new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

//	@PUT
//	@RequestMapping(value = "/admin/index", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public AnnotationOperationResponse indexAnnotationById(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true) String identifier) {

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/annotations/admin/index");
			
		try{
			getAnnotationService().indexAnnotation(new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));
//			getAnnotationService().indexAnnotation(resourceId, provider, Long.valueOf(query));
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

//	@PUT
//	@RequestMapping(value = "/admin/index/provider", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public AnnotationOperationResponse indexAnnotationByIdAndProvider(
//		@RequestParam(value = "apiKey", required = false) String apiKey,
//		@RequestParam(value = "profile", required = false) String profile,
//		@RequestParam(value = "query", required = true) String query,
//		@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId,
//		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider) {
//
//		AnnotationOperationResponse response;
//		response = new AnnotationOperationResponse(
//				apiKey, "/annotations/admin/index/provider");
//			
//		try{
//			getAnnotationService().indexAnnotation(resourceId, provider, Integer.valueOf(query));
//			response.success = true;
//		} catch (Exception e){
//			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
//			response.success = false;
//			response.error = e.getMessage();
//		}
//
//		return response;
//	}

//	@PUT
//	@RequestMapping(value = "/admin/annotation/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value = "/admin/annotation/disable/{provider}/{annotationNr}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public AnnotationOperationResponse disableAnnotationById(
//		@RequestParam(value = "apiKey", required = false) String apiKey,
//		@RequestParam(value = "profile", required = false) String profile,
//		@PathVariable(value = "provider") String provider,
//		@PathVariable(value = "identifier") String identifier) {
//
//
//		AnnotationOperationResponse response;
//		response = new AnnotationOperationResponse(
//				apiKey, "/admin/annotation/disable");
//			
//		try{
//			getAnnotationService().disableAnnotation(new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));
//			response.success = true;
//		} catch (Exception e){
//			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
//			response.success = false;
//			response.error = e.getMessage();
//		}
//
//		return response;
//	}

//	@DELETE
//	@RequestMapping(value = "/admin/tag/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public AnnotationOperationResponse deleteTagById(
//		@RequestParam(value = "apiKey", required = false) String apiKey,
//		@RequestParam(value = "profile", required = false) String profile,
//		@RequestParam(value = "query", required = true, defaultValue = WebAnnotationFields.REST_TAG_ID) String query) {
//
//		AnnotationOperationResponse response;
//		response = new AnnotationOperationResponse(
//				apiKey, "/admin/tag/delete");
//			
//		try{
//			getAnnotationService().deleteTag(query);
//			response.success = true;
//		} catch (Exception e){
//			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
//			response.success = false;
//			response.error = e.getMessage();
//		}
//
//		return response;
//	}

//	@RequestMapping(value = "/admin/disabled/{collection}/{object}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getDisabledAnnotationList(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
		@RequestParam(value = "startOn", required = true, defaultValue = WebAnnotationFields.REST_START_ON) String startOn,
		@RequestParam(value = "limit", required = true, defaultValue = WebAnnotationFields.REST_LIMIT) String limit) {
		
		String resourceId = toResourceId(collection, object);
		List<? extends Annotation> annotations = getAnnotationService()
				.getFilteredAnnotationList(resourceId, startOn, limit, true);
		
		String action = "/admin/disabled/collection/object.json";
		
		AnnotationSearchResults<AbstractAnnotation> response = buildSearchResponse(
				annotations, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}
	
	/*@RequestMapping(value = "/providers/{idGeneration}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getProviderList (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "idGeneration", required = true, defaultValue = WebAnnotationFields.REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE) String idGeneration
		) {
		
		List<? extends Provider> providers = getAnnotationService()
				.getProviderList(idGeneration);
		
		String action = "/providers/idGeneration.json";
		
		ProviderSearchResults<AbstractProvider> response = buildProviderSearchResponse(
				providers, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/providers/{name}/{idGeneration}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
//	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSON_LINK, value="")
	public ModelAndView createProvider (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "name", required = true) String name,
		@RequestParam(value = "idGeneration", required = true, defaultValue = WebAnnotationFields.REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE) String idGeneration,
		@RequestParam(value = "uri", required = false) String uri) {

		String action = "create:/providers/name/idGeneration.json";
		
		Provider provider = new BaseProvider();
		provider.setName(name);
		provider.setIdGeneration(IdGenerationTypes.getValueByType(idGeneration));
		provider.setUri(uri);

		//validate input params
//		AnnotationId annoId = buildAnnotationId(provider, annotationNr);
		if (!IdGenerationTypes.isRegistered(idGeneration)) {
			return getValidationReport(apiKey, action, ProviderOperationResponse.ERROR_ID_GENERATION_TYPE_DOES_NOT_MATCH + IdGenerationTypes.printTypes());
		}
		
		// check whether annotation vor given provider and annotationNr already exist in database
		if (getAnnotationService().existsProviderInDb(provider)) 
			return getValidationReport(apiKey, action, ProviderOperationResponse.ERROR_PROVIDER_EXISTS_IN_DB + provider, null);			
		
		//store				
		Provider storedProvider = getAnnotationService().storeProvider(provider);

		//build response
		ProviderOperationResponse response = new ProviderOperationResponse(
				apiKey, action);
		response.success = true;

		response.setProvider(storedProvider);

		return JsonWebUtils.toJson(response, null);
	}*/

	
//	@RequestMapping(value = "/annotations/get/status/{provider}/{annotationNr}.json"
//			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public ModelAndView getAnnotationStatus (
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier
		) throws AnnotationNotFoundException {

		Annotation annotation = getAnnotationService().getAnnotationById(
				new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));

		String action = "get: /annotations/get/status/"+ provider + WebAnnotationFields.SLASH + identifier + ".json";
		
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				"", action);

		if (annotation != null) {
			return getReport(action, WebAnnotationFields.STATUS + ":" + annotation.getStatus() 
					+ ", " + WebAnnotationFields.PROVIDER + ":" + provider 
					+ ", " + WebAnnotationFields.IDENTIFIER + ":" + identifier, "");			

//			response = new AnnotationOperationResponse(
//					"", "/annotations/get/status/provider/annotationNr.json");
//			
//			response.success = true;
//
//			response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
//					annotation));
		}else{
			String errorMessage = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;			
			response = buildErrorResponse(errorMessage, action, "");
		}
		
		return JsonWebUtils.toJson(response, null);
	}
	
	
//	@PUT
//	@RequestMapping(value = "/admin/set/status/{provider}/{annotationNr}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public ModelAndView setAnnotationStatus(
//		@RequestParam(value = "provider", required = true) String provider, // this is an ID provider
//		@RequestParam(value = "identifier", required = true) String identifier,
//		@RequestParam(value = "status", defaultValue = "public") String status) {
//
//		String action = "set: /admin/set/status/"+ provider + WebAnnotationFields.SLASH + identifier + ".json";
//		
//		try {
//			Annotation annotation = getAnnotationService().getAnnotationById(
//					new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));
//	
//			if (annotation != null) { 
//				//validate input status
//				if (!(StringUtils.isNotEmpty(status) && StatusTypes.isRegistered(status)))
//					return getValidationReport("", action, AnnotationOperationResponse.ERROR_STATUS_TYPE_NOT_REGISTERED + status, null);			
//				
//				//check if status already set
//				if (annotation.getStatus() != null && annotation.getStatus().equals(status))
//					return getValidationReport("", action, AnnotationOperationResponse.ERROR_STATUS_ALREADY_SET + status, null);			
//
//				//set status
//				annotation.setStatus(status);
//				Annotation updatedAnnotation = getAnnotationService().updateAnnotationStatus(annotation);
//				getAnnotationService().logAnnotationStatusUpdate("", annotation);
//		
//				//build response
////				AnnotationOperationResponse response = new AnnotationOperationResponse(
////						"", action);
////				response.success = true;
////		
////				response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
////						updatedAnnotation));
////		
////				return JsonWebUtils.toJson(response, null);
//				return getReport(action, WebAnnotationFields.STATUS + ":" + updatedAnnotation.getStatus() 
//						+ ", " + WebAnnotationFields.PROVIDER + ":" + updatedAnnotation.getAnnotationId().getProvider() 
//						+ ", " + WebAnnotationFields.IDENTIFIER + ":" + updatedAnnotation.getAnnotationId().getIdentifier(), "");			
//			} else {
//				return getValidationReport("", action, AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND 
//						+ " provider: " + provider + ", annotationNr: " + identifier, null);			
//			}
//		} catch (Exception e) {
//			return getValidationReport("", action, e.getMessage(), e);		
//		}
//	}

	
//	@RequestMapping(value = "/annotations/check/visibility/{provider}/{annotationNr}/{user}.json"
//			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public ModelAndView checkVisibility (
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier,
		@RequestParam(value = "user", required = false) String user
		) {

		String action = "get: /annotations/check/visibility/" + provider + WebAnnotationFields.SLASH + identifier + WebAnnotationFields.SLASH + user + ".json";
		
		try {
//			Annotation annotation = getAnnotationService().getAnnotationById(
			Annotation annotation = getAnnotationService().getAnnotationById(
					new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));
	
			getAnnotationService().checkVisibility(annotation, user);
				
			AnnotationOperationResponse response = new AnnotationOperationResponse(
					"", action);
	
			if (annotation != null) {
				return getReport(action, WebAnnotationFields.DISABLED + ":" + annotation.isDisabled() 
						+ ", " + WebAnnotationFields.PROVIDER + ":" + provider 
						+ ", " + WebAnnotationFields.IDENTIFIER + ":" + identifier
						+ ", " + WebAnnotationFields.USER + ":" + user, "");				
			}else{
				String errorMessage = AnnotationOperationResponse.ERROR_VISIBILITY_CHECK;			
				response = buildErrorResponse(errorMessage, action, "");
			}
			return JsonWebUtils.toJson(response, null);
		} catch (AnnotationStateException e) {
			getLogger().error("An error occured during the invocation of :" + action, e);
			return getValidationReport(null, action, AnnotationOperationResponse.ERROR_VISIBILITY_CHECK + ". " + e.getMessage(), e);	
		} catch (Exception e) {
			getLogger().error("An error occured during the invocation of :" + action, e);
			return getValidationReport(null, action, AnnotationOperationResponse.ERROR_VISIBILITY_CHECK + ". " + e.getMessage(), e);	
		}
	}
	
	@RequestMapping(value = "/admin/reindex"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes = "reindex by annotation id", value = "", hidden = false)
	public ModelAndView reindexAnnotationByAnnotationId (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken
		) {

		getAdminService().reindexAnnotationById(
				new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier));

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/admin/reindex");

		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/admin/reindexset", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes = SwaggerConstants.REINDEX_HELP_NOTE, value = "", hidden=false)
	public ModelAndView reindexAnnotationSet(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "startTimestamp", required = false) String startTimestamp,
			@RequestParam(value = "endTimestamp", required = false) String endTimestamp,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken
			) {

		String status = "Operation could not be executed due to missing access rights! Please check 'apiKey' and 'userToken'.";
		if (apiKey.equals("apiadmin") && userToken.equals("admin")) {
			status = getAdminService().reindexAnnotationSet(startDate, endDate, startTimestamp, endTimestamp);
		}

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/admin/reindexset");
		response.setStatus(status);

		return JsonWebUtils.toJson(response, null);
	}
	
}
