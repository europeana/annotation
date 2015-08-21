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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

/**
 * <CURRENT SPECIFICATION> POST /<annotation-web>/annotation.jsonLd GET /
 * <annotation-web>/annotation/provider/identifier.jsonld GET /
 * <annotation-web>/search.jsonld
 */

@Controller
@Api(value = "web-annotation", description = "Web Annotation - Rest Service(json-ld)")
public class WebAnnotationProtocolRest extends BaseJsonldRest {

	// TODO: move to constant to authentication interfaces/classes
	public static final String USER_ANONYMOUNS = "anonymous";

	@RequestMapping(value = "/annotation/", method = RequestMethod.POST, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(notes = WebAnnotationFields.SAMPLES_JSONLD, value = "")
	public ResponseEntity<String> createAnnotation(@RequestParam(value = WebAnnotationFields.WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, defaultValue = WebAnnotationFields.PROVIDER_WEBANNO) String provider, 
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "false") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = USER_ANONYMOUNS) String userToken)
					throws HttpException {

		return storeAnnotation(wskey, null, provider, identifier, indexOnCreate, annotation, userToken);
	}

	@RequestMapping(value = "/annotation/{annoType}.jsonld", method = RequestMethod.POST, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(notes = WebAnnotationFields.SAMPLES_JSONLD_WITH_TYPE, value = "")
	public ResponseEntity<String> createAnnotationByTypeJsonld(@RequestParam(value = WebAnnotationFields.WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, defaultValue = WebAnnotationFields.PROVIDER_WEBANNO) String provider, 
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "false") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = USER_ANONYMOUNS) String userToken,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_ANNO_TYPE) String annoType
		
			)
					throws HttpException {

//		if(!WebAnnotationFields.FORMAT_JSONLD.equals(format))
//			throw new ParamValidationException(ParamValidationException.MESSAGE_FORMAT_NOT_SUPPORTED, 
//					WebAnnotationFields.PATH_PARAM_FORMAT, format, HttpStatus.NOT_ACCEPTABLE, null);
		
		MotivationTypes motivation = MotivationTypes.getTypeForAnnoType(annoType);
		if(motivation == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE, 
					WebAnnotationFields.PATH_PARAM_ANNO_TYPE, annoType, HttpStatus.NOT_ACCEPTABLE, null);
		
		return storeAnnotation(wskey, motivation, provider, identifier, indexOnCreate, annotation, userToken);
	}
	
	@RequestMapping(value = "/annotation/{provider}/{identifier}", method = RequestMethod.GET, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnnotation(
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier
			) throws HttpException {

			String action = "get:/annotationld/{provider}/{identifier}";
			return getAnnotationById(wskey, provider, identifier, action);
	}
	
	@RequestMapping(value = "/annotation/{provider}/{identifier}.jsonld", method = RequestMethod.GET, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnnotationJsonld(
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_PROVIDER) String provider,
			@PathVariable(value = WebAnnotationFields.PATH_PARAM_IDENTIFIER) String identifier
			) throws HttpException {

			String action = "get:/annotationld/{provider}/{identifier}.jsonld";
			return getAnnotationById(wskey, provider, identifier, action);
	}

	
	@RequestMapping(value = "/annotation/{identifier}.jsonld", method = RequestMethod.PUT, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(notes = WebAnnotationFields.UPDATE_SAMPLES_JSONLD, value = "")
	public ResponseEntity<String> updateAnnotation(@RequestParam(value = WebAnnotationFields.WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestBody String annotation,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = USER_ANONYMOUNS) String userToken//,
			) throws HttpException {
		
		String action = "put:/annotation/{identifier}.jsonld";
		return updateAnnotation(wskey, identifier, annotation, userToken, action);
	}
	

	@RequestMapping(value = "/annotation/{identifier}.jsonld", method = RequestMethod.DELETE, produces = { "application/ld+json",
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteAnnotation(@RequestParam(value = WebAnnotationFields.WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required = false, defaultValue = USER_ANONYMOUNS) String userToken//,
			) throws HttpException {

		String action = "delete:/annotation/{identifier}.jsonld";
		return deleteAnnotation(wskey, identifier, userToken, action);
	}
	
}
