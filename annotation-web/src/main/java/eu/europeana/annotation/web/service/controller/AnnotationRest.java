package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api2.utils.JsonUtils;

@Controller
public class AnnotationRest {

	@Autowired
	AnnotationConfiguration configuration;

	@Autowired
	private AnnotationService annotationService;

	AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();

	AbstractAnnotationFactory factory;

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	protected AnnotationService getAnnotationService() {
		return annotationService;
	}

	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	private String toResourceId(String collection, String object) {
		return "/"+ collection +"/" + object;
	}
	
	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public AnnotationControllerHelper getControllerHelper() {
		return controllerHelper;
	}
	
	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = "text/*")
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName();
	}

	@RequestMapping(value = "/annotations/{collection}/{object}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getAnnotationList(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {
		
		String resourceId = toResourceId(collection, object);
		List<? extends Annotation> annotations = getAnnotationService()
				.getAnnotationList(resourceId);
		
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, "/annotations/collection/object");
		response.items = new ArrayList<AbstractAnnotation>(annotations.size());

		AbstractAnnotation webAnnotation;
		for (Annotation annotation : annotations) {
			webAnnotation = getControllerHelper().copyIntoWebAnnotation(
					annotation, apiKey);
			response.items.add(webAnnotation);
		}
		response.itemsCount = response.items.size();
		response.totalResults = annotations.size();

		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{collection}/{object}/{annotationNr}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotation(@PathVariable String collection,
			@PathVariable String object, @PathVariable Integer annotationNr,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, annotationNr);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/collection/object/annotationNr");

		if (annotation != null) {

			response.success = true;
			response.requestNumber = 0L;

			response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
					annotation, apiKey));
		}else{
			response.success = false;
			response.action = "get: /annotations/"+collection+"/"
					+object+"/"+annotationNr;
			
			response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
		}
		
		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{collection}/{object}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotation(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "annotation", required = true) String jsonAnno) {

		Annotation webAnnotation = JsonUtils.toAnnotationObject(jsonAnno);
		String resourceId = toResourceId(collection, object);
//		if(!europeanaId.equals(webAnnotation.getHas getResourceId()))
//			throw new FunctionalRuntimeException(FunctionalRuntimeException.MESSAGE_EUROPEANAID_NO_MATCH);
//		else if(webAnnotation.getResourceId() == null)
//			webAnnotation.setEuropeanaId(europeanaId);
//		
//		if(webAnnotation.getAnnotationNr() != null)
//			throw new FunctionalRuntimeException(FunctionalRuntimeException.MESSAGE_ANNOTATIONNR_NOT_NULL);
//		
		Annotation persistantAnnotation = getControllerHelper()
				.copyIntoPersistantAnnotation(webAnnotation, apiKey);

		Annotation storedAnnotation = getAnnotationService().createAnnotation(
				persistantAnnotation);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "create:/annotations/collection/object/");
		response.success = true;
		response.requestNumber = 0L;

		response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
				storedAnnotation, apiKey));

		return JsonUtils.toJson(response, null);
	}

	
	

}
