package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.web.exception.ParamValidationException;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.ProviderSearchResults;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api2.utils.JsonWebUtils;

public class BaseRest {

	@Autowired
	AnnotationConfiguration configuration;
	@Autowired
	private AnnotationService annotationService;
	protected AnnotationBuilder annotationBuilder = new AnnotationBuilder();
	protected AnnotationIdHelper annotationIdHelper;

	Logger logger = Logger.getLogger(getClass());
	
	public Logger getLogger() {
		return logger;
	}

	public AnnotationIdHelper getAnnotationIdHelper() {
		if(annotationIdHelper == null)
			annotationIdHelper = new AnnotationIdHelper();
		return annotationIdHelper;
	}

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

	protected AnnotationBuilder getControllerHelper() {
		return annotationBuilder;
	}
	
	public String toResourceId(String collection, String object) {
		return "/"+ collection +"/" + object;
	}
	
	public AnnotationSearchResults<AbstractAnnotation> buildSearchResponse(
			List<? extends Annotation> annotations, String apiKey, String action) {
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, action);
		response.items = new ArrayList<AbstractAnnotation>(annotations.size());

		AbstractAnnotation webAnnotation;
		for (Annotation annotation : annotations) {
			webAnnotation = getControllerHelper().copyIntoWebAnnotation(annotation);
			response.items.add(webAnnotation);
		}
		response.itemsCount = response.items.size();
		response.totalResults = annotations.size();
		return response;
	}

	public ProviderSearchResults<AbstractProvider> buildProviderSearchResponse(
			List<? extends Provider> providers, String apiKey, String action) {
		ProviderSearchResults<AbstractProvider> response = new ProviderSearchResults<AbstractProvider>(
				apiKey, action);
		response.items = new ArrayList<AbstractProvider>(providers.size());

		for (Provider provider : providers) {
			BaseProvider webProvider = new BaseProvider();
			webProvider.setName(provider.getName());
			webProvider.setUri(provider.getUri());
			webProvider.setIdGeneration(IdGenerationTypes.getValueByType(provider.getIdGeneration()));
			response.items.add(webProvider);
		}
		response.itemsCount = response.items.size();
		response.totalResults = providers.size();
		return response;
	}

	public AnnotationSearchResults<AbstractAnnotation> buildSearchErrorResponse(
			String apiKey, String action, Throwable th) {
		
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, action);
		response.success = false;
		response.error = th.getMessage();
//		response.requestNumber = 0L;
		
		return response;
	}
		
	public AnnotationOperationResponse buildErrorResponse(String errorMessage,
			String action, String apiKey) {
		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, action);
		
		response.success = false;
		response.error = errorMessage;
		return response;
	}

	/**
	 * This method generates validation error report.
	 * @param apiKey
	 * @param action
	 * @param errorMessage
	 * @return
	 */
	protected ModelAndView getValidationReport(String apiKey, String action, String errorMessage, Throwable th) {
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, action);
		if (StringUtils.isEmpty(errorMessage))
			errorMessage = th.toString();
		response = buildErrorResponse(errorMessage, response.action, response.apikey);
		//logg error message
		logger.debug("Error when invoking action: " + action + " with wskey: " + apiKey);
		logger.error(errorMessage, th);
		ModelAndView ret = JsonWebUtils.toJson(response, null);
		return ret;
	}

	protected ModelAndView getValidationReport(String apiKey, String action, String errorMessage) {
		return getValidationReport(apiKey, action, errorMessage, null);
	}

	//
	protected AnnotationId buildAnnotationId(String provider, Long annotationNr) throws ParamValidationException {
		// validate input parameters
//		if (!getAnnotationIdHelper().validateEuropeanaProvider(webAnnotation, provider)) 
//			
//			
		//initialize
//		String targetUri = null;
//		targetUri = getTargetUri(webAnnotation);
//
//		String[] resourceId = getAnnotationIdHelper().extractResoureIdPartsFromHttpUri(targetUri);
//		collection = getAnnotationIdHelper().extractCollectionFromResourceId(resourceId);
//		object = getAnnotationIdHelper().extractObjectFromResourceId(resourceId);

		validateProviderAndAnnotationNr(provider, annotationNr);
		
		AnnotationId annoId = getAnnotationIdHelper()
				.initializeAnnotationId(provider, annotationNr);
		return annoId;
	}
	
	
	private void validateProviderAndAnnotationNr(String provider, Long annotationNr) throws ParamValidationException {
		if(WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider)){
			if(annotationNr== null ||  annotationNr<1)
				throw new ParamValidationException("Invalid annotationNr for provider! " + provider + ":" + annotationNr);
		}else if(WebAnnotationFields.PROVIDER_WEBANNO.equals(provider)){
			if(annotationNr!= null)
				throw new ParamValidationException("AnnotationNr must not be set for provider! " + provider + ":" + annotationNr);
		}else{
			throw new ParamValidationException(WebAnnotationFields.INVALID_PROVIDER + " " + provider);
		}
		
	}
	
}