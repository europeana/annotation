package eu.europeana.annotation.web.service.controller.jsonld;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <CURRENT SPECIFICATION> POST /<annotation-web>/annotation.jsonLd GET /
 * <annotation-web>/annotation/provider/identifier.jsonld GET /
 * <annotation-web>/search.jsonld
 */

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Protocol", description=" ")
@Component
public class WebAnnotationProtocolRest extends BaseJsonldRest {

	@RequestMapping(value = "/annotation/", method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotation", nickname = "createAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> createAnnotation(
//			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
			@RequestBody String annotation,
			HttpServletRequest request)
					throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		Authentication authentication = verifyWriteAccess(WebAnnotationFields.CREATE_OPERATION, request);
		
		return storeAnnotation(null, indexOnCreate, annotation, authentication);
	}

	@RequestMapping(value = {"/annotation/{identifier}", "/annotation/{identifier}.jsonld"}, method = RequestMethod.GET, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Retrieve annotation", nickname = "getAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> getAnnotation(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String wskey,
			@RequestParam(value = WebAnnotationFields.PARAM_PROFILE, required = false) String profile,
			@RequestParam(value = WebAnnotationFields.LANGUAGE, required = false) String language,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			HttpServletRequest request
			) throws HttpException {

			verifyReadAccess(request);
			return getAnnotationById(identifier, profile, language);
	}
	
	
	@RequestMapping(value = {"/annotation/{identifier}"}, method = RequestMethod.PUT, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.UPDATE_SAMPLES_JSONLD, value = "Update annotation", nickname = "updateAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> updateAnnotation(
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestBody String annotation,
			HttpServletRequest request
			) throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {
		
		Authentication authentication = verifyWriteAccess(WebAnnotationFields.UPDATE_OPERATION, request);
		
//		String action = "put:/annotation/{identifier}[.{format}]";
		return updateAnnotation(identifier, annotation, authentication);
	}
	
	
	@RequestMapping(value = {"/annotation/{identifier}"}, method = RequestMethod.DELETE, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Delete annotation", nickname = "deleteAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotation(
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			HttpServletRequest request
			) throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

		Authentication authentication = verifyWriteAccess(WebAnnotationFields.DELETE_METHOD, request);
		
//		String action = "delete:/annotation/{identifier}[.{format}]";
		return deleteAnnotation(identifier, authentication);
	}
		
}
