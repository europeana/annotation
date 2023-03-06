package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;

import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.parse.WhiteListParser;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.WhitelistOperationResponse;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.WhitelistService;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api2.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Whitelist JSON Rest Service", description = " ", hidden = true)
@RequestMapping(value = "/" + WebAnnotationFields.WHITELIST)
public class WhitelistRest extends BaseRest {

    @Resource(name="whitelistService")
    private WhitelistService whitelistService;

    public WhitelistService getWhitelistService() {
	return whitelistService;
    }

    public void setWhitelistService(WhitelistService whitelistService) {
	this.whitelistService = whitelistService;
    }

    @RequestMapping(value = "/component", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    @ApiOperation(value = "Retrieve component name", nickname = "getComponentName", response = java.lang.Void.class)
    public String getComponentName() {
	return WebAnnotationFields.WHITELIST;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Retrieve whitelist entry for given URL", nickname = "getWhitelistEntry", response = java.lang.Void.class)
    public ResponseEntity<String> getWhitelistEntry(
//			public ModelAndView getWhitelistEntry (
	    @RequestParam(value = "url", required = true) String url, HttpServletRequest request)
	    throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {
	// JWT Token Only
	verifyWriteAccess(Operations.WHITELIST_RETRIEVE, request);

	WhitelistEntry whitelist = getWhitelistService().getWhitelistEntryByUrl(url);

	WhitelistOperationResponse response = new WhitelistOperationResponse(null, "/whitelist/search");

	if (whitelist != null) {
	    response.success = true;
	    response.setWhitelistEntry(serializeWhitelist(whitelist));
	} else {
	    String errorMessage = WhitelistOperationResponse.ERROR_NO_OBJECT_FOUND;
	    response.action = "get: /whitelist/search";
	    response.success = false;
	    response.error = errorMessage;
	}

	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Retrieve the whole whitelist", nickname = "getFullWhitelist", response = java.lang.Void.class)
    public ResponseEntity<String> getFullWhitelist(HttpServletRequest request)
	    throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException {

	// JWT Token Only
	verifyWriteAccess(Operations.WHITELIST_RETRIEVE, request);
	List<? extends WhitelistEntry> whitelist = getWhitelistService().getWhitelist();

	String action = "get:/whitelist/view";

	WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(whitelist, null, action);

//		return JsonWebUtils.toJson(response, null);
	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add a new entry to whitelist", nickname = "createWhitelistEntry", response = java.lang.Void.class)
    public ResponseEntity<String> createWhitelistEntry(@RequestBody String whitelist, HttpServletRequest request)
	    throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

	verifyWriteAccess(Operations.WHITELIST_CREATE, request);
	String action = "post:/whitelist/create";

	// parse
	WhitelistEntry webWhitelist = WhiteListParser.toWhitelistEntry(whitelist);

	// store
	WhitelistEntry storedWhitelist = getWhitelistService().storeWhitelistEntry(webWhitelist);

	// build response
	WhitelistOperationResponse response = new WhitelistOperationResponse(null, action);
	response.success = true;

	response.setWhitelistEntry(serializeWhitelist(storedWhitelist));

//		return JsonWebUtils.toJson(response, null);
	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Load the default whitelist entries in DB", nickname = "loadDefaultWhitelist", response = java.lang.Void.class)
    public ResponseEntity<String> loadDefaultWhitelist(
	    HttpServletRequest request)
	    throws WhitelistValidationException, ApplicationAuthenticationException, UserAuthorizationException,
	    OperationAuthorizationException, ApiKeyExtractionException, AuthorizationExtractionException {

	verifyWriteAccess(Operations.WHITELIST_CREATE, request);

	List<? extends WhitelistEntry> whitelist = getWhitelistService().loadWhitelistFromResources();

	String action = "/load";

	WhitelsitSearchResults<WhitelistEntry> response = buildSearchWhitelistResponse(whitelist, null, action);

//		return JsonWebUtils.toJson(response, null);
	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

    @DELETE
    @RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Delete the whole whitelist", nickname = "deleteAllWhitelistEntries", response = java.lang.Void.class)
    public ResponseEntity<String> deleteAllWhitelistEntries(
	    HttpServletRequest request) throws ApplicationAuthenticationException, UserAuthorizationException,
	    OperationAuthorizationException, ApiKeyExtractionException, AuthorizationExtractionException {

	verifyWriteAccess(Operations.WHITELIST_DELETE, request);

	WhitelistOperationResponse response;
	response = new WhitelistOperationResponse(null, "/whitelist/deleteall");

	try {
	    int numDeletedWhitelistEntries = getWhitelistService().deleteWholeWhitelist();
	    response.success = true;
	    response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
	} catch (Exception e) {
	    LogManager.getLogger(SolrSyntaxConstants.ROOT).error(e);
	    response.success = false;
	    response.error = e.getMessage();
	}

//		return JsonWebUtils.toJson(response, null);
	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

    @DELETE
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiOperation(value = "Delete an entry from the whitelist", nickname = "deleteWhitelistEntry", response = java.lang.Void.class)
    public ResponseEntity<String> deleteWhitelistEntry(
	    @RequestParam(value = "url", required = true) String url, 
	    HttpServletRequest request)
	    throws ApplicationAuthenticationException, UserAuthorizationException, OperationAuthorizationException,
	    ApiKeyExtractionException, AuthorizationExtractionException {

	verifyWriteAccess(Operations.WHITELIST_DELETE, request);

	WhitelistOperationResponse response;
	response = new WhitelistOperationResponse(null, "delete/whitelist/delete");

	try {
	    int numDeletedWhitelistEntries = getWhitelistService().deleteWhitelistEntry(url);
	    response.success = true;
	    response.error = "number of deleted whitelist entries: " + Integer.toString(numDeletedWhitelistEntries);
	} catch (Exception e) {
	    LogManager.getLogger(SolrSyntaxConstants.ROOT).error(e);
	    response.success = false;
	    response.error = e.getMessage();
	}

	String jsonStr = WebUtils.toJson(response);
	return buildResponse(jsonStr);
    }

}
