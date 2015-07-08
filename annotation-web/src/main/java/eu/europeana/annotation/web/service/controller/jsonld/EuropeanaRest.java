package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
@Api(value = "europeanald", description = "Europeana Annotation-Ld Rest Service")
public class EuropeanaRest extends BaseRest{

	@RequestMapping(value = "/annotationld/{provider}/{annotationNr}.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationLdByPath (
		@RequestParam(value = "apiKey", required = false) String apiKey,
//		@RequestParam(value = "profile", required = false) String profile,
		@PathVariable(value = "provider") String provider,
		@PathVariable(value = "annotationNr") Long annotationNr
		) {
		
		String action = "get:/annotationld/{provider}/{annotationNr}.jsonld";
		return getAnnotation(apiKey, provider, annotationNr, action);	
	}

//	@RequestMapping(value = "/annotation.jsonld?provider=&annotationNr=", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/annotation.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationLd (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "annotationNr", required = true) Long annotationNr
		) {
		
		String action = "get:/annotation.jsonld";
		return getAnnotation(apiKey, provider, annotationNr, action);	
	}

	private ModelAndView getAnnotation(String apiKey, String provider,
			Long annotationNr, String action) {
		
		try {
			Annotation annotation = getAnnotationService().getAnnotationById(provider, annotationNr);
			Annotation resAnnotation = annotationBuilder
					.copyIntoWebAnnotation(annotation);
	
			JsonLd annotationLd = new EuropeanaAnnotationLd(resAnnotation);
			String jsonLd = annotationLd.toString(4);
	       	
			return JsonWebUtils.toJson(jsonLd, null);
		} catch (Exception e) {
			getLogger().error("An error occured during the invocation of :" + action, e);
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND + ". " + e.getMessage(), e);		
		}
	}
	
	@RequestMapping(value = "/annotation/{motivation}.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable(value = "motivation") String motivation,
			@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
			@RequestParam(value = "annotationNr", required = false) Long annotationNr,
			@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
			@RequestBody String annotation) {

		String action = "create:/annotation/{motivation}.jsonld";
		
		try {
			// injest motivation
			String motivationStr = "\"" + WebAnnotationFields.MOTIVATION + "\": \"" + motivation + "\",";
			String annotationStr = annotation.substring(0, 1) + motivationStr + annotation.substring(1);

			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(annotationStr);
	
			AnnotationId annoId = buildAnnotationId(provider, annotationNr);
			
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
	
	@RequestMapping(value = "/annotations/search.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
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

}
