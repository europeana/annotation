package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.exception.ParamValidationException;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api.commons.definitions.utils.DateUtils;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.api2.utils.JsonWebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@SwaggerSelect
@Api(tags = "Web Annotation Auxiliary Methods", description = " ")
public class WebAnnotationAuxiliaryMethodsRest extends BaseJsonldRest {

    @RequestMapping(value = "/annotations/", method = RequestMethod.POST, produces = {
	    HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8 })
    @ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotations", nickname = "createAnnotations", response = java.lang.Void.class)
    public ResponseEntity<String> createAnnotations(@RequestBody String annotationPage, HttpServletRequest request)
	    throws HttpException, ApiKeyExtractionException, AuthorizationExtractionException {

	Authentication authentication = verifyWriteAccess(Operations.CREATE, request);

	return storeAnnotations(annotationPage, authentication);
    }

    @RequestMapping(value = { "/annotation/{annoType}", "/annotation/{annoType}.jsonld" }, method = RequestMethod.POST, 
    		consumes = { MediaType.APPLICATION_JSON },
    		produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8 })
    @ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotation of given type", nickname = "createAnnotationByType", response = java.lang.Void.class)
    public ResponseEntity<String> createAnnotationByTypeJsonld(
	    @RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
	    @RequestBody String annotation,
	    @PathVariable(value = WebAnnotationFields.PATH_PARAM_ANNO_TYPE) String annoType, HttpServletRequest request)
	    throws Exception {

	Authentication authentication = verifyWriteAccess(Operations.CREATE, request);

	MotivationTypes motivation = MotivationTypes.getTypeForAnnoType(annoType);

	if (motivation == null)
	    throw new ParamValidationException(String.format("Invalid parameter annoType: %s", annoType));

	return storeAnnotation(motivation, indexOnCreate, annotation, authentication);
    }

    @RequestMapping(value = "/annotations/deleted", method = RequestMethod.GET, produces = {
	    HttpHeaders.CONTENT_TYPE_JSON_UTF8 })
    @ApiOperation(value = "Get ids of deleted Annotations", nickname = "getDeleted", response = java.lang.Void.class,
    		notes = "The from and to parameters should have the format yyyy-mm-dd'T'hh:mm:ss'Z', e.g. 1970-01-01T00:00:00Z.")
    public ResponseEntity<String> getDeleted(
	    @RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
	    @RequestParam(value = "motivation", required = false) String motivation,
	    @RequestParam(value = "from", required = false) String startDateStr,
	    @RequestParam(value = "to", required = false) String stopDateStr,
	    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
	    @RequestParam(value = "limit", required = false, defaultValue = "100") int limit,
	    HttpServletRequest request)
	    throws Exception {

	// SET DEFAULTS
	verifyReadAccess(request);

	MotivationTypes motivationType = validateMotivation(motivation);
	
	//in case the start and stop dates are not provided, set them to defaults
	Date startDate = startDateStr==null ? DateUtils.convertStrToDate("1970-01-01T00:00:00Z") : DateUtils.convertStrToDate(startDateStr);
	if(startDate==null)
		throw new ParamValidationException(String.format("Invalid from parameter format: %s", startDateStr));
	Date stopDate = stopDateStr==null ? new Date() : DateUtils.convertStrToDate(stopDateStr);
	if(stopDate==null)
		throw new ParamValidationException(String.format("Invalid to parameter format: %s", stopDateStr));

	List<String> deletions = getAnnotationService().getDeletedAnnotationSet(motivationType, startDate, stopDate, page, limit);

	String jsonStr = deletions==null ? null : JsonWebUtils.toJson(deletions, null);
	
    // build response entity with headers
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(1);
    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
	
	ResponseEntity<String> response = new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
	return response;
    }
    
    @RequestMapping(value = "/annotations/getDeletedWithAdditionalInfo", method = RequestMethod.GET, produces = {
    	    HttpHeaders.CONTENT_TYPE_JSON_UTF8 })
        @ApiOperation(value = "Get deleted Annotations where not only the id is returned but some additional information.", nickname = "getDeletedWithAdditionalInfo", response = java.lang.Void.class,
        		notes = "The from and to parameters should have the format yyyy-mm-dd'T'hh:mm:ss'Z', e.g. 1970-01-01T00:00:00Z.")
        public ResponseEntity<String> getDeletedWithAdditionalInfo(
    	    @RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
    	    @RequestParam(value = "motivation", required = false) String motivation,
    	    @RequestParam(value = "from", required = false) String startDateStr,
    	    @RequestParam(value = "to", required = false) String stopDateStr,
    	    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
    	    @RequestParam(value = "limit", required = false, defaultValue = "100") int limit,
    	    HttpServletRequest request)
    	    throws Exception {

    	// SET DEFAULTS
    	verifyReadAccess(request);

    	MotivationTypes motivationType = validateMotivation(motivation);
    	
    	//in case the start and stop dates are not provided, set them to defaults
    	Date startDate = startDateStr==null ? DateUtils.convertStrToDate("1970-01-01T00:00:00Z") : DateUtils.convertStrToDate(startDateStr);
    	if(startDate==null)
    		throw new ParamValidationException(String.format("Invalid from parameter format: %s", startDateStr));
    	Date stopDate = stopDateStr==null ? new Date() : DateUtils.convertStrToDate(stopDateStr);
    	if(stopDate==null)
    		throw new ParamValidationException(String.format("Invalid to parameter format: %s", stopDateStr));

    	List<AnnotationDeletion> deletions = getAnnotationService().getDeletedAnnotationSetWithAdditionalInfo(motivationType, startDate, stopDate, page, limit);

    	String jsonStr = deletions==null ? null : JsonWebUtils.toJson(deletions, null);
    	
    	ResponseEntity<String> response = new ResponseEntity<String>(jsonStr, null, HttpStatus.OK);
    	return response;
    }

    protected MotivationTypes validateMotivation(String motivation) throws ParamValidationException {
	MotivationTypes motivationType = null;
	if (StringUtils.isNotBlank(motivation)) {
	    motivationType = MotivationTypes.getType(motivation);
	    if (motivationType==MotivationTypes.UNKNOWN)
	    	throw new ParamValidationException(String.format("Invalid parameter motivation: %s", motivation));
	}
	return motivationType;
    }
}
