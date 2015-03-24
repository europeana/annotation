package eu.europeana.annotation.web.service.controller;

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

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.api2.utils.JsonWebUtils;

@Controller
@Api(value = "annotationld", description = "Annotation-Ld Rest Service")
public class AnnotationLdRest extends BaseRest {


	@RequestMapping(value = "/annotationld/component", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName() + "-annotationLd";
	}

	private String toResourceId(String collection, String object) {
		return "/"+ collection +"/" + object;
	}
	
	@RequestMapping(value = "/annotationld/{collection}/{object}/{annotationNr}.jsonld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotationLd(@PathVariable String collection,
			@PathVariable String object, @PathVariable Integer annotationNr,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, annotationNr);
		
		AnnotationLd annotationLd = new AnnotationLd(annotation);
        String jsonLd = annotationLd.toString(4);
       	
		return JsonWebUtils.toJson(jsonLd, null);
	}

	
	@RequestMapping(value = "/annotationld/{collection}/{object}.jsonld", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotationLd(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestBody @RequestParam(value = "annotation", required = true, defaultValue = WebAnnotationFields.REST_ANNOTATION_JSON_LD) String jsonAnno) {

		Annotation storedAnnotation = getAnnotationService().createAnnotation(jsonAnno);

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
