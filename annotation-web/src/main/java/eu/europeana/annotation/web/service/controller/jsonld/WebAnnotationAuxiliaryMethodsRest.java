package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.http.SwaggerConstants;
import eu.europeana.api.common.config.I18nConstantsAnnotation;
import eu.europeana.api.commons.definitions.exception.DateParsingException;
import eu.europeana.api.commons.definitions.utils.DateUtils;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.api2.utils.JsonWebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
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
    		produces = { HttpHeaders.CONTENT_TYPE_JSONLD_UTF8, HttpHeaders.CONTENT_TYPE_JSON_UTF8 })
    @ApiOperation(notes = SwaggerConstants.SAMPLES_JSONLD, value = "Create annotation of given type", nickname = "createAnnotationByType", response = java.lang.Void.class)
    public ResponseEntity<String> createAnnotationByTypeJsonld(
	    @RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "true") boolean indexOnCreate,
	    @RequestBody String annotation,
	    @PathVariable(value = WebAnnotationFields.PATH_PARAM_ANNO_TYPE) String annoType, HttpServletRequest request) throws HttpException
	    {

	Authentication authentication = verifyWriteAccess(Operations.CREATE, request);

	MotivationTypes motivation = MotivationTypes.getTypeForAnnoType(annoType);

	if (motivation == null) {
	  throw new HttpException("Invalid parameter value.", I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          new String[] {"annoType", annoType}, HttpStatus.BAD_REQUEST); 
	}

	return storeAnnotation(motivation, indexOnCreate, annotation, authentication);
    }

    @RequestMapping(value = "/annotations/deleted", method = RequestMethod.GET)
    @ApiOperation(value = "Get ids of deleted Annotations", nickname = "getDeleted", response = java.lang.Void.class,
            notes = "The from and to parameters should have the format yyyy-mm-dd'T'hh:mm:ss'Z', e.g. 1970-01-01T00:00:00Z.")
    public ResponseEntity<String> getDeleted(
	    @RequestParam(value = WebAnnotationFields.PARAM_WSKEY, required = false) String apiKey,
	    @RequestParam(value = "motivation", required = false) String motivation,
	    @RequestParam(value = "from", required = false) String startDateStr,
	    @RequestParam(value = "to", required = false) String stopDateStr,
	    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
	    @RequestParam(value = "limit", required = false, defaultValue = "100") int limit,
	    HttpServletRequest request) throws HttpException {

	// SET DEFAULTS
	verifyReadAccess(request);

	MotivationTypes motivationType = validateMotivation(motivation);
	
	//in case the start and stop dates are not provided, set them to defaults
	Date startDate = null;
    try {
      startDate = startDateStr==null ? DateUtils.parseToDate("1970-01-01T00:00:00Z") : DateUtils.parseToDate(startDateStr);
    } catch (DateParsingException e) {
      throw new HttpException("Invalid parameter value.", I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          new String[] {"from", startDateStr}, HttpStatus.BAD_REQUEST, e);
    }
    
    Date stopDate = null;
    try {
      stopDate = stopDateStr==null ? new Date() : DateUtils.parseToDate(stopDateStr);
    } catch (DateParsingException e) {
      throw new HttpException("Invalid parameter value.", I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          new String[] {"to", stopDateStr}, HttpStatus.BAD_REQUEST, e);
    }

    if(startDate.compareTo(stopDate)>=0) {
      throw new HttpException("The start date (from) needs to be before the stop date (to).", I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          new String[] {"from / to ", " " + startDateStr + " / " + stopDateStr}, HttpStatus.BAD_REQUEST);
	}

	List<String> deletions = getAnnotationService().getDeletedAnnotationSet(motivationType, startDate, stopDate, page, limit);

	String jsonStr = JsonWebUtils.toJson(deletions, null);
	
    // build response entity with headers
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(1);
    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
	
	ResponseEntity<String> response = new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
	return response;
    }
    
    protected MotivationTypes validateMotivation(String motivation) throws HttpException {
	MotivationTypes motivationType = null;
	if (StringUtils.isNotBlank(motivation)) {
	    motivationType = MotivationTypes.getType(motivation);
	    if (motivationType==MotivationTypes.UNKNOWN)
	      throw new HttpException("Invalid parameter value.", I18nConstantsAnnotation.INVALID_PARAM_VALUE,
	          new String[] {"motivation", motivation}, HttpStatus.BAD_REQUEST);
	}
	return motivationType;
    }
}
