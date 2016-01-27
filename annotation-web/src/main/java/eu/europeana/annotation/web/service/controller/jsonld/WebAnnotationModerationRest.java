package eu.europeana.annotation.web.service.controller.jsonld;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.annotation.web.model.ModerationOperationResponse;
import eu.europeana.api2.utils.JsonWebUtils;


@Controller
@Api(value = "web-annotation-moderation", description = "Web Annotation Moderation API")
public class WebAnnotationModerationRest extends BaseJsonldRest {

	@RequestMapping(value = "/moderation/report", method = RequestMethod.POST, produces = { "moderation/json",
			MediaType.APPLICATION_JSON_VALUE })
	public ModelAndView createModeration(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, required = false) String provider, 
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws HttpException {

		String action = "get:/moderation/report";
		ModerationOperationResponse response = storeModeration(wskey, provider, identifier, userToken, action);
		return JsonWebUtils.toJson(response, null);
	}

	@RequestMapping(value = "/moderation/{provider}/{identifier}", method = RequestMethod.GET, produces = { "moderation/json",
			MediaType.APPLICATION_JSON_VALUE })
	public ModelAndView getModeration(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier
			) throws HttpException {

			String action = "get:/moderation/{provider}/{identifier}";
			ModerationOperationResponse response = getModerationById(wskey, provider, identifier, action);
			return JsonWebUtils.toJson(response, null);
	}
	
}
