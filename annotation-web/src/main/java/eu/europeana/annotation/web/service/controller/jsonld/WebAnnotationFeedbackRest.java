package eu.europeana.annotation.web.service.controller.jsonld;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wordnik.swagger.annotations.Api;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.web.exception.HttpException;


@Controller
@Api(value = "web-annotation-feedback", description = "Providing Feedback on Annotations - API")
public class WebAnnotationFeedbackRest extends BaseJsonldRest {

	@RequestMapping(value = "/annotation/{provider}/{identifier}/report", method = RequestMethod.POST, produces = {"application/ld+json", MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> reportAnnotation(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider, 
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws HttpException {

		String action = "post:/annotation/{provider}/{identifier}/report";
		return storeAnnotationReport(wskey, provider, identifier, userToken, action);
	}

	@RequestMapping(value = "/annotation/{provider}/{identifier}/moderationsummary", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getModerationSummary(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider, 
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken
			) throws HttpException {

			String action = "get:/annotation/{provider}/{identifier}/moderationsummary";
			return super.getModerationSummary(wskey, provider, identifier, action);		
	}
	
}
