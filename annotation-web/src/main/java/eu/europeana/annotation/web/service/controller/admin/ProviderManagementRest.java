package eu.europeana.annotation.web.service.controller.admin;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.ProviderOperationResponse;
import eu.europeana.annotation.web.model.ProviderSearchResults;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;


//@Controller
//@Api(value = "admin-api", description = "Provider Management Rest Service")
public class ProviderManagementRest extends BaseRest {

	@GET
	@RequestMapping(value = "/admin/provider/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "-admin/provider";
	}

	@DELETE
	@RequestMapping(value = "/admin/provider/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnnotationOperationResponse deleteAnnotationById(
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "idGeneration", required = true, defaultValue = WebAnnotationFields.REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE) String idGeneration,
		@RequestParam(value = "name", required = true, defaultValue = WebAnnotationFields.DEFAULT_PROVIDER) String name) {


		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, "/admin/provider/delete");
			
		try{
			getAnnotationService().deleteProvider(name, idGeneration);
			response.success = true;
		} catch (Exception e){
			Logger.getLogger(SolrSyntaxConstants.ROOT).error(e);
			response.success = false;
			response.error = e.getMessage();
		}

		return response;
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
			return getValidationReport(apiKey, action, ProviderOperationResponse.ERROR_ID_GENERATION_TYPE_DOES_NOT_MATCH + IdGenerationTypes.printTypes(), null, false);
		}
		
		// check whether annotation vor given provider and annotationNr already exist in database
		if (getAnnotationService().existsProviderInDb(provider)) 
			return getValidationReport(apiKey, action, ProviderOperationResponse.ERROR_PROVIDER_EXISTS_IN_DB + provider, null, false);			
		
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
