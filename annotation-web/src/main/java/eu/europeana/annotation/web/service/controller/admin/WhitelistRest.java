package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import javax.ws.rs.DELETE;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConst;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.model.WhitelistOperationResponse;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;

@Controller
@Api( basePath= "/"+WebAnnotationFields.WHITELIST,  value = WebAnnotationFields.WHITELIST, description = "Whitelist JSON Rest Service", hidden=true)
@RequestMapping(value = "/"+WebAnnotationFields.WHITELIST)
public class WhitelistRest extends BaseRest {


	@RequestMapping(value = "/component", method = RequestMethod.GET
			, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return WebAnnotationFields.WHITELIST;
	}

	@RequestMapping(value = "/{uri}.json"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getWhitelist (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "uri", required = true) String uri
		) {

		Whitelist whitelist = getAnnotationService().getWhitelistByUrl(uri);

		WhitelistOperationResponse response = new WhitelistOperationResponse(
				apiKey, "/whitelist/uri.json");

		if (whitelist != null) {
			response = new WhitelistOperationResponse(
					apiKey, "/whitelist/uri.json");			
			response.success = true;
			response.setWhitelist(whitelist);
		} else {
			String errorMessage = WhitelistOperationResponse.ERROR_NO_OBJECT_FOUND;
			response.action = "get: /whitelist/"+ uri + ".json";
			response.success = false;
			response.error = errorMessage;
		}
		
		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/all.json"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAllWhitelist (
		@RequestParam(value = "apiKey", required = false) String apiKey) {

		List<? extends Whitelist> whitelist = getAnnotationService().getAllWhitelistEntries();

		String action = "/all.json";
		
		WhitelsitSearchResults<Whitelist> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/{uri}.json", method = RequestMethod.POST
			, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="createWhitelistEntry", value="", hidden=true)
	public ModelAndView createWhitelist (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "uri", required = true) String uri,
		@RequestBody String whitelist) throws ParamValidationException {

		String action = "create:/whitelist/uri.json";
		
		//parse
		Whitelist webWhitelist = JsonUtils.toWhitelistObject(whitelist);
	
		//store				
		Whitelist storedWhitelist = getAnnotationService().storeWhitelist(webWhitelist);

		//build response
		WhitelistOperationResponse response = new WhitelistOperationResponse(
				apiKey, action);
		response.success = true;

		response.setWhitelist(storedWhitelist);

		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/load"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="loadDefaultWhitelist", value="")//, hidden=true)
	public ModelAndView loadDefaultWhitelist (
		@RequestParam(value = "apiKey", required = false) String apiKey) throws ParamValidationException{

		List<? extends Whitelist> whitelist = getAnnotationService().loadWhitelistFromResources();

		String action = "/load";
		
		WhitelsitSearchResults<Whitelist> response = buildSearchWhitelistResponse(
				whitelist, apiKey, action);

		return JsonWebUtils.toJson(response, null);
		
		
//		WhitelistOperationResponse response = new WhitelistOperationResponse(
//				apiKey, "/load");
//
//		if (whitelist != null) {
//			response = new WhitelistOperationResponse(
//					apiKey, "/load");			
//			response.success = true;
//			response.setWhitelistEntries(whitelist);
//		} else {
//			String errorMessage = WhitelistOperationResponse.ERROR_NO_OBJECT_FOUND;
//			response.action = "get: /whitelist/load";
//			response.success = false;
//			response.error = errorMessage;
//		}
//		
//		return JsonWebUtils.toJson(response, null);
	}

	
	@DELETE
	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="deleteWhitelist", value="", hidden=false)
	public WhitelistOperationResponse deleteAllWhitelistEntries(
		@RequestParam(value = "apiKey", required = false) String apiKey) {

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "/whitelist/delete");
			
		try{
			getAnnotationService().deleteAllWhitelistEntries();
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

	@DELETE
	@RequestMapping(value = "/delete/{uri}.json", method = RequestMethod.DELETE
					, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes="deleteWhitelist", value="", hidden=false)
	public WhitelistOperationResponse deleteWhitelistEntry(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "uri", required = true) String uri
		) {

		WhitelistOperationResponse response;
		response = new WhitelistOperationResponse(
				apiKey, "/whitelist/delete/{uri}.json");
			
		try{
			getAnnotationService().deleteWhitelistEntry(uri);
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

	
}
