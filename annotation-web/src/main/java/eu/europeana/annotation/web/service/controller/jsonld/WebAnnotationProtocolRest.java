package eu.europeana.annotation.web.service.controller.jsonld;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, required = false) String provider, 
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken)
					throws HttpException {

		return storeAnnotation(wskey, null, provider, identifier, indexOnCreate, annotation, userToken);
	}
	
	@RequestMapping(value = {"/annotation/{annoType}", "/annotation/{annoType}.jsonld"}, method = RequestMethod.POST, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotation of given type", nickname = "createAnnotationByType", response = java.lang.Void.class)
	public ResponseEntity<String> createAnnotationByTypeJsonld(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, required = false) String provider, 
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_ANNO_TYPE) String annoType
		
			)
					throws HttpException {

//		if(!WebAnnotationFields.FORMAT_JSONLD.equals(format))
//			throw new ParamValidationException(ParamValidationException.MESSAGE_FORMAT_NOT_SUPPORTED, 
//					WebAnnotationFields.PATH_PARAM_FORMAT, format, HttpStatus.NOT_ACCEPTABLE, null);
		
		MotivationTypes motivation = MotivationTypes.getTypeForAnnoType(annoType);
		if(motivation == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE,
					I18nConstants.ANNOTATION_VALIDATION,
					new String[]{WebAnnotationFields.PATH_PARAM_ANNO_TYPE, annoType},
					HttpStatus.NOT_ACCEPTABLE, 
					null);
		
		return storeAnnotation(wskey, motivation, provider, identifier, indexOnCreate, annotation, userToken);
	}
	
	@RequestMapping(value = {"/annotation/{provider}/{identifier}", "/annotation/{provider}/{identifier}.jsonld"}, method = RequestMethod.GET, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Retrieve annotation", nickname = "getAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> getAnnotation(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier
//			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken//,
			) throws HttpException {

			String action = "get:/annotation/{provider}/{identifier}[.{format}]";
			return getAnnotationById(wskey, provider, identifier, action);
	}
	
	@RequestMapping(value = "/annotation/{provider}/{identifier}", method = RequestMethod.OPTIONS, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = "TODO", value = "Support CORS preflight requests", nickname = "options", response = java.lang.Void.class)
	public ResponseEntity<String> options(
			@PathVariable(value = WebAnnotationFields.PROVIDER) String provider, 
			@PathVariable(value = WebAnnotationFields.IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required=false) String wskey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required=false) String userToken)
					throws HttpException {

		//the content response is delivered automatically by spring
		return null;
//		return optionsForCorsPreflight(wskey, provider, identifier, userToken);
	}
	
	@RequestMapping(value = "/annotation/", method = RequestMethod.OPTIONS, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = "TODO", value = "Support CORS preflight requests", nickname = "options", response = java.lang.Void.class)
	public ResponseEntity<String> options(
			@RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String wskey,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false) String userToken)
					throws HttpException {

		//the content response is delivered automatically by spring
				return null;
//				return optionsForCorsPreflight(wskey, provider, identifier, userToken);
	}
	
	@RequestMapping(value = {"/annotation/{provider}/{identifier}", "/annotation/{provider}/{identifier}.jsonld"}, method = RequestMethod.PUT, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(notes = SwaggerConstants.UPDATE_SAMPLES_JSONLD, value = "Update annotation", nickname = "updateAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> updateAnnotation(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestBody String annotation,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken//,
			) throws HttpException {
		
		String action = "put:/annotation/{provider}/{identifier}[.{format}]";
		return updateAnnotation(wskey, provider, identifier, annotation, userToken, action);
	}
	
	
	@RequestMapping(value = {"/annotation/{provider}/{identifier}", "/annotation/{provider}/{identifier}.jsonld"}, method = RequestMethod.DELETE, 
			produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8})
	@ApiOperation(value = "Delete annotation", nickname = "deleteAnnotation", response = java.lang.Void.class)
	public ResponseEntity<String> deleteAnnotation(@RequestParam(value = WebAnnotationFields.PARAM_WSKEY) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier,
			@RequestParam(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = WebAnnotationFields.USER_ANONYMOUNS) String userToken
			) throws HttpException {

		String action = "delete:/annotation/{provider}/{identifier}[.{format}]";
		return deleteAnnotation(wskey, provider, identifier, userToken, action);
	}
	
	
}
