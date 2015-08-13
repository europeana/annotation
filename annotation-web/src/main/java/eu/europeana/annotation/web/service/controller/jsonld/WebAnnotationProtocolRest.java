package eu.europeana.annotation.web.service.controller.jsonld;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.jsonld.EuropeanaAnnotationLd;
import eu.europeana.annotation.solr.exceptions.AnnotationStatusException;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api2.utils.JsonWebUtils;


/**
<CURRENT SPECIFICATION>
POST /<annotation-web>/annotation.jsonLd
GET /<annotation-web>/annotation/provider/identifier.jsonld
GET /<annotation-web>/search.jsonld
*/

@Controller
@Api(value = "web-annotation", description = "Web Annotation - Rest Service(json-ld)")
public class WebAnnotationProtocolRest extends BaseRest{

	//TODO: move to constant to authentication interfaces/classes
	public static final String USER_ANONYMOUNS = "anonymous";
	
//	@RequestMapping(value = "/annotation/{provider}/{annotationNr}.jsonld",  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public ModelAndView getAnnotationLdByPath (
		@RequestParam(value = "wskey", required = false) String wskey,
//		@RequestParam(value = "profile", required = false) String profile,
		@PathVariable(value = "provider") String provider,
		@PathVariable(value = "identifier") String identifier
		) {
		
		String action = "get:/annotationld/{provider}/{annotationNr}.jsonld";
		return getAnnotation(wskey, provider, identifier, action);	
	}

//	@RequestMapping(value = "/annotation.jsonld?provider=&annotationNr=", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value = "/annotation.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
	public ModelAndView getAnnotationLd (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "identifier", required = true) String identifier
		) {
		
		String action = "get:/annotation.jsonld";
		return getAnnotation(apiKey, provider, identifier, action);	
	}

	private ModelAndView getAnnotation(String apiKey, String provider,
			String identifier, String action) {
		
		try {
			Annotation annotationFromDb = getAnnotationService().getAnnotationById(provider, identifier);
			
			Annotation annotation = getAnnotationService().checkVisibility(annotationFromDb, null);
			
			Annotation resAnnotation = annotationBuilder
					.copyIntoWebAnnotation(annotation);
	
			JsonLd annotationLd = new EuropeanaAnnotationLd(resAnnotation);
			String jsonLd = annotationLd.toString(4);
	       	
			return JsonWebUtils.toJson(jsonLd, null);
		} catch (AnnotationStatusException e) {
			getLogger().error("An error occured during the invocation of :" + action, e);
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_VISIBILITY_CHECK + ". " + e.getMessage(), e);		
		} catch (Exception e) {
			getLogger().error("An error occured during the invocation of :" + action, e);
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND + ". " + e.getMessage(), e);		
		}
	}
	
//	@RequestMapping(value = "/annotation/{motivation}.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable(value = "motivation") String motivation,
			@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
			@RequestParam(value = "identifier", required = false) String identifier,
			@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
			@RequestBody String annotation) {

		String action = "create:/annotation/{motivation}.jsonld";
		
		try {
			// injest motivation
			String motivationStr = "\"" + WebAnnotationFields.MOTIVATION + "\": \"" + motivation + "\",";
			String annotationStr = annotation.substring(0, 1) + motivationStr + annotation.substring(1);

			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(annotationStr);
	
			AnnotationId annoId = buildAnnotationId(provider, identifier);
			
			// check whether annotation vor given provider and annotationNr already exist in database
			if (getAnnotationService().existsInDb(annoId)) 
				return getValidationReport(wskey, action, AnnotationOperationResponse.ERROR_ANNOTATION_EXISTS_IN_DB + annoId.toUri(), null);			
			
			webAnnotation.setAnnotationId(annoId);		
			Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexing);
	
			/**
			 * Convert PersistentAnnotation in Annotation.
			 */
			Annotation resAnnotation = annotationBuilder
					.copyIntoWebAnnotation(storedAnnotation);
	
			JsonLd annotationLd = new EuropeanaAnnotationLd(resAnnotation);
	        String jsonLd = annotationLd.toString(4);
	        return JsonWebUtils.toJson(jsonLd, null);			
		} catch (Exception e) {
			return getValidationReport(wskey, action, e.toString(), e);		
		}
	}
	
//	@RequestMapping(value = "/annotations/search.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	@ApiOperation(notes=WebAnnotationFields.SEARCH_NOTES, value="")
	public ModelAndView searchLd(
		@RequestParam(value = "wsKey", required = false) String wsKey,
		@RequestParam(value = "target", required = false) String target,
		@RequestParam(value = "resourceId", required = false) String resourceId) {

		List<? extends Annotation> annotationList = null;
		AnnotationSearchResults<AbstractAnnotation> response;
		
		if (StringUtils.isNotEmpty(target)) {
			annotationList = getAnnotationService().getAnnotationListByTarget(target);
		}
		if (StringUtils.isNotEmpty(resourceId)) {
			annotationList = getAnnotationService().getAnnotationListByResourceId(resourceId);
		}
		response = buildSearchResponse(
				annotationList, wsKey, "/annotations/search.jsonld");
		return JsonWebUtils.toJson(response, null);
	}

	
//	private String getTargetUri(Annotation webAnnotation) {
//		String targetUri;
//		// extract collection and object from Target object if it exists
//		//if (webAnnotation.getTarget() != null && webAnnotation.getTarget().getHttpUri() != null) 
//		//at this stage the httpuri of the target must have bin already set
//		targetUri = webAnnotation.getTarget().getHttpUri();
//		
//		// extract collection and object from the first Target object if Target object list exists
////		if (StringUtils.isEmpty(httpUri)  
////			&& webAnnotation.getTargets() != null
////			&& webAnnotation.getTargets().get(0).getHttpUri() != null) 
////			httpUri = webAnnotation.getTargets().get(0).getHttpUri();
//	
//		
//		return targetUri;
//	}
	
	@RequestMapping(value = "/annotation/", method = RequestMethod.POST, produces = {"application/ld+json", MediaType.APPLICATION_JSON_VALUE})
//	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD, value="")
	public ResponseEntity<String> createAnnotation (
			@RequestParam(value = WebAnnotationFields.WSKEY) String wskey,
			@RequestParam(value = WebAnnotationFields.PROVIDER, defaultValue = WebAnnotationFields.PROVIDER_WEBANNO) String provider, // this is an ID provider
			@RequestParam(value = WebAnnotationFields.IDENTIFIER, required = false) String identifier,
			@RequestParam(value = WebAnnotationFields.INDEX_ON_CREATE, required = false, defaultValue = "false") boolean indexOnCreate,
			@RequestBody String annotation,
			@RequestHeader(value = WebAnnotationFields.USER_TOKEN, required=false, defaultValue = USER_ANONYMOUNS) String userToken) throws ParamValidationException, UserAuthorizationException, RequestBodyValidationException, InternalServerException{

//		String action = "create:/annotation/";
//		
		try {
			
			//0. annotation id
			AnnotationId annoId = buildAnnotationId(provider, identifier);
			
			//1. authorize user
			authorizeUser(userToken, annoId);
			
			//parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(annotation);
			
			//2. validate
			// check whether annotation with the given provider and identifier already exist in the database
			if (annoId.getIdentifier()!= null && getAnnotationService().existsInDb(annoId)) 
				throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_EXISTS, "/provider/identifier", annoId.toUri());
				
			//3-6 create ID and annotation + backend validation
			webAnnotation.setAnnotationId(annoId);		
			Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexOnCreate);
	
			//serialize to jsonld
			JsonLd annotationLd = new EuropeanaAnnotationLd(storedAnnotation);
	        String jsonLd = annotationLd.toString(4);
	        //return JsonWebUtils.toJson(jsonLd, null);	
	        
	        //build response entity with headers
//	        TODO: clarify serialization ETag: "_87e52ce126126"
//	        	TODO: clarify Allow: PUT,GET,DELETE,OPTIONS,HEAD,PATCH

	        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	        headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	        headers.add(HttpHeaders.ETAG, ""+storedAnnotation.getLastUpdate().hashCode());
	        headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);
	        
	        ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.CREATED);
	        
	        return response;
	        
		} catch (JsonParseException e) {
			throw new RequestBodyValidationException(annotation, e);
		}
		catch (Exception e) {
			throw new InternalServerException(e);
		}
	}

	//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpException.class)
	@ResponseBody
	public ModelAndView handleBadRequestException(HttpException ex, HttpServletRequest req, HttpServletResponse response) throws IOException {
	
		ModelAndView res= getValidationReport(req.getParameter("wskey"), req.getServletPath(), null, ex);
		response.sendError(ex.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		return res;		
	} 
	
	//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
//		@ExceptionHandler(Exception.class)
//		@ResponseBody
//		public ModelAndView handleException(Exception ex, HttpServletRequest req, HttpServletResponse response) throws IOException {
//		
//			ModelAndView res= getValidationReport(req.getParameter("wskey"), req.getServletPath(), null, ex);
//			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//			return res;		
//		} 
}
