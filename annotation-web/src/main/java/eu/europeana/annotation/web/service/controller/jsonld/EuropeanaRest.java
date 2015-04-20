package eu.europeana.annotation.web.service.controller.jsonld;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.jsonld.EuropeanaAnnotationLd;
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

	@RequestMapping(value = "/annotation/create.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
			@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
			@RequestBody String annotation) {

		// parse
		Annotation webAnnotation = getAnnotationService().parseEuropeanaAnnotation(annotation);
		String action = "create:/annotation/create.jsonld";
		annotationIdHelper = new AnnotationIdHelper();

		// validate input parameters
		if (!annotationIdHelper.validateEuropeanaProvider(webAnnotation, provider)) 
			return getValidationReport(wskey, action, AnnotationOperationResponse.ERROR_PROVIDER_DOES_NOT_MATCH);
		
		//initialize
		String collection = null;
		String object = null;
		String httpUri = null;
		
		// extract collection and object from Target object if it exists
		if (webAnnotation.getTarget() != null && webAnnotation.getTarget().getHttpUri() != null) 
			httpUri = webAnnotation.getTarget().getHttpUri();

		// extract collection and object from the first Target object if Target object list exists
//		if (StringUtils.isEmpty(httpUri)  
//			&& webAnnotation.getTargets() != null
//			&& webAnnotation.getTargets().get(0).getHttpUri() != null) 
//			httpUri = webAnnotation.getTargets().get(0).getHttpUri();
		
		String resourceId = annotationIdHelper.extractResoureIdFromHttpUri(httpUri);
		collection = annotationIdHelper.extractCollectionFromResourceId(resourceId);
		object = annotationIdHelper.extractObjectFromResourceId(resourceId);

		AnnotationId annoId = annotationIdHelper
				.initializeAnnotationId(collection, object, provider, webAnnotation.getEquivalentTo());
		
		webAnnotation.setAnnotationId(annoId);		
		Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexing);

		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation resAnnotation = annotationBuilder
				.copyIntoWebAnnotation(storedAnnotation);

		AnnotationLd annotationLd = new EuropeanaAnnotationLd(resAnnotation);
        String jsonLd = annotationLd.toString(4);
	
		return JsonWebUtils.toJson(jsonLd, null);
	}

}
