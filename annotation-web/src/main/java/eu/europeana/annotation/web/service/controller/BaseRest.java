package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.ProviderSearchResults;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class BaseRest extends ApiResponseBuilder {

	@Resource
	AnnotationConfiguration configuration;
	@Resource
	private AnnotationService annotationService;

	@Resource
	AuthenticationService authenticationService;

	@Resource
	AnnotationSearchService annotationSearchService;

	Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	public AnnotationSearchService getAnnotationSearchService() {
		return annotationSearchService;
	}

	public void setAnnotationSearchService(AnnotationSearchService annotationSearchService) {
		this.annotationSearchService = annotationSearchService;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	protected AnnotationBuilder annotationBuilder = new AnnotationBuilder();
	protected AnnotationIdHelper annotationIdHelper;

	public AnnotationIdHelper getAnnotationIdHelper() {
		if (annotationIdHelper == null)
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

	protected AnnotationConfiguration getConfiguration() {
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

	protected AnnotationBuilder getAnnotationBuilder() {
		return annotationBuilder;
	}

	public String toResourceId(String collection, String object) {
		return "/" + collection + "/" + object;
	}

	public AnnotationSearchResults<AbstractAnnotation> buildSearchResponse(List<? extends Annotation> annotations,
			String apiKey, String action) {
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(apiKey,
				action);
		response.items = new ArrayList<AbstractAnnotation>(annotations.size());

		AbstractAnnotation webAnnotation;
		for (Annotation annotation : annotations) {
			webAnnotation = getAnnotationBuilder().copyIntoWebAnnotation(annotation);
			response.items.add(webAnnotation);
		}
		response.itemsCount = response.items.size();
		response.totalResults = annotations.size();
		return response;
	}

	public ProviderSearchResults<AbstractProvider> buildProviderSearchResponse(List<? extends Provider> providers,
			String apiKey, String action) {
		ProviderSearchResults<AbstractProvider> response = new ProviderSearchResults<AbstractProvider>(apiKey, action);
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

	public AnnotationSearchResults<AbstractAnnotation> buildSearchErrorResponse(String apiKey, String action,
			Throwable th) {

		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(apiKey,
				action);
		response.success = false;
		response.error = th.getMessage();
		// response.requestNumber = 0L;

		return response;
	}

	protected AnnotationId buildAnnotationId(String provider, String identifier) throws ParamValidationException {

		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);

		annotationService.validateAnnotationId(annoId);

		return annoId;
	}

	/**
	 * This method is employed when identifier is an URL and contains provider.
	 * e.g. identifier 'http://data.europeana.eu/annotaion/base/1'
	 * 
	 * @param identifier
	 * @return AnnotationId
	 * @throws ParamValidationException
	 */
	protected AnnotationId buildAnnotationId(String identifier) throws ParamValidationException {

		if (identifier.split(WebAnnotationFields.SLASH).length < ParamValidationException.MIN_IDENTIFIER_LEN)
			return null;

		AnnotationId annoId = getAnnotationIdHelper().initializeAnnotationId(identifier);

		// annotationService.validateAnnotationId(annoId);

		return annoId;
	}

	protected void authorizeUser(String userToken, String apiKey, AnnotationId annoId)
			throws UserAuthorizationException {
		// throws exception if user is not found
		Agent user = getAuthenticationService().getUserByToken(apiKey, userToken);

		if (user.getName() == null)
			throw new UserAuthorizationException("User not authorized to perform this operation: ", user.getName());
		// TODO Auto-generated method stub
		// implement when authentication/authorization is available
	}

	/**
	 * This method extracts provider name from an identifier URL e.g. identifier
	 * 'http://data.europeana.eu/annotaion/base/1' comprises provider 'base'.
	 * 
	 * @param identifier
	 * @return provider name
	 */
	protected String extractProviderFromIdentifier(String identifier) {

		return getAnnotationIdHelper().extractProviderFromIdentifier(identifier);
	}

	protected void validateApiKey(String wsKey) throws ApplicationAuthenticationException {
		// throws exception if the wskey is not found
		getAuthenticationService().getByApiKey(wsKey);
	}

}