package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.parse.WhiteListParser;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.model.WhitelistOperationResponse;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.WhitelistService;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api2.utils.JsonWebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@SwaggerSelect
@Api(tags = "Whitelist JSON Rest Service", description=" ", hidden=true)
@RequestMapping(value = "/"+WebAnnotationFields.WHITELIST)
public class WhitelistRest extends BaseRest {
	
	@Resource
	private WhitelistService whitelistService;
	
	public WhitelistService getWhitelistService() {
		return whitelistService;
	}

	public void setWhitelistService(WhitelistService whitelistService) {
		this.whitelistService = whitelistService;
	}
	
	@RequestMapping(value = "/component", method = RequestMethod.GET
			, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	@ApiOperation(value = "Retrieve component name", nickname = "getComponentName", response = java.lang.Void.class)
	public String getComponentName() {
		return WebAnnotationFields.WHITELIST;
	}

	@RequestMapping(value = "/search"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Retrieve whitelist entry for given URL", nickname = "getWhitelistEntry", response = java.lang.Void.class)
	public ResponseEntity<String> getWhitelistEntry (
//			public ModelAndView getWhitelistEntry (
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestParam(value = "url", required = true) String url
		) throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {
		
		validateApiKey(apiKey, WebAnnotationFields.READ_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_RETRIEVE);

		WhitelistEntry whitelist = getWhitelistService().getWhitelistEntryByUrl(url);

		WhitelistOperationResponse response = new WhitelistOperationResponse(
				apiKey, "/whitelist/search");

		if (whitelist != null) {
			response = new WhitelistOperationResponse(
					apiKey, "/whitelist/search");			
			response.success = true;
			response.setWhitelistEntry(serializeWhitelist(whitelist));
		} else {
			String errorMessage = WhitelistOperationResponse.ERROR_NO_OBJECT_FOUND;
			response.action = "get: /whitelist/search";
			response.success = false;
			response.error = errorMessage;
		}
		
//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}

	@RequestMapping(value = "/view"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Retrieve the whole whitelist", nickname = "getFullWhitelist", response = java.lang.Void.class)
	public ResponseEntity<String> getFullWhitelist(
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {

		validateApiKey(apiKey, WebAnnotationFields.READ_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_RETRIEVE);

		List<? extends WhitelistEntry> whitelist = getWhitelistService().getWhitelist();

		String action = "get:/whitelist/view";
		
		WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST
			, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Add a new entry to whitelist", nickname = "createWhitelistEntry", response = java.lang.Void.class)
	public ResponseEntity<String> createWhitelistEntry (
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestBody String whitelist) throws ParamValidationException, ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {

		validateApiKey(apiKey, WebAnnotationFields.WRITE_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_CREATE);
		
		String action = "post:/whitelist/create";
		
		//parse
		WhitelistEntry webWhitelist = WhiteListParser.toWhitelistEntry(whitelist);
	
		//store				
		WhitelistEntry storedWhitelist = getWhitelistService().storeWhitelistEntry(webWhitelist);

		//build response
		WhitelistOperationResponse response = new WhitelistOperationResponse(
				apiKey, action);
		response.success = true;

		response.setWhitelistEntry(serializeWhitelist(storedWhitelist));

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}

	@RequestMapping(value = "/load"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Load the default whitelist entries in DB", nickname = "loadDefaultWhitelist", response = java.lang.Void.class)
	public ResponseEntity<String> loadDefaultWhitelist (
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ParamValidationException, ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException{

		validateApiKey(apiKey, WebAnnotationFields.WRITE_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_RETRIEVE);

		List<? extends WhitelistEntry> whitelist = getWhitelistService().loadWhitelistFromResources();

		String action = "/load";
		
		WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}

	
	@DELETE
	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Delete the whole whitelist", nickname = "deleteAllWhitelistEntries", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAllWhitelistEntries(
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {

		validateApiKey(apiKey, WebAnnotationFields.DELETE_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_DELETE);

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "/whitelist/deleteall");
			
		try{
			int numDeletedWhitelistEntries = getWhitelistService().deleteWholeWhitelist();
			response.success = true;
			response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
		} catch (Exception e){
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}

	@DELETE
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE
					, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(value = "Delete an entry from the whitelist", nickname = "deleteWhitelistEntry", response = java.lang.Void.class)
	public ResponseEntity<String> deleteWhitelistEntry(
		@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = true) String apiKey,
		@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestParam(value = "url", required = true) String url
		) throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {

		validateApiKey(apiKey, WebAnnotationFields.DELETE_METHOD);

		getAuthorizationService().authorizeUser(userToken, apiKey, Operations.WHITELIST_DELETE);

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "delete/whitelist/delete");
			
		try{
			int numDeletedWhitelistEntries = getWhitelistService().deleteWhitelistEntry(url);
			response.success = true;
			response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
		} catch (Exception e){
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

//		return JsonWebUtils.toJson(response, null);
		String jsonStr = JsonWebUtils.toJson(response, null);
		return buildResponseEntityForJsonString(jsonStr);		
	}
	
}
