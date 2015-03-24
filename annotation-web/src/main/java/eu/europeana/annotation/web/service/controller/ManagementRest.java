package eu.europeana.annotation.web.service.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;


@Controller
@Api(value = "admin", description = "Annotation Management Rest Service")
public class ManagementRest extends BaseRest {

	@GET
	@RequestMapping(value = "/admin/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "-admin";
	}

	@DELETE
	@RequestMapping(value = "/admin/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse deleteAnnotationById(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/delete");
			
		try{
			getAnnotationService().deleteAnnotation(resourceId, Integer.valueOf(query));
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

	@PUT
	@RequestMapping(value = "/admin/index", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse indexAnnotationById(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/annotations/admin/index");
			
		try{
			getAnnotationService().indexAnnotation(resourceId, Integer.valueOf(query));
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

	@PUT
	@RequestMapping(value = "/admin/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse disableAnnotationById(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/disable");
			
		try{
			getAnnotationService().disableAnnotation(resourceId, Integer.valueOf(query));
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

	@DELETE
	@RequestMapping(value = "/admin/tag/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse deleteTagById(
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true, defaultValue = WebAnnotationFields.REST_TAG_ID) String query) {

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/tag/delete");
			
		try{
			getAnnotationService().deleteTag(query);
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrAnnotationConst.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
	}

}
