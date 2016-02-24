package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import javax.ws.rs.DELETE;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.model.WhitelistOperationResponse;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;

@Controller
@Api( basePath= "/"+WebAnnotationFields.WHITELIST,  value = WebAnnotationFields.WHITELIST
	, description = "Whitelist JSON Rest Service", hidden=true)
@RequestMapping(value = "/"+WebAnnotationFields.WHITELIST)
public class WhitelistRest extends BaseRest {


	@RequestMapping(value = "/component", method = RequestMethod.GET
			, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return WebAnnotationFields.WHITELIST;
	}

	@RequestMapping(value = "/search"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getWhitelistEntry (
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestParam(value = "url", required = true) String url
		) throws ApplicationAuthenticationException, UserAuthorizationException {
		
		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.RETRIEVE);

		WhitelistEntry whitelist = getAdminService().getWhitelistEntryByUrl(url);

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
		
		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/view"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getFullWhitelist(
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ApplicationAuthenticationException, UserAuthorizationException {

		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.RETRIEVE);

		List<? extends WhitelistEntry> whitelist = getAdminService().getWhitelist();

		String action = "get:/whitelist/view";
		
		WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST
			, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="createWhitelistEntry", value="", hidden=false)
	public ModelAndView createWhitelistEntry (
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestBody String whitelist) throws ParamValidationException, ApplicationAuthenticationException, UserAuthorizationException {

		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.CREATE);
		
		String action = "post:/whitelist/create";
		
		//parse
		WhitelistEntry webWhitelist = JsonUtils.toWhitelistEntry(whitelist);
	
		//store				
		WhitelistEntry storedWhitelist = getAdminService().storeWhitelistEntry(webWhitelist);

		//build response
		WhitelistOperationResponse response = new WhitelistOperationResponse(
				apiKey, action);
		response.success = true;

		response.setWhitelistEntry(serializeWhitelist(storedWhitelist));

		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/load"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="loadDefaultWhitelist", value="")//, hidden=true)
	public ModelAndView loadDefaultWhitelist (
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ParamValidationException, ApplicationAuthenticationException, UserAuthorizationException{

		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.RETRIEVE);

		List<? extends WhitelistEntry> whitelist = getAdminService().loadWhitelistFromResources();

		String action = "/load";
		
		WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}

	
	@DELETE
	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="deleteWhitelist", value="", hidden=false)
	public ModelAndView deleteAllWhitelistEntries(
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken) 
				throws ApplicationAuthenticationException, UserAuthorizationException {

		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.DELETE);

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "/whitelist/deleteall");
			
		try{
			int numDeletedWhitelistEntries = getAdminService().deleteWholeWhitelist();
			response.success = true;
			response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
		} catch (Exception e){
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return JsonWebUtils.toJson(response, null);
	}

	@DELETE
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE
					, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="deleteWhitelist", value="", hidden=false)
	public ModelAndView deleteWhitelistEntry(
		@RequestParam(value = "apiKey", required = true) String apiKey,
		@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
		@RequestParam(value = "url", required = true) String url
		) throws ApplicationAuthenticationException, UserAuthorizationException {

		validateApiKey(apiKey);

		authorizeUser(userToken, apiKey, Operations.DELETE);

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "delete/whitelist/delete");
			
		try{
			int numDeletedWhitelistEntries = getAdminService().deleteWhitelistEntry(url);
			response.success = true;
			response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
		} catch (Exception e){
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return JsonWebUtils.toJson(response, null);
	}

	
}
