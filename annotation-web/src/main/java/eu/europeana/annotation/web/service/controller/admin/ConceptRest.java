package eu.europeana.annotation.web.service.controller.admin;

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
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.ConceptOperationResponse;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;

//@Controller
//@Api(value = "concepts", description = "Concept JSON Rest Service")
public class ConceptRest extends BaseRest {


	@RequestMapping(value = "/concepts/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
//		return getConfiguration().getComponentName();
		return WebAnnotationFields.CONCEPT;
	}

	@RequestMapping(value = "/concepts/{uri}.json"
			, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getConcept (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "uri", required = true) String uri
		) {

		Concept concept = getAnnotationService().getConceptByUrl(uri);

		ConceptOperationResponse response = new ConceptOperationResponse(
				apiKey, "/concepts/uri.json");

		if (concept != null) {
			response = new ConceptOperationResponse(
					apiKey, "/concepts/uri.json");			
			response.success = true;
			response.setConcept(concept);
		} else {
			String errorMessage = ConceptOperationResponse.ERROR_NO_OBJECT_FOUND;
			response.action = "get: /concepts/"+ uri + ".json";
			response.success = false;
			response.error = errorMessage;
		}
		
		return JsonWebUtils.toJson(response, null);
	}
	
	@RequestMapping(value = "/concepts/{uri}.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=SwaggerConstants.SAMPLES_JSON_LINK, value="")
	public ModelAndView createConcept (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "uri", required = true) String uri,
		@RequestBody String concept) {

		String action = "create:/concepts/uri.json";
		
		//parse
		Concept webConcept = JsonUtils.toConceptObject(concept);
	
		//store				
		Concept storedConcept = getAnnotationService().storeConcept(webConcept);

		//build response
		ConceptOperationResponse response = new ConceptOperationResponse(
				apiKey, action);
		response.success = true;

		response.setConcept(storedConcept);

		return JsonWebUtils.toJson(response, null);
	}

}
