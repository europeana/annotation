package eu.europeana.annotation.web.service.controller.admin;

import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api2.utils.JsonWebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Admin", description=" ", hidden=true)
public class ManagementRest extends BaseRest {

	// @GET
	// @RequestMapping(value = "/admin/component", method = RequestMethod.GET,
	// produces = MediaType.TEXT_HTML_VALUE)
	// @ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "-admin";
	}

	@RequestMapping(value = "/admin/annotation/delete", method = RequestMethod.DELETE, produces ={"application/ld+json", MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation(value = "Delete Annotation for good", nickname = "deleteAnnotationById", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotationById(
//			public ModelAndView deleteAnnotationById(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.REQ_PARAM_PROVIDER, required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
			@RequestParam(value = WebAnnotationFields.REQ_PARAM_IDENTIFIER, required = true) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws HttpException {

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/delete");

		deleteAnnotationForGood(provider, identifier, apiKey, userToken);
		response.success = true;
		//response.setStatus(status);

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);				
	}

	@RequestMapping(value = "/admin/annotation/deleteset", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Delete a set of Annotations for good", nickname = "deleteAnnotationSet", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotationSet(
//			public ModelAndView deleteAnnotationSet(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			@RequestBody String uris) throws HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		int failureCount = 0;
		int successCount = 0;
		
		List<String> uriList = JsonUtils.toStringList(uris, true);
		AnnotationId annoId;
		
		for (String annoUri : uriList) {
			annoId = JsonUtils.getIdHelper().parseAnnotationId(annoUri, true);
			try{
				getAnnotationService().deleteAnnotation(annoId);
				successCount++;
			}catch(Throwable th){
				getLogger().info(th);
				failureCount++;
			}
		}

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/deleteset");
		response.setStatus("Success count: " + successCount + ". Failure count: " + failureCount);
		response.success = true;

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);				
	}

	protected void deleteAnnotationForGood(String provider, String identifier, String apiKey, String userToken)
			throws HttpException {

		// 0. annotation id
		AnnotationId annoId = buildAnnotationId(provider, identifier, false);

		deleteAnnotationForGood(annoId, apiKey, userToken);
	}

	protected void deleteAnnotationForGood(AnnotationId annoId, String apiKey, String userToken)
			throws InternalServerException, UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		try {
			getAnnotationService().deleteAnnotation(annoId);
		} catch (AnnotationServiceException e) {
			throw new InternalServerException(e);
		}
	}

	// @PUT
	// @RequestMapping(value = "/admin/index", method = RequestMethod.PUT,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public AnnotationOperationResponse indexAnnotationById(
	// @RequestParam(value = "apiKey", required = false) String apiKey,
	// @RequestParam(value = "profile", required = false) String profile,
	// @RequestParam(value = "provider", required = true, defaultValue =
	// WebAnnotationFields.REST_PROVIDER) String provider,
	// @RequestParam(value = "identifier", required = true) String identifier) {
	//
	// AnnotationOperationResponse response;
	// response = new AnnotationOperationResponse(
	// apiKey, "/annotations/admin/index");
	//
	// try{
	// getAnnotationService().indexAnnotation(new
	// BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
	// identifier));
	//// getAnnotationService().indexAnnotation(resourceId, provider,
	// Long.valueOf(query));
	// response.success = true;
	// } catch (Exception e){
	// Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
	// response.success = false;
	// response.error = e.getMessage();
	// }
	//
	// return response;
	// }

	// @PUT
	// @RequestMapping(value = "/admin/index/provider", method =
	// RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public AnnotationOperationResponse indexAnnotationByIdAndProvider(
	// @RequestParam(value = "apiKey", required = false) String apiKey,
	// @RequestParam(value = "profile", required = false) String profile,
	// @RequestParam(value = "query", required = true) String query,
	// @RequestParam(value = "europeana_id", required = true, defaultValue =
	// WebAnnotationFields.REST_RESOURCE_ID) String resourceId,
	// @RequestParam(value = "provider", required = true, defaultValue =
	// WebAnnotationFields.REST_PROVIDER) String provider) {
	//
	// AnnotationOperationResponse response;
	// response = new AnnotationOperationResponse(
	// apiKey, "/annotations/admin/index/provider");
	//
	// try{
	// getAnnotationService().indexAnnotation(resourceId, provider,
	// Integer.valueOf(query));
	// response.success = true;
	// } catch (Exception e){
	// Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
	// response.success = false;
	// response.error = e.getMessage();
	// }
	//
	// return response;
	// }

	// @PUT
	// @RequestMapping(value = "/admin/annotation/disable", method =
	// RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	// @RequestMapping(value =
	// "/admin/annotation/disable/{provider}/{annotationNr}.json", method =
	// RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public AnnotationOperationResponse disableAnnotationById(
	// @RequestParam(value = "apiKey", required = false) String apiKey,
	// @RequestParam(value = "profile", required = false) String profile,
	// @PathVariable(value = "provider") String provider,
	// @PathVariable(value = "identifier") String identifier) {
	//
	//
	// AnnotationOperationResponse response;
	// response = new AnnotationOperationResponse(
	// apiKey, "/admin/annotation/disable");
	//
	// try{
	// getAnnotationService().disableAnnotation(new
	// BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
	// identifier));
	// response.success = true;
	// } catch (Exception e){
	// Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
	// response.success = false;
	// response.error = e.getMessage();
	// }
	//
	// return response;
	// }

	// @DELETE
	// @RequestMapping(value = "/admin/tag/delete", method =
	// RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public AnnotationOperationResponse deleteTagById(
	// @RequestParam(value = "apiKey", required = false) String apiKey,
	// @RequestParam(value = "profile", required = false) String profile,
	// @RequestParam(value = "query", required = true, defaultValue =
	// WebAnnotationFields.REST_TAG_ID) String query) {
	//
	// AnnotationOperationResponse response;
	// response = new AnnotationOperationResponse(
	// apiKey, "/admin/tag/delete");
	//
	// try{
	// getAnnotationService().deleteTag(query);
	// response.success = true;
	// } catch (Exception e){
	// Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
	// response.success = false;
	// response.error = e.getMessage();
	// }
	//
	// return response;
	// }

	// @RequestMapping(value = "/admin/disabled/{collection}/{object}.json",
	// method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	// public ModelAndView getDisabledAnnotationList(
	// @RequestParam(value = "apiKey", required = false) String apiKey,
	// @RequestParam(value = "profile", required = false) String profile,
	// @RequestParam(value = "collection", required = true, defaultValue =
	// WebAnnotationFields.REST_COLLECTION) String collection,
	// @RequestParam(value = "object", required = true, defaultValue =
	// WebAnnotationFields.REST_OBJECT) String object,
	// @RequestParam(value = "startOn", required = true, defaultValue =
	// WebAnnotationFields.REST_START_ON) String startOn,
	// @RequestParam(value = "limit", required = true, defaultValue =
	// WebAnnotationFields.REST_LIMIT) String limit) {
	//
	// String resourceId = toResourceId(collection, object);
	// List<? extends Annotation> annotations = getAnnotationService()
	// .getFilteredAnnotationList(resourceId, startOn, limit, true);
	//
	// String action = "/admin/disabled/collection/object.json";
	//
	// AnnotationSearchResults<AbstractAnnotation> response =
	// buildSearchResponse(
	// annotations, apiKey, action);
	//
	// return JsonWebUtils.toJson(response, null);
	// }

	/*
	 * @RequestMapping(value = "/providers/{idGeneration}.json", method =
	 * RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE) public
	 * ModelAndView getProviderList (
	 * 
	 * @RequestParam(value = "apiKey", required = false) String apiKey,
	 * 
	 * @RequestParam(value = "profile", required = false) String profile,
	 * 
	 * @RequestParam(value = "idGeneration", required = true, defaultValue =
	 * WebAnnotationFields.REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE) String
	 * idGeneration ) {
	 * 
	 * List<? extends Provider> providers = getAnnotationService()
	 * .getProviderList(idGeneration);
	 * 
	 * String action = "/providers/idGeneration.json";
	 * 
	 * ProviderSearchResults<AbstractProvider> response =
	 * buildProviderSearchResponse( providers, apiKey, action);
	 * 
	 * return JsonWebUtils.toJson(response, null); }
	 * 
	 * @RequestMapping(value = "/providers/{name}/{idGeneration}.json", method =
	 * RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @ResponseBody
	 * // @ApiOperation(notes=WebAnnotationFields.SAMPLES_JSON_LINK, value="")
	 * public ModelAndView createProvider (
	 * 
	 * @RequestParam(value = "apiKey", required = false) String apiKey,
	 * 
	 * @RequestParam(value = "profile", required = false) String profile,
	 * 
	 * @RequestParam(value = "name", required = true) String name,
	 * 
	 * @RequestParam(value = "idGeneration", required = true, defaultValue =
	 * WebAnnotationFields.REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE) String
	 * idGeneration,
	 * 
	 * @RequestParam(value = "uri", required = false) String uri) {
	 * 
	 * String action = "create:/providers/name/idGeneration.json";
	 * 
	 * Provider provider = new BaseProvider(); provider.setName(name);
	 * provider.setIdGeneration(IdGenerationTypes.getValueByType(idGeneration));
	 * provider.setUri(uri);
	 * 
	 * //validate input params // AnnotationId annoId =
	 * buildAnnotationId(provider, annotationNr); if
	 * (!IdGenerationTypes.isRegistered(idGeneration)) { return
	 * getValidationReport(apiKey, action,
	 * ProviderOperationResponse.ERROR_ID_GENERATION_TYPE_DOES_NOT_MATCH +
	 * IdGenerationTypes.printTypes()); }
	 * 
	 * // check whether annotation vor given provider and annotationNr already
	 * exist in database if
	 * (getAnnotationService().existsProviderInDb(provider)) return
	 * getValidationReport(apiKey, action,
	 * ProviderOperationResponse.ERROR_PROVIDER_EXISTS_IN_DB + provider, null);
	 * 
	 * //store Provider storedProvider =
	 * getAnnotationService().storeProvider(provider);
	 * 
	 * //build response ProviderOperationResponse response = new
	 * ProviderOperationResponse( apiKey, action); response.success = true;
	 * 
	 * response.setProvider(storedProvider);
	 * 
	 * return JsonWebUtils.toJson(response, null); }
	 */

	// @RequestMapping(value =
	// "/annotations/get/status/{provider}/{annotationNr}.json"
	// , method = RequestMethod.GET, produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public ModelAndView getAnnotationStatus (
	// @RequestParam(value = "provider", required = true, defaultValue =
	// WebAnnotationFields.REST_PROVIDER) String provider,
	// @RequestParam(value = "identifier", required = true, defaultValue =
	// WebAnnotationFields.REST_ANNOTATION_NR) String identifier
	// ) throws AnnotationNotFoundException {
	//
	// Annotation annotation = getAnnotationService().getAnnotationById(
	// new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
	// identifier));
	//
	// String action = "get: /annotations/get/status/"+ provider +
	// WebAnnotationFields.SLASH + identifier + ".json";
	//
	// AnnotationOperationResponse response = new AnnotationOperationResponse(
	// "", action);
	//
	// if (annotation != null) {
	// return getReport(action, WebAnnotationFields.STATUS + ":" +
	// annotation.getStatus()
	// + ", " + WebAnnotationFields.PROVIDER + ":" + provider
	// + ", " + WebAnnotationFields.IDENTIFIER + ":" + identifier, "");

	// response = new AnnotationOperationResponse(
	// "", "/annotations/get/status/provider/annotationNr.json");
	//
	// response.success = true;
	//
	// response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
	// annotation));
	// }else{
	// String errorMessage = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
	// response = buildErrorResponse(errorMessage, action, "");
	// }
	//
	// return JsonWebUtils.toJson(response, null);
	// }

	// @PUT
	// @RequestMapping(value =
	// "/admin/set/status/{provider}/{annotationNr}.json", method =
	// RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public ModelAndView setAnnotationStatus(
	// @RequestParam(value = "provider", required = true) String provider, //
	// this is an ID provider
	// @RequestParam(value = "identifier", required = true) String identifier,
	// @RequestParam(value = "status", defaultValue = "public") String status) {
	//
	// String action = "set: /admin/set/status/"+ provider +
	// WebAnnotationFields.SLASH + identifier + ".json";
	//
	// try {
	// Annotation annotation = getAnnotationService().getAnnotationById(
	// new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
	// identifier));
	//
	// if (annotation != null) {
	// //validate input status
	// if (!(StringUtils.isNotEmpty(status) &&
	// StatusTypes.isRegistered(status)))
	// return getValidationReport("", action,
	// AnnotationOperationResponse.ERROR_STATUS_TYPE_NOT_REGISTERED + status,
	// null);
	//
	// //check if status already set
	// if (annotation.getStatus() != null &&
	// annotation.getStatus().equals(status))
	// return getValidationReport("", action,
	// AnnotationOperationResponse.ERROR_STATUS_ALREADY_SET + status, null);
	//
	// //set status
	// annotation.setStatus(status);
	// Annotation updatedAnnotation =
	// getAnnotationService().updateAnnotationStatus(annotation);
	// getAnnotationService().logAnnotationStatusUpdate("", annotation);
	//
	// //build response
	//// AnnotationOperationResponse response = new AnnotationOperationResponse(
	//// "", action);
	//// response.success = true;
	////
	//// response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
	//// updatedAnnotation));
	////
	//// return JsonWebUtils.toJson(response, null);
	// return getReport(action, WebAnnotationFields.STATUS + ":" +
	// updatedAnnotation.getStatus()
	// + ", " + WebAnnotationFields.PROVIDER + ":" +
	// updatedAnnotation.getAnnotationId().getProvider()
	// + ", " + WebAnnotationFields.IDENTIFIER + ":" +
	// updatedAnnotation.getAnnotationId().getIdentifier(), "");
	// } else {
	// return getValidationReport("", action,
	// AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND
	// + " provider: " + provider + ", annotationNr: " + identifier, null);
	// }
	// } catch (Exception e) {
	// return getValidationReport("", action, e.getMessage(), e);
	// }
	// }

	// @RequestMapping(value =
	// "/annotations/check/visibility/{provider}/{annotationNr}/{user}.json"
	// , method = RequestMethod.GET, produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public ModelAndView checkVisibility (
	// @RequestParam(value = "provider", required = true, defaultValue =
	// WebAnnotationFields.REST_PROVIDER) String provider,
	// @RequestParam(value = "identifier", required = true, defaultValue =
	// WebAnnotationFields.REST_ANNOTATION_NR) String identifier,
	// @RequestParam(value = "user", required = false) String user
	// ) {
	//
	// String action = "get: /annotations/check/visibility/" + provider +
	// WebAnnotationFields.SLASH + identifier + WebAnnotationFields.SLASH + user
	// + ".json";
	//
	// try {
	// Annotation annotation = getAnnotationService().getAnnotationById(
	// Annotation annotation = getAnnotationService().getAnnotationById(
	// new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
	// identifier));
	//
	// getAnnotationService().checkVisibility(annotation, user);
	//
	// AnnotationOperationResponse response = new AnnotationOperationResponse(
	// "", action);
	//
	// if (annotation != null) {
	// return getReport(action, WebAnnotationFields.DISABLED + ":" +
	// annotation.isDisabled()
	// + ", " + WebAnnotationFields.PROVIDER + ":" + provider
	// + ", " + WebAnnotationFields.IDENTIFIER + ":" + identifier
	// + ", " + WebAnnotationFields.USER + ":" + user, "");
	// }else{
	// String errorMessage = AnnotationOperationResponse.ERROR_VISIBILITY_CHECK;
	// response = buildErrorResponse(errorMessage, action, "");
	// }
	// return JsonWebUtils.toJson(response, null);
	// } catch (AnnotationStateException e) {
	// getLogger().error("An error occured during the invocation of :" + action,
	// e);
	// return getValidationReport(null, action,
	// AnnotationOperationResponse.ERROR_VISIBILITY_CHECK + ". " +
	// e.getMessage(), e);
	// } catch (Exception e) {
	// getLogger().error("An error occured during the invocation of :" + action,
	// e);
	// return getValidationReport(null, action,
	// AnnotationOperationResponse.ERROR_VISIBILITY_CHECK + ". " +
	// e.getMessage(), e);
	// }
	// }

	@RequestMapping(value = "/admin/annotation/reindex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Reindex by annotation id", nickname="reindexAnnotationByAnnotationId", response = java.lang.Void.class)
	public ResponseEntity<String> reindexAnnotationByAnnotationId(@RequestParam(value = "apiKey", required = false) String apiKey,
//			public ModelAndView reindexAnnotationByAnnotationId(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
			@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = true) String userToken)
					throws UserAuthorizationException {

		if (!isAdmin(apiKey, userToken))
			throw new UserAuthorizationException(
					"Not authorized for performing administration operations. Must use a valid apikey and token:",
					apiKey + "_" + userToken);

		BaseAnnotationId baseAnnotationId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
				identifier);
		getAdminService().reindexAnnotationById(baseAnnotationId, new Date());

		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, "/admin/reindex");

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);				
	}

	/**
	 * This method validates whether user has admin rights to execute methods in
	 * management API.
	 * 
	 * @param apiKey
	 * @param userToken
	 * @return true if user has necessary permissions
	 */
	private boolean isAdmin(String apiKey, String userToken) {
		return (apiKey.equals("apiadmin") && userToken.equals("admin"));
	}

	@RequestMapping(value = "/admin/annotation/reindexset", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Reindex by annotation id", notes = SwaggerConstants.REINDEX_HELP_NOTE, 
		nickname="reindexAnnotationByAnnotationId", response = java.lang.Void.class)
	public ResponseEntity<String> reindexAnnotationSet(@RequestParam(value = "apiKey", required = false) String apiKey,
//			public ModelAndView reindexAnnotationSet(@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "startTimestamp", required = false) String startTimestamp,
			@RequestParam(value = "endTimestamp", required = false) String endTimestamp,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = true) String userToken)
					throws UserAuthorizationException {

		if (!isAdmin(apiKey, userToken))
			throw new UserAuthorizationException("User not authorized. Must use the admin apikey and token:",
					apiKey + "_" + userToken);

		String status = getAdminService().reindexAnnotationSet(startDate, endDate, startTimestamp, endTimestamp);

		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, "/admin/reindexset");
		response.setStatus(status);

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);				
	}

}
