package eu.europeana.annotation.web.service.controller;

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
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.api2.utils.JsonWebUtils;

@Controller
@Api(value = "annotationld", description = "Annotation-Ld Rest Service")
public class AnnotationLdRest extends BaseRest {


	@RequestMapping(value = "/annotationld/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "ld";
	}

	@RequestMapping(value = "/annotationld/{collection}/{object}/{provider}/{annotationNr}.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationLd (
		@RequestParam(value = "apiKey", required = false) String apiKey,
		@RequestParam(value = "profile", required = false) String profile,
		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "annotationNr", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) Integer annotationNr
		) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, provider, annotationNr);
		
		AnnotationLd annotationLd = new AnnotationLd(annotation);
        String jsonLd = annotationLd.toString(4);
       	
		return JsonWebUtils.toJson(jsonLd, null);
	}

	
	@RequestMapping(value = "/annotationld/{collection}/{object}.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
			@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
			@RequestParam(value = "provider", required = false) String provider,
			@RequestBody @RequestParam(value = "annotation", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_JSON_LD) String jsonAnno) {

//		Annotation storedAnnotation = getAnnotationService().createAnnotation(jsonAnno);
		Annotation webAnnotation = getAnnotationService().createAnnotation(jsonAnno);
		String action = "create:/annotationld/collection/object.json";
		if (!(new AnnotationIdHelper()).validateResouceId(webAnnotation, collection, object)) 
			return getValidationReport(apiKey, action);
		
		getAnnotationService().appendAnnotationId(collection, object, provider, webAnnotation);
				
		Annotation persistentAnnotation = getControllerHelper().copyIntoPersistantAnnotation(webAnnotation);		
		Annotation storedAnnotation = getAnnotationService().storeAnnotation(persistentAnnotation);

		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation resAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);

		AnnotationLd annotationLd = new AnnotationLd(resAnnotation);
        String jsonLd = annotationLd.toString(4);
	
		return JsonWebUtils.toJson(jsonLd, null);
	}

}
