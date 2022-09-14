package eu.europeana.annotation.web.service.controller.jsonld;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
//@SwaggerSelect
@Api(tags = "Provide Feedback on Annotations", description=" ")
public class WebAnnotationFeedbackRest extends BaseJsonldRest {

	@RequestMapping(value = "/annotation/{identifier}/report", method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Report an (innapropriate) annotation", nickname = "reportAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> reportAnnotation(
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) long identifier,
			HttpServletRequest request)
					throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		Authentication authentication = verifyWriteAccess(Operations.CREATE, request);
		
//		String action = "post:/annotation/{identifier}/report";
		return storeAnnotationReport(identifier, authentication);
	}

	@RequestMapping(value = "/annotation/{identifier}/moderationsummary", method = RequestMethod.GET, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Retrieve moderation summary", nickname = "getModerationReportSummary", response = java.lang.Void.class)
	public ResponseEntity<String> getModerationSummary(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) long identifier,
			HttpServletRequest request
			) throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		verifyReadAccess(request);
		return getModerationReportSummary(wskey, identifier);		
	}
	
}
