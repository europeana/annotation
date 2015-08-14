package eu.europeana.annotation.web.service.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.HttpHeaders;
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
		
	public AnnotationOperationResponse buildResponse(String message,
			String action, String apiKey) {
		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, action);
		
		response.success = true;
		response.setStatus(message);
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
		if (th !=null)
			errorMessage = th.toString() + ". " + errorMessage;
		response = buildErrorResponse(errorMessage, response.action, response.apikey);
		//logg error message
		logger.debug("Error when invoking action: " + action + " with wskey: " + apiKey);
		logger.error(errorMessage, th);
		ModelAndView ret = JsonWebUtils.toJson(response, null);
		
		
		return ret;
	}

	/**
	 * This method is employed for positive report - without errors.
	 * @param apiKey
	 * @param action
	 * @param message
	 * @return
	 */
	protected ModelAndView getReport(String apiKey, String action, String message) {
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, action);
		response = buildResponse(message, response.action, response.apikey);
		ModelAndView ret = JsonWebUtils.toJson(response, null);
		return ret;
	}

	protected AnnotationId buildAnnotationId(String provider, String identifier) throws ParamValidationException {
		
		AnnotationId annoId = getAnnotationIdHelper()
				.initializeAnnotationId(provider, identifier);
		
		annotationService.validateAnnotationId(annoId);
		
		return annoId;
	}

	protected void authorizeUser(String userToken, AnnotationId annoId) throws UserAuthorizationException {
		// TODO Auto-generated method stub
		//implement when authentication/authorization is available
	}
	
	@ExceptionHandler(HttpException.class)
//	@ResponseBody
	public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		ModelAndView res = getValidationReport(req.getParameter("wskey"), req.getServletPath(), null, ex);
//		response.sendError(ex.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		//return res;
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		//headers.add(HttpHeaders.ETAG, "" + storedAnnotation.getLastUpdate().hashCode());
		//headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

		//TODO remove the usage of Model and View
		ResponseEntity<String> responseEntity = new ResponseEntity<String>((String)res.getModel().get("json"), headers, ex.getStatus());
		
		return responseEntity;
	}

	@ExceptionHandler(RuntimeException.class)
	//@ResponseBody
	public ResponseEntity<String> handleException(Exception ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		ModelAndView res = getValidationReport(req.getParameter("wskey"), req.getServletPath(), null, ex);
		//response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		//return res;
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		//headers.add(HttpHeaders.ETAG, "" + storedAnnotation.getLastUpdate().hashCode());
		//headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

		//TODO remove the usage of Model and View
		ResponseEntity<String> responseEntity = new ResponseEntity<String>((String)res.getModel().get("json"), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		
		return responseEntity;
	}
}