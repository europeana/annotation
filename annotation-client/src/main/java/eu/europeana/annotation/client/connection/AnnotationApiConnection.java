package eu.europeana.annotation.client.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.client.model.result.WhitelistOperationResponse;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.utils.parse.AnnotationPageParser;
import eu.europeana.annotation.utils.parse.WhiteListParser;

/**
 * @author GrafR
 *
 */
public class AnnotationApiConnection extends BaseApiConnection {

	String authorizationHeaderName = null;
	String regularUserAuthorizationValue = null;
	String adminUserAuthorizationValue = null;

	/**
	 * Create a new connection to the Annotation Service (REST API).
	 * 
	 * @param apiKey
	 *            API Key required to access the API
	 */
	public AnnotationApiConnection(String annotationServiceUri, String apiKey) {
		super(annotationServiceUri, apiKey);
		initConfigurations();		
	}

	public AnnotationApiConnection() {
		this(ClientConfiguration.getInstance().getServiceUri(),
				ClientConfiguration.getInstance().getApiKey());
		initConfigurations();		
	}

	private void initConfigurations() {
		authorizationHeaderName = ClientConfiguration.getInstance().getHeaderName();
		regularUserAuthorizationValue = ClientConfiguration.getInstance().getAuthorizationHeaderValue();
		adminUserAuthorizationValue = ClientConfiguration.getInstance().getAuthorizationHeaderValueForAdmin();
	}
	
	/**
	 * @param collectionId
	 * @param objectHash
	 * @return
	 * @throws IOException
	 */
	public AnnotationSearchResults getAnnotationsForObject(String collectionId,
			String objectHash) throws IOException {
		String url = getAnnotationServiceUri();
		url += WebAnnotationFields.SLASH + collectionId 
				+ WebAnnotationFields.SLASH + objectHash 
				+ ".json";
		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		
		return getAnnotationSearchResults(json);
	}
	
	/**
	 * The HTTP request sample:
	 *     http://localhost:8081/annotation-web/annotations/{collection}/{object}.json?collection=15502&object=GG_8285&provider=webanno
	 * @param collectionId
	 * @param objectHash
	 * @param provider
	 * @return
	 * @throws IOException
	 */
	public AnnotationSearchResults getAnnotationsForObject(String collectionId,
			String objectHash, String provider) throws IOException {
		String url = getAnnotationServiceUri();
		url += WebAnnotationFields.SLASH + collectionId 
				+ WebAnnotationFields.SLASH + objectHash; 
		url += WebAnnotationFields.JSON_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.COLLECTION + WebAnnotationFields.EQUALS + collectionId + WebAnnotationFields.AND;
		url += WebAnnotationFields.OBJECT + WebAnnotationFields.EQUALS + objectHash + WebAnnotationFields.AND;
		if (StringUtils.isNotEmpty(provider))
			url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider;

		// Execute Europeana API request
		String json = getJSONResult(url);
		
		AnnotationSearchResults asr = new AnnotationSearchResults();
		asr.setJson(json);
		return asr;
	}
	
	/**
	 * @param provider
	 * @param annotationNr
	 * @return
	 * @throws IOException
	 */
	public AnnotationSearchResults getAnnotationLd(String provider, Long annotationNr) throws IOException {
		return getAnnotationLd(provider, annotationNr, null);
	}
	
	/**
	 * This method retrieves the Europeana AnnotationLd object by provider and annotationNr.
	 * The HTTP request sample is:
	 *     http://localhost:8081/annotation-web/annotation.jsonld?provider=webanno&annotationNr=111
	 * @param provider
	 * @param annotationNr
	 * @param apikey
	 * @return
	 */
	public AnnotationSearchResults getAnnotationLd(String provider, Long annotationNr, String apikey) throws IOException {
		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.SLASH;
		if (StringUtils.isNotEmpty(provider))
			url += provider + WebAnnotationFields.SLASH;
		if (annotationNr != null)
			url += annotationNr + WebAnnotationFields.PAR_CHAR;
		String resApiKey = getApiKey();
		if (apikey != null) {
			resApiKey = apikey;
		}
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + resApiKey;
		
		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithHeader(url, authorizationHeaderName, regularUserAuthorizationValue);
		AnnotationSearchResults asr = new AnnotationSearchResults();
		asr.setJson(json);
		return asr;
	}
	
	/**
	 * This method searches for Europeana AnnotationLd objects by target or by resourceId.
	 * The HTTP request sample for target is:
	 *     http://localhost:8081/annotation-web/annotations/search.jsonld?target=http%3A%2F%2Fdata.europeana.eu%2Fitem%2F123%2Fxyz&
	 * The HTTP request sample for resourceId is:
	 *     http://localhost:8081/annotation-web/annotations/search.jsonld?resourceId=%2F123%2Fxyz
	 * @param target 
	 * @param resourceId
	 * @return
	 */
	public AnnotationSearchResults searchAnnotationLd(String target, String resourceId) throws IOException {
		String url = getAnnotationServiceUri() + WebAnnotationFields.SLASH; 
		url += WebAnnotationFields.SEARCH_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		if (StringUtils.isNotEmpty(target))
			url += WebAnnotationFields.TARGET + WebAnnotationFields.EQUALS + target;
		if (StringUtils.isNotEmpty(resourceId))
			url += WebAnnotationFields.RESOURCE_ID + WebAnnotationFields.EQUALS + resourceId;

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResult(url);
		AnnotationSearchResults asr = new AnnotationSearchResults();
		asr.setJson(json);
		return asr;
	}
	
	public AnnotationOperationResponse createAnnotation(Annotation annotation) throws IOException {
		String url = getAnnotationServiceUri();
        AnnotationIdHelper annotationIdHelper = new AnnotationIdHelper();
	String resourceId = annotationIdHelper.extractResourceId(annotation);
		url += resourceId;
		url += ModelConst.JSON_REST;
		url += "?collection=" + annotationIdHelper.extractCollectionFromResourceId(resourceId) 
				+ "&object=" + annotationIdHelper.extractObjectFromResourceId(resourceId); 
//				+ "&provider=" + WebAnnotationFields.PROVIDER_WEBANNO;
		// Execute Europeana API request
		String jsonPost = getAnnotationGson().toJson(annotation);
		String json = getJSONResultWithBody(url, jsonPost);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotations/collection/object.json");
		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(json);
		aor.setJson(annotationJsonString);
		return aor;
	}

	/**
	 * This method creates AnnotationLd object from JsonLd string.
	 * The HTTP request sample is:
	 *     http://localhost:8081/annotation-web/annotationld.jsonld?provider=historypin&annotationNr=1111&indexing=true
	 * @param provider
	 * @param annotationNr
	 * @param annotationJsonLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createAnnotationLd(
			String provider, Long annotationNr, String annotationJsonLdStr) throws IOException {
		
		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_LD_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
//		url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider + WebAnnotationFields.AND;
		if (annotationNr != null)
			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + annotationNr + WebAnnotationFields.AND;
		url += WebAnnotationFields.INDEX_ON_CREATE + WebAnnotationFields.EQUALS + "true";

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithBody(url, annotationJsonLdStr);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotationld.jsonld");
		aor.setJson(json);
		return aor;
	}

	/**
	 * @param motivation
	 * @param annotationNr
	 * @param europeanaLdStr
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse createEuropeanaAnnotationLd(
			String motivation, Long annotationNr, String europeanaLdStr) throws IOException {
		return createEuropeanaAnnotationLd(motivation, annotationNr, europeanaLdStr, null);		
	}
	
	/**
	 * This method creates Europeana Annotation object from JsonLd string.
	 * The HTTP request sample is:
	 *     http://localhost:8081/annotation-web/annotation.jsonld?wskey=ws&provider=historypin&annotationNr=161&indexing=true
	 *     http://localhost:8081/annotation-web/annotation/oa%3Atagging.jsonld?provider=webanno&&indexing=true
	 * @param motivation
	 * @param annotationNr
	 * @param europeanaLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createEuropeanaAnnotationLd(
			String motivation, Long annotationNr, String europeanaLdStr, String apikey) throws IOException {
		
		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations
		url += "/" + WebAnnotationFields.PAR_CHAR;
		String resApiKey = getApiKey();
		if (apikey != null) {
			resApiKey = apikey;
		}
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + resApiKey + WebAnnotationFields.AND;
		url += WebAnnotationFields.USER_TOKEN + WebAnnotationFields.EQUALS + "tester1" + WebAnnotationFields.AND;
		if (annotationNr != null)
			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + annotationNr + WebAnnotationFields.AND;
		url += WebAnnotationFields.INDEX_ON_CREATE + WebAnnotationFields.EQUALS + "true";

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithBodyAndHeader(url, europeanaLdStr, authorizationHeaderName, regularUserAuthorizationValue);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotation.jsonld");
		
		aor.setJson(json);
		return aor;
	}

	
	/**
	 * This method creates Annotation object from JsonLd string.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotation/?wskey=apidemo&provider=webanno&&indexOnCreate=false
	 * and for tag object with type jsonld
	 *     http://localhost:8080/annotation/tag.jsonld?wskey=apidemo&provider=webanno&&indexOnCreate=false
	 * @param wskey
	 * @param identifier
	 * @param indexOnCreate
	 * @param annotation The Annotation body
	 * @param userToken
	 * @param annoType
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> createAnnotation(
			Boolean indexOnCreate, 
			String annotation, String annoType) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!url.endsWith(WebAnnotationFields.SLASH))
			url +=  WebAnnotationFields.SLASH;
		if (annoType != null)
			url += annoType + WebAnnotationFields.JSON_LD_REST;
		url += WebAnnotationFields.PAR_CHAR;
		if(indexOnCreate != null )
			url += WebAnnotationFields.INDEX_ON_CREATE + WebAnnotationFields.EQUALS + indexOnCreate;		
		
		logger.trace("Ivoking create annotation: " + url);
		/**
		 * Execute Europeana API request
		 */
		return postURL(url, annotation, authorizationHeaderName, regularUserAuthorizationValue);		
	}

	
	/**
	 * This method creates Annotation report object.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotation/1222/report?wskey=apiadmin&userToken=tester1
	 * @param wskey
	 * @param identifier
	 * @param userToken
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> createAnnotationReport(
			String wskey, String identifier, String userToken) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!url.endsWith(WebAnnotationFields.SLASH))
			url +=  WebAnnotationFields.SLASH;
		url += identifier + WebAnnotationFields.SLASH;
		url += WebAnnotationFields.PATH_FIELD_REPORT;
		url += WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
		url += WebAnnotationFields.USER_TOKEN + WebAnnotationFields.EQUALS + userToken;
		
		logger.trace("Ivoking create annotation report: " + url);
		/**
		 * Execute Europeana API request
		 */
		return postURL(url, "", authorizationHeaderName, regularUserAuthorizationValue);		
	}

	
	/**
	 * This method retrieves Annotation object.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotation/webanno/497?wskey=apidemo
	 * and for tag object with type jsonld
	 *     http://localhost:8080/annotation/webanno/497.jsonld?wskey=apidemo
	 * @param wskey
	 * @param identifier
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String identifier, boolean isTypeJsonld) throws IOException {
		
		String url = getAnnotationServiceUri() + WebAnnotationFields.SLASH;
    	url += identifier;
		if (isTypeJsonld)
			url += WebAnnotationFields.JSON_LD_REST;
		url += WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
		
		/**
		 * Execute Europeana API request
		 */
		return getURL(url);		
	}


	/**
	 * This method retrieves Annotation object.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotation/webanno/497?wskey=apidemo&profile=dereference
	 * and for tag object with type jsonld
	 *     http://localhost:8080/annotation/webanno/497.jsonld?wskey=apidemo&profile=dereference
	 * @param wskey
	 * @param identifier
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> getAnnotation(
			String wskey, String identifier, boolean isTypeJsonld, SearchProfiles searchProfile) throws IOException {
		
		String url = getAnnotationServiceUri() + WebAnnotationFields.SLASH;
    	url += identifier;
		if (isTypeJsonld)
			url += WebAnnotationFields.JSON_LD_REST;
		url += WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
		url += WebAnnotationFields.PARAM_PROFILE + WebAnnotationFields.EQUALS + searchProfile.toString();
		
		/**
		 * Execute Europeana API request
		 */
		return getURL(url);		
	}


	/**
	 * This method retrieves summary of the Annotation moderation report object.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotation/1202/moderationsummary?wskey=apiadmin&userToken=tester1
	 * @param wskey
	 * @param identifier
	 * @param userToken
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> getModerationReport(
			String wskey, String identifier) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!url.endsWith(WebAnnotationFields.SLASH))
			url +=  WebAnnotationFields.SLASH;
		url += identifier + WebAnnotationFields.SLASH;
		url += WebAnnotationFields.PATH_FIELD_MODERATION_SUMMARY;
		url += WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
//		url += WebAnnotationFields.USER_TOKEN + WebAnnotationFields.EQUALS + userToken;
		
		logger.trace("Ivoking get annotation moderation report: " + url);
		
		/**
		 * Execute Europeana API request
		 */
		return getURLWithHeader(url, authorizationHeaderName, regularUserAuthorizationValue);		
	}


	/**
	 * This method updates Annotation object by the passed JsonLd update string.
	 * Example HTTP request: 
	 *      http://localhost:8080/annotation/{provider}/{identifier}?wskey=apidemo
	 * where identifier is:
	 *     http://data.europeana.eu/annotation/webanno/496
	 * and the update JSON string is:
	 *     { "body": "Buccin Trombone","target": "http://data.europeana.eu/item/09102/_UEDIN_2214" }
	 * @param wskey
	 * @param identifier The identifier URL that comprise annotation provider and ID
	 * @param updateAnnotation The update Annotation body in JSON format
	 * @param userToken
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> updateAnnotation(
			String identifier, String updateAnnotation) throws IOException {
		
		//TODO:refactor to use an abstract implementation for building client urls 
		String url = getAnnotationServiceUri();
		url += WebAnnotationFields.SLASH + identifier;		
//		url += WebAnnotationFields.PAR_CHAR;
//		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
//		url += WebAnnotationFields.USER_TOKEN + WebAnnotationFields.EQUALS + userToken;
		/**
		 * Execute Europeana API request
		 */
		return putURL(url, updateAnnotation, authorizationHeaderName, regularUserAuthorizationValue);		
	}
	/**
	 * @Deprecated see new specifications for WebAnnotationProtocol
	 * This method deletes Annotation object by the passed identifier.
	 * Example HTTP request: 
	 *      http://localhost:8080/annotation/{identifier}.jsonld?wskey=apidemo&identifier=http%3A%2F%2Fdata.europeana.eu%2Fannotation%2Fwebanno%2F494
	 * where identifier is:
	 *     http://data.europeana.eu/annotation/webanno/494
	 * @param wskey
	 * @param identifier The identifier URL that comprise annotation provider and ID
	 * @param userToken
	 * @param format 
	 * @return response entity that comprises response headers and status code.
	 * @throws IOException
	 * @deprecated TODO: remove deprecation or method when the json API will be enabled
	 */
//	public ResponseEntity<String> deleteAnnotation(
//			String wskey, String identifier, String userToken) throws IOException {
//		
//		String url = getAnnotationServiceUri() + WebAnnotationFields.SLASH;
//		url += encodeUrl("{") + WebAnnotationFields.IDENTIFIER + encodeUrl("}") + WebAnnotationFields.JSON_LD_REST;
//		url += WebAnnotationFields.PAR_CHAR;
//		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + wskey + WebAnnotationFields.AND;
//		if (identifier != null)
//			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + encodeUrl(identifier) + WebAnnotationFields.AND;
//		url += WebAnnotationFields.USER_TOKEN + WebAnnotationFields.EQUALS + userToken;	
//		
//		/**
//		 * Execute Europeana API request
//		 */
//		return deleteURL(url, authorizationHeaderName, regularUserAuthorizationValue);		
//	}

	/**
	 * This method deletes Annotation object by the passed identifier.
	 * Example HTTP request: 
	 *      http://localhost:8080/annotation/provider/identifier{.jsonld}?wskey=apidemo
	 * where identifier is:
	 *     http://data.europeana.eu/annotation/webanno/494
	 * @param wskey
	 * @param identifier The identifier URL that comprise annotation provider and ID
	 * @param userToken
	 * @param format 
	 * @return response entity that comprises response headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> deleteAnnotation(
			String identifier) throws IOException {
		
		String url = getAnnotationServiceUri();
		url += WebAnnotationFields.SLASH + identifier;
		
		/**
		 * Execute Europeana API request
		 */
		return deleteURL(url, authorizationHeaderName, regularUserAuthorizationValue);		
	}

	
	/**
	 * This method creates Annotation object from JsonLd string.
	 * @param annotationJsonLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createAnnotation(String annotationJsonLdStr) throws IOException {

		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.PARAM_WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		
		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithBody(url, annotationJsonLdStr);		
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotations/collection/object.json");
		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(json);
		aor.setAnnotation(JsonUtils.toAnnotationObject(annotationJsonString));
		return aor;
	}

	/**
	 * This method returns a list of Annotation objects for the passed query.
     * E.g. /annotation-web/annotations/search?
     *     wskey=key&profile=webtest&value=vlad&field=all&language=en&startOn=0&limit=&search=search	 
     * @param query The query string 
     * @param page Start page
     * @param pageSize Page size
	 * @return annotation operation response
	 * @throws IOException
	 * @throws JsonParseException 
	 */
	public AnnotationPage search(String query, String page, String pageSize) 
			throws IOException, JsonParseException {
		
		return search(query, page, pageSize, SearchProfiles.STANDARD, null); 
		
	}
	

	/**
	 * This method returns a list of Annotation objects for the passed query according to the given search profile.
     * E.g. /annotation-web/annotations/search?wskey=key&profile=webtest&value=vlad&field=all&language=en&startOn=0&limit=&search=search	 
     * @param query The query string
     * * @param language Response language
	  
	 * @return AnnotationPage object
	 * @throws IOException
	 * @throws JsonParseException 
	 */
	public AnnotationPage search(String query, String page, String pageSize, SearchProfiles searchProfile, String language) 
			throws IOException, JsonParseException {
//	    String DEFAULT_LANGUAGE = "en,en-US";
			
	    String url = buildUrl(query, page, pageSize, searchProfile.toString(), language); 
		
	 // Execute Europeana API request
	 String json = getJSONResultWithHeader(url, authorizationHeaderName, regularUserAuthorizationValue);
	 		
	    
		// Execute Europeana API request
//		String json = getJSONResult(url);
		
		return getAnnotationPage(json);
	}


	/**
	 * This method converts json response in AnnotationSearchResults.
	 * @param json
	 * @return AnnotationSearchResults
	 * @throws JsonParseException 
	 */
	public AnnotationPage getAnnotationPage(String json) throws JsonParseException {
		AnnotationPageParser apParser = new AnnotationPageParser();
		AnnotationPage ap = apParser.parseAnnotationPage(json);
		return ap;
	}

	/**
	 * This method converts json response in AnnotationSearchResults.
	 * @param json
	 * @return AnnotationSearchResults
	 */
	public AnnotationSearchResults getAnnotationSearchResults(String json) {
		AnnotationSearchResults asr = new AnnotationSearchResults();
		asr.setSuccess("true");
		asr.setAction("create:/annotations/search");
		String annotationListJsonString = JsonUtils.extractAnnotationListStringFromJsonString(json, "\":(.*?)}]");
		if (StringUtils.isNotEmpty(annotationListJsonString)) {
	        if (!annotationListJsonString.isEmpty()) {
	        	annotationListJsonString = 
	        			annotationListJsonString.substring(1, annotationListJsonString.length() - 1); // remove braces
		        String[] arrValue = JsonLdParser.splitAnnotationListStringToArray(annotationListJsonString);
		        List<Annotation> annotationList = new ArrayList<Annotation>();
		        for (String annotationJsonString : arrValue) {
		        	if (!annotationJsonString.startsWith("{"))
		        		annotationJsonString = "{" + annotationJsonString;
		    		Annotation annotationObject = JsonUtils.toAnnotationObject(annotationJsonString + "}}");
					annotationList.add(annotationObject);
		    	}
		        asr.setItems(annotationList);
	        }
		}
		return asr;
	}

	/**
	 * This method constructs the url dependent on search parameters.
	 * @param query Query
	 * @param page Start page
	 * @param pageSize Page size
	 * @param profile Query profile
	 * @param language Query language
	 * @return query Query URL
	 */
	private String buildUrl(String query, String page, String pageSize, String profile, String language)  throws IOException {
		String url = getAnnotationServiceUri();
		url += "/search?wskey=" + getApiKey() + "&profile=" + profile;
		if (StringUtils.isNotEmpty(query)) {
			url += "&query=" + encodeUrl(query);
		}
		if (StringUtils.isNotEmpty(page))
			url += "&page=" + page;
		else
			url += "&page=0";
		if (StringUtils.isNotEmpty(pageSize))
			url += "&pageSize=" + pageSize;
		else
			url += "&pageSize=10";
		if (StringUtils.isNotEmpty(language)) {
			url += "&language=" + language;
		}
		return url;
	}

	public AnnotationPage search(String query) throws IOException, JsonParseException {
		return search(query, null, null);
	}



	
//	public TagSearchResults searchTags(String query) throws IOException {
//	    return searchTags(query, null, null);
//	}	
		
	public AnnotationOperationResponse getAnnotation(
			String europeanaId, String identifier) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!europeanaId.startsWith(WebAnnotationFields.SLASH))
			url += WebAnnotationFields.SLASH ;
		
		url += europeanaId;
		url += WebAnnotationFields.SLASH + identifier;
		
		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		
		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
				AnnotationOperationResponse.class);

	}


	/**
	 * Sample HTTP request:
	 *     //http://localhost:8081/annotation-web/annotations/set/status/{provider}/{annotationNr}.json?provider=webanno&annotationNr=214&status=public
	 *     http://localhost:8081/annotation-web/admin/set/status/{provider}/{annotationNr}.json?provider=webanno&annotationNr=294&status=public
	 * @param identifier
	 * @param status
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse setAnnotationStatus(String identifier, String status) throws IOException {
		String url = getAnnotationServiceUri().replace("annotations","admin"); // current annotation service uri is .../annotation-web/annotations
		url += "/set/status/" + identifier + ".json" + WebAnnotationFields.PAR_CHAR;
		if (identifier != null)
			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + identifier + WebAnnotationFields.AND;
		url += WebAnnotationFields.STATUS + WebAnnotationFields.EQUALS + status;

		String json = getJSONResultWithBody(url, status);		
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("set:/annotations/set/status/object.json");
		aor.setJson(json);
		return aor;
	}

	/**
	 * Sample HTTP request:
	 *     http://localhost:8081/annotation-web/statuslogs/search?status=private&startOn=0&limit=10
	 * @param status
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse searchAnnotationStatusLogs(String status, String startOn, String limit) throws IOException {
		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations
		url += "/statuslogs/search" + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.STATUS + WebAnnotationFields.EQUALS + status + WebAnnotationFields.AND;
		url += WebAnnotationFields.START_ON + WebAnnotationFields.EQUALS + startOn + WebAnnotationFields.AND;
		url += WebAnnotationFields.LIMIT + WebAnnotationFields.EQUALS + limit;

		String json = getJSONResult(url);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("search:/statuslogs.json");
		aor.setJson(json);
		return aor;
	}

	/**
	 * Sample HTTP request:
	 *     http://localhost:8081/annotation-web/annotations/get/status/{annotationNr}.json?provider=webanno&annotationNr=214
	 * @param provider
	 * @param identifier
	 * @param status
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse getAnnotationStatus(String identifier) throws IOException {
		String url = getAnnotationServiceUri(); 
		url += "/get/status/" + identifier + ".json" + WebAnnotationFields.PAR_CHAR;
		if (identifier != null)
			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + identifier;

		String json = getJSONResult(url);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("get:/annotations/get/status/object.json");
		aor.setJson(json);
		return aor;
	}

		
	/**
	 * Sample HTTP request:
	 *     http://localhost:8081/annotation-web/admin/annotation/disable/webanno/441.json
	 * @param identifier
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse disableAnnotation(String identifier) throws IOException {
		String url = getAnnotationServiceUri(); // current annotation service uri is .../annotation-web/annotations 
		url += "/admin/annotation/disable/" + identifier + ".json";
		String json = getJSONResultWithBody(url, "");		
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("put:/admin/annotation/disable/object.json");
		aor.setJson(json);
		return aor;
	}

		
	/**
	 * Sample HTTP request:
	 *     http://localhost:8081/annotation-web/annotations/check/visibility/{annotationNr}/{user}.json?provider=webanno&annotationNr=407&
	 * @param provider
	 * @param annotationNr
	 * @return
	 * @throws IOException
	 */
	public AnnotationOperationResponse checkVisibility(Annotation annotation, String user) throws IOException {
		String url = getAnnotationServiceUri();
		String identifier = annotation.getAnnotationId().getIdentifier();
		url += "/check/visibility/" + identifier + "/" + user + ".json" + WebAnnotationFields.PAR_CHAR;
		if (identifier != null)
			url += WebAnnotationFields.IDENTIFIER + WebAnnotationFields.EQUALS + identifier + WebAnnotationFields.AND;
		url += WebAnnotationFields.USER + WebAnnotationFields.EQUALS + user;

		String json = getJSONResult(url);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("get:/annotations/check/visibility/object.json");
		aor.setJson(json);
		return aor;
	}

		
	/**
	 * Sample HTTP request
	 *     http://localhost:8080/whitelist/create?apiKey=apidemo
	 * @param whitelistEntryJson
	 * @return WhitelistOperationResponse
	 * @throws IOException
	 */
	public WhitelistOperationResponse createWhitelistEntry(String whitelistEntryJson) throws IOException {
		String action = "create";
		String url = getWhitelistServiceUrl(action);
		
		// Execute Whitelist API request
		String json = postURL(url, whitelistEntryJson, authorizationHeaderName, adminUserAuthorizationValue).getBody();
		
		WhitelistOperationResponse aor = new WhitelistOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/whitelist");
		String whitelistJsonString = "";
		try {
			JSONObject mainObject = new JSONObject(json);
			JSONObject whitelistEntry = mainObject.getJSONObject("whitelistEntry");
			whitelistJsonString = whitelistEntry.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		aor.setWhitelistEntry(WhiteListParser.toWhitelistEntry(whitelistJsonString));
		aor.setJson(json);
		return aor;
	}

	protected String getWhitelistServiceBaseUri() {
		return getAnnotationServiceUri().replace("annotation","whitelist") + "/";
	}

	
	/**
	 * Sample HTTP request
	 *     http://localhost:8080/whitelist/load?apiKey=apidemo
	 * @return WhitelistOperationResponse
	 * @throws IOException
	 */
	public WhitelistOperationResponse loadWhitelist() throws IOException {
		String action = "load"; 
		String url = getWhitelistServiceUrl(action);	
		
		// Execute Whitelist API request
		String json = getJSONResult(url);
		
		WhitelistOperationResponse aor = new WhitelistOperationResponse();
		aor.setSuccess("true");
		aor.setAction("load:/whitelist");
		List<WhitelistEntry> resList = new ArrayList<WhitelistEntry>();
		try {
			JSONObject mainObject = new JSONObject(json);
			JSONArray whitelistEntries = mainObject.getJSONArray("items");
			for (int i=0; i < whitelistEntries.length(); i++) {
			    JSONObject entry = (JSONObject) whitelistEntries.get(0);
			    WhitelistEntry whitelistEntry = WhiteListParser.toWhitelistEntry(entry.toString());
			    resList.add(whitelistEntry);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		aor.setWhitelistEntries(resList);
		aor.setJson(json);
		return aor;
	}

	protected String getWhitelistServiceUrl(String action) {
		String url = getWhitelistServiceBaseUri()
				+ action; 
//		url += WebAnnotationFields.PAR_CHAR + WebAnnotationFields.PARAM_WSKEY + "=" 
//				+ getAdminApiKey();
		//TODO: add admin user tocken to properties
//		url += WebAnnotationFields.AND + WebAnnotationFields.USER_TOKEN +"=admin";
		return url;
	}

	
	/**
	 * Sample HTTP request
	 *     http://localhost:8080/whitelist/search?apiKey=apidemo&url=http%3A%2F%2Ftest.data.europeana.eu
	 * @param httpUrl
	 * @return WhitelistOperationResponse
	 * @throws IOException
	 */
	public WhitelistOperationResponse getWhitelistEntry(
			String httpUrl) throws IOException {
		
		String action = "search";
		String url = getWhitelistServiceUrl(action);	

		url += WebAnnotationFields.PAR_CHAR + "url=" + httpUrl;
		// Execute Whitelist API request
		String json = getJSONResultWithHeader(url, authorizationHeaderName, adminUserAuthorizationValue);
		
		WhitelistOperationResponse aor = new WhitelistOperationResponse();
		aor.setSuccess("true");
		aor.setAction("search:/whitelist");
		String whitelistJsonString = "";
		try {
			JSONObject mainObject = new JSONObject(json);
			JSONObject whitelistEntry = mainObject.getJSONObject("whitelistEntry");
			whitelistJsonString = whitelistEntry.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		aor.setWhitelistEntry(WhiteListParser.toWhitelistEntry(whitelistJsonString));
		aor.setJson(json);
		return aor;
	}

	
	/**
	 * Sample HTTP request
	 *     http://localhost:8080/whitelist/view?apiKey=apidemo
	 * @return WhitelistOperationResponse
	 * @throws IOException
	 */
	public WhitelistOperationResponse getWhitelist() throws IOException {
		
		String action = "view";
		String url = getWhitelistServiceUrl(action);	
	
		
		// Execute Whitelist API request
		String json = getJSONResultWithHeader(url, authorizationHeaderName, adminUserAuthorizationValue);
		
		WhitelistOperationResponse aor = new WhitelistOperationResponse();
		aor.setSuccess("true");
		aor.setAction("view:/whitelist");
		List<WhitelistEntry> resList = new ArrayList<WhitelistEntry>();
		try {
			JSONObject mainObject = new JSONObject(json);
			JSONArray whitelistEntries = mainObject.getJSONArray("items");
			for (int i=0; i < whitelistEntries.length(); i++) {
			    JSONObject entry = (JSONObject) whitelistEntries.get(0);
			    WhitelistEntry whitelistEntry = WhiteListParser.toWhitelistEntry(entry.toString());
			    resList.add(whitelistEntry);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		aor.setWhitelistEntries(resList);
		aor.setJson(json);
		return aor;
	}

	
	/**
	 * Sample HTTP request
	 * 	   http://localhost:8080/whitelist/delete?apiKey=apidemo&url=http%3A%2F%2Ftest.data.europeana.eu
	 * @param httpUrl
	 * @return ResponseEntity<String>
	 * @throws IOException
	 */
	public ResponseEntity<String> deleteWhitelistEntry(
			String httpUrl) throws IOException {
		
		String action = "delete";
		String url = getWhitelistServiceUrl(action);	
	
				
		url += WebAnnotationFields.PAR_CHAR + "url=" + httpUrl;
		// Execute Whitelist API request
		return deleteURL(url, authorizationHeaderName, adminUserAuthorizationValue);
	}

	
	/**
	 * Sample HTTP request
	 * 	   http://localhost:8080/whitelist/deleteall?apiKey=apidemo
	 * @return ResponseEntity<String>
	 * @throws IOException
	 */
	public ResponseEntity<String> deleteWholeWhitelist() throws IOException {
		
		String action = "deleteall";
		String url = getWhitelistServiceUrl(action);	

		
		// Execute Whitelist API request
		return deleteURL(url, authorizationHeaderName, adminUserAuthorizationValue);
	}
	
	
	/**
	 * Sample HTTP request
	 * 	   curl -X DELETE --header 'Accept: application/ld+json' 'http://localhost:8080/admin/annotation/delete?wskey=apiadmin&provider=webanno&identifier=19&userToken=admin'
	 * @return ResponseEntity<String>
	 * @throws IOException
	 */
	public ResponseEntity<String> deleteAnnotationForGood(String identifier) throws IOException {
		
		String action = "delete";
		
		logger.debug("Annotation service URI: " +getAnnotationServiceUri());	
		String adminAnnotationServiceUri = getAnnotationServiceUri().replace("annotation", "admin/annotation");
		logger.trace("Admin annotation service URI: " +adminAnnotationServiceUri);	
		
		String url = adminAnnotationServiceUri+ WebAnnotationFields.SLASH + action ; 	
		url += WebAnnotationFields.AND + WebAnnotationFields.IDENTIFIER +"="+identifier;
		
		logger.trace("Delete Annotation request URL: " + url);
		// Execute Annotation delete request
		ResponseEntity<String> re = deleteURL(url, authorizationHeaderName, adminUserAuthorizationValue);
		logger.trace(re.toString());

		return re;
	}
	
	/**
	 * Sample HTTP request
	 *     curl -X GET --header 'Accept: application/ld+json' 'http://localhost:8080/admin/annotation/reindexoutdated?wskey=apidemo&userToken=admin'
	 * @return ResponseEntity<String>
	 * @throws IOException
	 */
	public ResponseEntity<String> reindexOutdated() throws IOException {
		
		String action = "reindexoutdated";
		
		logger.debug("Annotation service URI: " +getAnnotationServiceUri());	
		String adminAnnotationServiceUri = getAnnotationServiceUri().replace("annotation", "admin/annotation");
		logger.trace("Admin annotation service URI: " +adminAnnotationServiceUri);	
		
		String url = adminAnnotationServiceUri+ WebAnnotationFields.SLASH + action ; 	
//		url += WebAnnotationFields.PAR_CHAR + WebAnnotationFields.PARAM_WSKEY + "=" + getAdminApiKey();
//		url += WebAnnotationFields.AND + WebAnnotationFields.USER_TOKEN +"=admin";
		
		logger.trace("(Re)index outdated annotations request URL: " + url);
		ResponseEntity<String> res = getURLWithHeader(url, authorizationHeaderName, adminUserAuthorizationValue);
//		ResponseEntity<String> res = getHttpConnection().getURL(url);
		logger.trace("(Re)index outdated annotations HTTP status: " + res.getStatusCode().toString());
		
		return res;
	}
	
	
	
	/**
	 * This method uploads annotations passed as annotations page json.
	 * Example HTTP request for tag object: 
	 *      http://localhost:8080/annotations/?wskey=apidemo&userToken=tester1
	 * @param wskey
	 * @param userToken
	 * @return response entity that comprises response body, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> uploadAnnotations(
			String tag, Boolean indexOnCreate) throws IOException {
		String url = getAnnotationServiceUri()+"s";
		if(!url.endsWith(WebAnnotationFields.SLASH))
			url +=  WebAnnotationFields.SLASH;
		url += WebAnnotationFields.INDEX_ON_CREATE + WebAnnotationFields.EQUALS + indexOnCreate;
		
		logger.debug("Upload annotations request URL: " + url);
		
		/**
		 * Execute Europeana API request
		 */
		return postURL(url, tag, authorizationHeaderName, regularUserAuthorizationValue);		
	}

}
