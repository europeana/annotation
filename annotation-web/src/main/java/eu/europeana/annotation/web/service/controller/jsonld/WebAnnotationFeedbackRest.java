package eu.europeana.annotation.web.service.controller.jsonld;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//@Api(value = "web-annotation-feedback", description = "Providing Feedback on Annotations - API")
@Controller
@SwaggerSelect
@Api(tags = "Provide Feedback on Annotations", description=" ")
public class WebAnnotationFeedbackRest extends BaseJsonldRest {

	@RequestMapping(value = "/annotation/{provider}/{identifier}/report", method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Report an (innapropriate) annotation", nickname = "reportAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> reportAnnotation(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider, 
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			HttpServletRequest request)
					throws HttpException {

		userToken = getUserToken(userToken, request);
		
		String action = "post:/annotation/{provider}/{identifier}/report";
		return storeAnnotationReport(wskey, provider, identifier, userToken, action);
	}

	@RequestMapping(value = "/annotation/{provider}/{identifier}/moderationsummary", method = RequestMethod.GET, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Retrieve moderation summary", nickname = "getModerationReportSummary", response = java.lang.Void.class)
	public ResponseEntity<String> getModerationSummary(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider, 
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			HttpServletRequest request
			) throws HttpException {

		userToken = getUserToken(userToken, request);
		
		String action = "get:/annotation/{provider}/{identifier}/moderationsummary";
		return getModerationReportSummary(wskey, provider, identifier, action);		
	}
	
}
