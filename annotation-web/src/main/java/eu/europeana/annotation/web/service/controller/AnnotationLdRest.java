package eu.europeana.annotation.web.service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.ApiOperation;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.service.controller.jsonld.BaseJsonldRest;
import eu.europeana.api2.utils.JsonWebUtils;

//@Controller
//@Api(value = "annotationld", description = "Annotation-Ld Rest Service")
@Deprecated
public class AnnotationLdRest extends BaseJsonldRest {

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
//		@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
//		@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
		@RequestParam(value = "provider", required = true, defaultValue = WebAnnotationFields.REST_PROVIDER) String provider,
		@RequestParam(value = "annotationNr", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_NR) String identifier
		) {

//		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				provider, identifier);
//		resourceId, provider, annotationNr);
		
		AnnotationLd annotationLd = new AnnotationLd(annotation, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);
       	
		return JsonWebUtils.toJson(jsonLd, null);
	}

	
	@RequestMapping(value = "/annotationld.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ApiOperation(notes=WebAnnotationFields.SAMPLES_JSONLD_LINK, value="")
	public ModelAndView createAnnotationLd (
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
//			@RequestParam(value = "collection", required = true, defaultValue = WebAnnotationFields.REST_COLLECTION) String collection,
//			@RequestParam(value = "object", required = true, defaultValue = WebAnnotationFields.REST_OBJECT) String object,
			@RequestParam(value = "provider", required = false) String provider, // this is an ID provider
			@RequestParam(value = "identifier", required = false) String identifier,
			@RequestParam(value = "indexing", defaultValue = "true") boolean indexing,
//			@RequestBody @RequestParam(value = "annotation", required = true) String annotation) {
			@RequestBody String annotation) {

		// parse
		Annotation webAnnotation = getAnnotationService().parseAnnotation(annotation);
		String action = "create:/annotationld/collection/object.json";
		annotationIdHelper = new AnnotationIdHelper();

		// validate input parameters
//		if (!annotationIdHelper.validateResouceId(webAnnotation, collection, object))
//			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_RESOURCE_ID_DOES_NOT_MATCH);
		//TODO: change to ParamValidationException 
		if (!annotationIdHelper.validateProvider(webAnnotation, provider)) 
			return getValidationReport(apiKey, action, AnnotationOperationResponse.ERROR_PROVIDER_DOES_NOT_MATCH, null);
		
		//initialize
		AnnotationId annoId = new BaseAnnotationId(null, provider, identifier);
//		.initializeAnnotationId(collection, object, provider, webAnnotation.getSameAs());
		
		webAnnotation.setAnnotationId(annoId);		
		Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexing);

		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation resAnnotation = annotationBuilder
				.copyIntoWebAnnotation(storedAnnotation);

		AnnotationLd annotationLd = new AnnotationLd(resAnnotation, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);
	
		return JsonWebUtils.toJson(jsonLd, null);
	}

}
