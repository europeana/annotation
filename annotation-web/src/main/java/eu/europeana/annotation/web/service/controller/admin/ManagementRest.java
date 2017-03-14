package eu.europeana.annotation.web.service.controller.admin;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.IndexingJobServiceException;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.parse.BaseJsonParser;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.IndexingJobLockedException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api2.utils.JsonWebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;


@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Admin", description = " ", hidden = true)
public class ManagementRest extends BaseRest {
	
	protected final Logger logger = getLogger();

	@Resource
	private AdminService adminService;

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@RequestMapping(value = "/admin/annotation/delete", method = RequestMethod.DELETE, 
			produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Delete Annotation for good", nickname = "deleteAnnotationById", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotationById(
			// public ModelAndView deleteAnnotationById(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.REQ_PARAM_PROVIDER, required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
			@RequestParam(value = WebAnnotationFields.REQ_PARAM_IDENTIFIER, required = true) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws HttpException {

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/delete");

		deleteAnnotationForGood(provider, identifier, apiKey, userToken);
		response.success = true;
		String jsonStr = JsonWebUtils.toJson(response, null);
		logger.info("Delete Annotation for good result: " + jsonStr);
		return buildResponseEntityForJsonString(jsonStr);
	}

	@RequestMapping(value = "/admin/annotation/deleteset", method = RequestMethod.DELETE, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Delete a set of Annotations for good", nickname = "deleteAnnotationSet", 
	notes=SwaggerConstants.URIS_HELP_NOTE, response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotationSet(
			// public ModelAndView deleteAnnotationSet(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			@RequestBody String uris) throws HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		List<String> uriList = BaseJsonParser.toStringList(uris, true);

		BatchProcessingStatus status = getAdminService().deleteAnnotationSet(uriList);

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/deleteset");
		response.setStatus(
				"Success count: " + status.getSuccessCount() + ". Failure count: " + status.getFailureCount());
		response.success = true;

		// return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		logger.info("Delete a set of Annotations for good result: " + jsonStr);
		return buildResponseEntityForJsonString(jsonStr);
	}

	protected void deleteAnnotationForGood(String provider, String identifier, String apiKey, String userToken)
			throws HttpException {

		// 0. annotation id
		AnnotationId annoId = buildAnnotationId(provider, identifier, false);

		deleteAnnotationForGood(annoId, apiKey, userToken);
	}

	protected void deleteAnnotationForGood(AnnotationId annoId, String apiKey, String userToken)
			throws InternalServerException, UserAuthorizationException, ApplicationAuthenticationException,
			OperationAuthorizationException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		try {
			getAdminService().deleteAnnotation(annoId);
		} catch (AnnotationServiceException e) {
			throw new InternalServerException(e);
		}
	}


	@RequestMapping(value = "/admin/annotation/reindex", method = RequestMethod.GET, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Reindex by annotation id", nickname = "reindexAnnotationByAnnotationId", response = java.lang.Void.class)
	public ResponseEntity<String> reindexAnnotationByAnnotationId(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			// public ModelAndView
			// reindexAnnotationByAnnotationId(@RequestParam(value = "apiKey",
			// required = false) String apiKey,
			@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String provider,
			@RequestParam(value = "identifier", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = true) String userToken)
					throws UserAuthorizationException, HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		// if (!isAdmin(apiKey, userToken))
		// throw new UserAuthorizationException(
		// "Not authorized for performing administration operations. Must use a
		// valid apikey and token:",
		// apiKey + "_" + userToken);

		BaseAnnotationId baseAnnotationId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
				identifier);
		getAdminService().reindexAnnotationById(baseAnnotationId, new Date());

		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, "/admin/reindex");

		String jsonStr = JsonWebUtils.toJson(response, null);
		logger.info("Reindex by annotation id result: " + jsonStr);
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

	@RequestMapping(value = "/admin/annotation/reindexselection", method = RequestMethod.GET, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Reindex a set of annotations defined by selection criteria", notes = SwaggerConstants.DATE_FORMAT_HELP_NOTE, nickname = "reindexAnnotationBySelection", response = java.lang.Void.class)
	public ResponseEntity<String> reindexAnnotationSelection(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			// public ModelAndView reindexAnnotationSet(@RequestParam(value =
			// "apiKey", required = false) String apiKey,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "startTimestamp", required = false) String startTimestamp,
			@RequestParam(value = "endTimestamp", required = false) String endTimestamp,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = true) String userToken)
					throws IndexingJobServiceException, IndexingJobLockedException, HttpException {

		if (!isAdmin(apiKey, userToken))
			throw new UserAuthorizationException("User not authorized. Must use the admin apikey and token:",
					apiKey + "_" + userToken);

		BatchProcessingStatus status = getAdminService().reindexAnnotationSelection(startDate, endDate, startTimestamp, endTimestamp);

		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, "/admin/reindexset");
		response.setStatus(status.toString());

		// return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		logger.info("Reindex a set of annotations defined by selection criteria result: " + jsonStr);
		return buildResponseEntityForJsonString(jsonStr);
	}

	@RequestMapping(value = "/admin/annotation/reindexset", method = RequestMethod.POST, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Reindex a set of annotations", nickname = "reindexAnnotationByAnnotationId",  
	notes = SwaggerConstants.URIS_HELP_NOTE, response = java.lang.Void.class)
	public ResponseEntity<String> reindexAnnotationSet(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			@RequestBody String uris) throws UserAuthorizationException, HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		List<String> uriList = BaseJsonParser.toStringList(uris, true);

		BatchProcessingStatus status;
		try{
			status = getAdminService().reindexAnnotationSet(uriList, false,"/admin/annotation/reindexset");
		} catch (IndexingJobServiceException e) {
			throw new InternalServerException("Cannot reindex annotation selection", e);
		}

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/reindexset");
		response.setStatus(
				"Success count: " + status.getSuccessCount() + ". Failure count: " + status.getFailureCount());
		//only if at least one item was successfully reindexed
		response.success = (status.getSuccessCount() > 0);
		
		// return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		HttpStatus httpStatus = response.success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;  
		logger.info("Reindex a set of annotations result: " + jsonStr + "(HTTP status: " + httpStatus.toString() + ")");
		return buildResponseEntityForJsonString(jsonStr, httpStatus);
	}
	
	@RequestMapping(value = "/admin/annotation/reindexall", method = RequestMethod.GET, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Reindex all annotations", nickname = "reindexAll", response = java.lang.Void.class)
	public ResponseEntity<String> reindexAll(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws UserAuthorizationException, HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);

		BatchProcessingStatus status;
		try{
			status = getAdminService().reindexAll();
		}catch (IndexingJobServiceException e) {
			throw new InternalServerException("Cannot reindex annotation selection", e);
		}	

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(apiKey, "/admin/annotation/reindexset");
		response.setStatus(
				"Success count: " + status.getSuccessCount() + ". Failure count: " + status.getFailureCount());
		response.success = true;

		// return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		logger.info("Reindex all annotations result: " + jsonStr);
		return buildResponseEntityForJsonString(jsonStr);
	}
	
	@RequestMapping(value = "/admin/annotation/reindexoutdated", method = RequestMethod.GET, produces = { HttpHeaders.CONTENT_TYPE_JSON_UTF8, HttpHeaders.CONTENT_TYPE_JSONLD_UTF8})
	@ApiOperation(value = "Index new and reindex outdated annotations", nickname = "reindexOutdated", response = java.lang.Void.class)
	public ResponseEntity<String> reindexOutdated(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws UserAuthorizationException, HttpException {

		// SET DEFAULTS
		getAuthenticationService().getByApiKey(apiKey);

		// 1. authorize user
		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.ADMIN_ALL);
		
		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, "/admin/annotation/reindexoutdated");
		BatchProcessingStatus status; 
		try{
			status = getAdminService().reindexOutdated();
		
		}catch (IndexingJobServiceException e) {
			throw new InternalServerException("Cannot reindex annotation selection", e);
		}
		
		String successMsg = "Outdated annotations reindexed. " + status.toString();
		response.setStatus(successMsg);
		response.success = true;
		HttpStatus httpStatus = HttpStatus.OK;
		logger.info(successMsg);
		
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr, httpStatus);
	}
	


}
