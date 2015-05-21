package eu.europeana.annotation.web.service.controller;

import java.util.List;

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
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.ProviderOperationResponse;
import eu.europeana.annotation.web.model.ProviderSearchResults;
import eu.europeana.api2.utils.JsonWebUtils;


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
//		@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/delete");
			
		try{
			getAnnotationService().deleteAnnotation(provider, Long.valueOf(query));
//			getAnnotationService().deleteAnnotation(resourceId, provider, Long.valueOf(query));
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
//		@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider) {

		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/annotations/admin/index");
			
		try{
			getAnnotationService().indexAnnotation(provider, Long.valueOf(query));
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

	@PUT
	@RequestMapping(value = "/admin/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse disableAnnotationById(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "query", required = true) String query,
		@RequestParam(value = "europeana_id", required = true, defaultValue = WebAnnotationFields.REST_RESOURCE_ID) String resourceId,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/disable");
			
		try{
			getAnnotationService().disableAnnotation(provider, Long.valueOf(query));
//			getAnnotationService().disableAnnotation(resourceId, provider, Long.valueOf(query));
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

	@RequestMapping(value = "/admin/disabled/{collection}/{object}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	
	@RequestMapping(value = "/providers/{idGeneration}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	}

	
}
