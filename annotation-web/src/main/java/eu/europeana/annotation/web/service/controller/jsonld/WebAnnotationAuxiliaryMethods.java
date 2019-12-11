package eu.europeana.annotation.web.service.controller.jsonld;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Auxiliary Methods", description=" ")
@Component
public class WebAnnotationAuxiliaryMethods extends BaseJsonldRest {

	@RequestMapping(value = "/annotations/", method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotations", nickname = "createAnnotations", response = java.lang.Void.class)
	public ResponseEntity<String> createAnnotations(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestBody String annotationPage,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			HttpServletRequest request)
					throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		Authentication authentication = verifyWriteAccess(WebAnnotationFields.CREATE_OPERATION, request);
		
		return storeAnnotations(wskey, annotationPage, authentication);
	}
	
	@RequestMapping(value = {"/annotation/{annoType}"}, method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotation of given type", nickname = "createAnnotationByType", response = java.lang.Void.class)
	public ResponseEntity<String> createAnnotationByTypeJsonld(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_ANNO_TYPE) String annoType,
			HttpServletRequest request
			)throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		Authentication authentication = verifyWriteAccess(WebAnnotationFields.CREATE_OPERATION, request);
		
		MotivationTypes motivation = MotivationTypes.getTypeForAnnoType(annoType);
		
		if(motivation == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE,
					I18nConstants.ANNOTATION_VALIDATION,
					new String[]{WebAnnotationFields.PATH_PARAM_ANNO_TYPE, annoType},
					HttpStatus.NOT_ACCEPTABLE, 
					null);
		
		return storeAnnotation(wskey, motivation, identifier, indexOnCreate, annotation, authentication);
	}
	
}
