package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.WhitelsitSearchResults;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.authorization.AuthorizationService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.controller.BaseRestController;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;

public class BaseRest extends BaseRestController {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	private AnnotationService annotationService;

	@Resource
	AuthorizationService authorizationService;

	@Resource
	AnnotationSearchService annotationSearchService;
	
	//TODO move to base class
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

	public AnnotationSearchResults<AbstractAnnotation> buildSearchErrorResponse(String apiKey, String action,
			Throwable th) {

		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(apiKey,
				action);
		response.success = false;
		response.error = th.getMessage();
		// response.requestNumber = 0L;

		return response;
	}

	protected AnnotationId buildAnnotationId(String identifier) throws ParamValidationException {

		return buildAnnotationId("webanno", identifier, true);
	}

	protected AnnotationId buildAnnotationId(String provider, String identifier, boolean validation) throws ParamValidationException {

		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), identifier);

		if(validation)
			annotationService.validateAnnotationId(annoId);

		return annoId;
	}

	
	protected ResponseEntity <String> buildResponse(String jsonStr) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		ResponseEntity<String> response = buildResponse(jsonStr, httpStatus);
		
		return response;		
	}
	

	protected ResponseEntity<String> buildResponse(String jsonStr, HttpStatus httpStatus) {
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
//			byte[] decodedBase64Str = Base64.decodeBase64(base64Str);
			byte[] decodedBase64Str = org.apache.commons.codec.binary.Base64.decodeBase64(base64Str.getBytes());
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
	
	@Override
	public Authentication verifyWriteAccess(String operation, HttpServletRequest request)
	        throws ApplicationAuthenticationException {
	    
	    Authentication auth = super.verifyWriteAccess(operation, request);
	    //prevent write when locked
	    getAuthorizationService().checkWriteLockInEffect(operation);
	    return auth;
	}
	        
}
