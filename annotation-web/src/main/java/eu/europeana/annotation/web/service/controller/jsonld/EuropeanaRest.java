package eu.europeana.annotation.web.service.controller.jsonld;

import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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
import eu.europeana.annotation.jsonld.EuropeanaAnnotationLd;
import eu.europeana.annotation.web.exception.FunctionalRuntimeException;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
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
	public ModelAndView getAnnotationLd (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "annotationNr", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) Long annotationNr
		) {

		Annotation annotation = getAnnotationService().getAnnotationById(provider, annotationNr);
		Annotation resAnnotation = annotationBuilder
				.copyIntoWebAnnotation(annotation);

		JsonLd annotationLd = new EuropeanaAnnotationLd(resAnnotation);
		String jsonLd = annotationLd.toString(4);
       	
		return JsonWebUtils.toJson(jsonLd, null);
	}
	
	@RequestMapping(value = "/annotation.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
			@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
			@RequestBody String annotation) {

		String action = "create:/annotation.jsonld";
		
		try{
			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(annotation);
			//annotationIdHelper = new AnnotationIdHelper();
	
			AnnotationId annoId = buildAnnotationId(wskey, provider, action,
					webAnnotation);
			
			// check whether annotation vor given provider and annotationNr already exist in database
			if (getAnnotationService().existsInDb(annoId)) 
				return getValidationReport(wskey, action, AnnotationOperationResponse.ERROR_ANNOTATION_EXISTS_IN_DB + annoId.toUri());			
			
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
		}catch (Exception e){
			throw new RuntimeException(e);
			//return getValidationReport(wskey, action, AnnotationOperationResponse.ERROR_PROVIDER_DOES_NOT_MATCH);		
		}

	}

	private AnnotationId buildAnnotationId(String wskey, String provider,
			String action, Annotation webAnnotation) {
		// validate input parameters
		if (!getAnnotationIdHelper().validateEuropeanaProvider(webAnnotation, provider)) 
			throw new FunctionalRuntimeException("invalid annotation provider: " + provider);
			
		//initialize
		String targetUri = null;
		targetUri = getTargetUri(webAnnotation);

		String[] resourceId = getAnnotationIdHelper().extractResoureIdPartsFromHttpUri(targetUri);
//		collection = getAnnotationIdHelper().extractCollectionFromResourceId(resourceId);
//		object = getAnnotationIdHelper().extractObjectFromResourceId(resourceId);

		AnnotationId annoId = getAnnotationIdHelper()
				.initializeAnnotationId(resourceId[0], resourceId[1], provider, webAnnotation.getEquivalentTo());
		return annoId;
	}

	private String getTargetUri(Annotation webAnnotation) {
		String targetUri;
		// extract collection and object from Target object if it exists
		//if (webAnnotation.getTarget() != null && webAnnotation.getTarget().getHttpUri() != null) 
		//at this stage the httpuri of the target must have bin already set
		targetUri = webAnnotation.getTarget().getHttpUri();
		
		// extract collection and object from the first Target object if Target object list exists
//		if (StringUtils.isEmpty(httpUri)  
//			&& webAnnotation.getTargets() != null
//			&& webAnnotation.getTargets().get(0).getHttpUri() != null) 
//			httpUri = webAnnotation.getTargets().get(0).getHttpUri();
	
		
		return targetUri;
	}

}
