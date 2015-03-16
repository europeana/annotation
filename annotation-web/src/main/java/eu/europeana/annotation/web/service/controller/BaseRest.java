package eu.europeana.annotation.web.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;

public class BaseRest {

	@Autowired
	AnnotationConfiguration configuration;
	@Autowired
	private AnnotationService annotationService;
	protected AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
	TypeUtils typeUtils = new TypeUtils();

	public BaseRest() {
		super();
	}

	protected TypeUtils getTypeUtils() {
		return typeUtils;
	}

	AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	protected AnnotationService getAnnotationService() {
		return annotationService;
	}

	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	protected AnnotationControllerHelper getControllerHelper() {
		return controllerHelper;
	}

	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = "text/*")
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName();
	}

}