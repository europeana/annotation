package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.AbstractProvider;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseProvider;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.AuthorizationExtractionException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.ProviderSearchResults;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authorization.AuthorizationService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.exception.ApiKeyExtractionException;
import eu.europeana.api.commons.web.controller.ApiResponseBuilder;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.api.commons.web.model.ApiResponse;
//import eu.europeana.apikey.client.ValidationRequest;

public class BaseRest extends ApiResponseBuilder {

    /**
     * API key cache map contains apiKeys as a key and last response time as a value.
     * We only add keys if API key client responded with "204" - valid apikey.
     */
    private static Map<String, Long> apyKeyCache = new HashMap<String, Long>();
    

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	private AnnotationService annotationService;

	@Resource
	AuthenticationService authenticationService;
	
	@Resource
	AuthorizationService authorizationService;

	@Resource
	AnnotationSearchService annotationSearchService;
	
	@Resource
	private AdminService adminService;

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@Resource
	I18nService i18nService;

	@Override
	protected I18nService getI18nService() {
		return i18nService;
	}

	@Override
	public ApiResponse buildErrorResponse(String errorMessage, String action, String apiKey) {
		// TODO Auto-generated method stub
		return null;
	}

	Logger logger = LogManager.getLogger(getClass());

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

	public WhitelistEntry serializeWhitelist(WhitelistEntry entry) {
		((PersistentWhitelistEntry) entry).setId(null);
		return entry;
	}
	
	public WhitelsitSearchResults<WhitelistEntry> buildSearchWhitelistResponse(List<? extends WhitelistEntry> entries,
			String apiKey, String action) {
		WhitelsitSearchResults<WhitelistEntry> response = new WhitelsitSearchResults<WhitelistEntry>(apiKey,
				action);
		List<WhitelistEntry> webWhitelist = new ArrayList<WhitelistEntry>();
		for (WhitelistEntry entry : entries) {
			entry.setCreationDate(null);
			entry.setLastUpdate(null);
			entry.setEnableFrom(null);
			entry.setDisableTo(null);
			((PersistentWhitelistEntry) entry).setId(null);
			webWhitelist.add(entry);
		}
		response.items = webWhitelist;
		response.itemsCount = response.items.size();
		response.totalResults = entries.size();
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

		return buildAnnotationId(provider, identifier, true);
	}

	protected AnnotationId buildAnnotationId(String provider, String identifier, boolean validation) throws ParamValidationException {

		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);

		if(validation)
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
//	protected AnnotationId buildAnnotationId(String identifier) throws ParamValidationException {
//
//		if (identifier.split(WebAnnotationFields.SLASH).length < ParamValidationException.MIN_IDENTIFIER_LEN)
//			return null;
//
//		AnnotationId annoId = getAnnotationIdHelper().parseAnnotationId(identifier);
//
//		// annotationService.validateAnnotationId(annoId);
//
//		return annoId;
//	}
	
	
	/**
	 * This method extracts provider name from an identifier URL e.g. identifier
	 * 'http://data.europeana.eu/annotaion/base/1' comprises provider 'base'.
	 * 
	 * @param identifier
	 * @return provider name
	 */
	protected String extractProviderFromIdentifier(String identifier) {

		return getAnnotationIdHelper().extractProviderFromUri(identifier);
	}
	
    /**
     * This method employs API key client library for API key validation
     * @param apiKey The API key e.g. ApiKey1
     * @param method The method e.g. read, write, delete...
     * @return true if API key is valid
     * @throws ApplicationAuthenticationException 
     */
    public boolean validateApiKeyUsingClient(String apiKey, String method) throws ApplicationAuthenticationException {
    	
    	boolean res = false;
    	
    	// check in cache if there is a valid value
    	// if yes - return true
    	long currentTime = System.currentTimeMillis();
    	Long cacheTime = apyKeyCache.get(apiKey);
    	if (cacheTime != null) {
    	    long diff = currentTime - cacheTime.longValue();
    	    long configCacheTime = getConfiguration().getApiKeyCachingTime();
    	    if (diff < configCacheTime) 
    	    	return true; // we already have recent positive response from the client 
    	} 
    	
//        ValidationRequest request = new ValidationRequest(
//        		getConfiguration().getValidationAdminApiKey() // the admin API key
//        		, getConfiguration().getValidationAdminSecretKey() // the admin secret key
//        		, apiKey
//        		, getConfiguration().getValidationApi() // the name of API e.g. search, entity, annotation...
//        		);
//        
//        if (StringUtils.isNotBlank(method)) request.setMethod(method);
//        res = getAdminService().validateApiKey(request, method);
//        if (res) {
//        	apyKeyCache.put(apiKey, currentTime);
//        } else {
//        	//remove invalid from cache if exists
//        	if (apyKeyCache.containsKey(apiKey)) 
//        		apyKeyCache.remove(apiKey);
//   			
//        	throw new ApplicationAuthenticationException(null, I18nConstants.INVALID_APIKEY, new String[]{apiKey});
//        }
        return res;
    }

	protected void validateApiKey(String wsKey, String method) throws ApplicationAuthenticationException {
		
		//TODO: will not be included in the 0.2.8-RELEASE, enable in 0.2.9
//		validateApiKeyUsingClient(wsKey, method);
		
		// throws exception if the wskey is not found
		getAuthenticationService().getByApiKey(wsKey);
	}

	
	protected ResponseEntity <String> buildResponseEntityForJsonString(String jsonStr) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		ResponseEntity<String> response = buildResponseEntityForJsonString(jsonStr, httpStatus);
		
		return response;		
	}
	

	protected ResponseEntity<String> buildResponseEntityForJsonString(String jsonStr, HttpStatus httpStatus) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.ETAG, Integer.toString(hashCode()));
		headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);

		ResponseEntity<String> response = new ResponseEntity<String>(jsonStr, headers, httpStatus);
		return response;
	}

	public AuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	/**
	 * This method performs decoding of base64 string
	 * 
	 * @param base64Str
	 * @return decoded string
	 * @throws ApplicationAuthenticationException
	 */
	public String decodeBase64(String base64Str) throws ApplicationAuthenticationException {
		String res = null;
		try {
			byte[] decodedBase64Str = Base64.decodeBase64(base64Str);
			res = new String(decodedBase64Str);
		} catch (Exception e) {
			throw new ApplicationAuthenticationException(
					I18nConstants.BASE64_DECODING_FAIL, I18nConstants.BASE64_DECODING_FAIL, null);			
		}
		return res;
	}

	/**
	 * This method takes user token from a HTTP header if it exists or from the
	 * passed request parameter.
	 * 
	 * @param paramUserToken
	 *            The HTTP request parameter
	 * @param request
	 *            The HTTP request with headers
	 * @return user token
	 * @throws ApplicationAuthenticationException
	 */
	public String getUserToken(String paramUserToken, HttpServletRequest request)
			throws ApplicationAuthenticationException {
		int USER_TOKEN_TYPE_POS = 0;
		int BASE64_ENCODED_STRING_POS = 1;
		String userToken = null;
		String userTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (userTokenHeader != null) {
			logger.trace("'Authorization' header value: " + userTokenHeader);
			String[] headerElems = userTokenHeader.split(" ");
			if(headerElems.length < 2 )
				throw new ApplicationAuthenticationException(
						I18nConstants.INVALID_HEADER_FORMAT, I18nConstants.INVALID_HEADER_FORMAT, null);

			String userTokenType = headerElems[USER_TOKEN_TYPE_POS];
			if (!AnnotationHttpHeaders.BEARER.equals(userTokenType))
				throw new ApplicationAuthenticationException(
						I18nConstants.UNSUPPORTED_TOKEN_TYPE, I18nConstants.UNSUPPORTED_TOKEN_TYPE,
						new String[] {userTokenType});

			String encodedUserToken = headerElems[BASE64_ENCODED_STRING_POS];
			
			userToken = decodeBase64(encodedUserToken);
			logger.debug("Decoded user token: " + userToken);

		} else {
			//@deprecated to be removed in the next versions
			//fallback to URL param 
			userToken = paramUserToken;
		}
		return userToken;
	}
	
    /**
     * This method adopts KeyCloack token from HTTP request
     * @param request The HTTP request
     * @return list of Authentication objects
     * @throws ApplicationAuthenticationException
     * @throws ApiKeyExtractionException
     * @throws eu.europeana.api.commons.web.exception.ApplicationAuthenticationException 
     * @throws eu.europeana.api.commons.exception.AuthorizationExtractionException 
     */
    public List<? extends Authentication> processJwtToken(HttpServletRequest request) 
	    throws ApplicationAuthenticationException, ApiKeyExtractionException, AuthorizationExtractionException, 
	        eu.europeana.api.commons.web.exception.ApplicationAuthenticationException, 
	        eu.europeana.api.commons.exception.AuthorizationExtractionException {
	    return getAuthorizationService().processJwtToken(request); 	
    }	
    
    /**
     * This method verifies write access rights for particular api and operation
     * @param authenticationList The list of authentications extracted from the JWT token
     * @param operation The name of current operation
     * @return true if authenticated, false otherwise
     * @throws ApplicationAuthenticationException
     * @throws OperationAuthorizationException 
     * @throws eu.europeana.api.commons.web.exception.ApplicationAuthenticationException 
     */
	public boolean verifyWriteAccess(List<? extends Authentication> authenticationList, String operation)
			throws ApplicationAuthenticationException, OperationAuthorizationException, 
			eu.europeana.api.commons.web.exception.ApplicationAuthenticationException {
		boolean res = getAuthorizationService().authorizeWriteAccess(authenticationList, operation);
		if (!res) {
			throw new OperationAuthorizationException(I18nConstants.OPERATION_NOT_AUTHORIZED,
					I18nConstants.OPERATION_NOT_AUTHORIZED, null);
		}
		return res;
	}    
    
}