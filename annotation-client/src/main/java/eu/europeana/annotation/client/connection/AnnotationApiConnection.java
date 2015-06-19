package eu.europeana.annotation.client.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLdParser;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.client.model.result.TagSearchResults;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.utils.JsonUtils;

public class AnnotationApiConnection extends BaseApiConnection {

	/**
	 * Create a new connection to the Annotation Service (REST API).
	 * 
	 * @param apiKey
	 *            API Key required to access the API
	 */
	public AnnotationApiConnection(String annotationServiceUri, String apiKey) {
		super(annotationServiceUri, apiKey);
	}

	public AnnotationApiConnection() {
		this(ClientConfiguration.getInstance().getServiceUri(),
				ClientConfiguration.getInstance().getApiKey());
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
//
//		Gson gson = getAnnotationGson();
//
//		return (AnnotationSearchResults) gson.fromJson(json,
//				AnnotationSearchResults.class);
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
//				+ WebAnnotationFields.SLASH + provider 
//				+ ".json";
		url += WebAnnotationFields.JSON_REST + WebAnnotationFields.PAR_CHAR;
//		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		url += WebAnnotationFields.COLLECTION + WebAnnotationFields.EQUALS + collectionId + WebAnnotationFields.AND;
		url += WebAnnotationFields.OBJECT + WebAnnotationFields.EQUALS + objectHash + WebAnnotationFields.AND;
		if (StringUtils.isNotEmpty(provider))
			url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider;// + WebAnnotationFields.AND;
//		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		
//		return getAnnotationSearchResults(json);
		AnnotationSearchResults asr = new AnnotationSearchResults();
		asr.setJson(json);
		return asr;

//
//		Gson gson = getAnnotationGson();
//
//		return (AnnotationSearchResults) gson.fromJson(json,
//				AnnotationSearchResults.class);
	}
	
	/**
	 * This method retrieves the Europeana AnnotationLd object by provider and annotationNr.
	 * The HTTP request sample is:
	 *     http://localhost:8081/annotation-web/annotation.jsonld?provider=webanno&annotationNr=111
	 * @param provider
	 * @param annotationNr
	 * @return
	 */
	public AnnotationSearchResults getAnnotationLd(String provider, Long annotationNr) throws IOException {
		String url = getAnnotationServiceUri().replace("annotations",""); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		if (StringUtils.isNotEmpty(provider))
			url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider + WebAnnotationFields.AND;
		if (annotationNr != null)
			url += WebAnnotationFields.ANNOTATION_NR + WebAnnotationFields.EQUALS + annotationNr;

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResult(url);
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
		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
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
	
//	public AnnotationSearchResults convertAnnotationToAnnotationLdString(Annotation annotation) throws IOException {
//		String url = getAnnotationServiceUri();
//		url += "/" + collectionId + "/" + objectHash + ".json";
//		url += "?wsKey=" + getApiKey() + "&profile=annotation";
//
//		// Execute Europeana API request
//		String json = getJSONResult(url);
//		
//		return getAnnotationSearchResults(json);
//	}
	
	public AnnotationOperationResponse createAnnotation(Annotation annotation) throws IOException {
		String url = getAnnotationServiceUri();
        String resourceId = (new AnnotationIdHelper()).extractResourceId(annotation);
		url += resourceId;
		url += ModelConst.JSON_REST;
		url += "?collection=" + (new AnnotationIdHelper()).extractCollectionFromResourceId(resourceId) 
				+ "&object=" + (new AnnotationIdHelper()).extractObjectFromResourceId(resourceId) 
				+ "&provider=" + WebAnnotationFields.PROVIDER_WEBANNO;
		// Execute Europeana API request
		String jsonPost = getAnnotationGson().toJson(annotation);
//		String json = getJSONResult(url, ModelConst.ANNOTATION, jsonPost);
		String json = getJSONResultWithBody(url, jsonPost);
		
		//JSONObject jsonObj = (JSONObject) new JSONParser().parse(json);
//		AnnotationOperationResponse aor = new ObjectMapper().readValue(json, AnnotationOperationResponse.class);
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotations/collection/object.json");
		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(json);
//		annotationJsonString = annotationJsonString
//				.replace("\"\\\"", "").replace("\\\"\"","").replace(":[",":\"[").replace("],","]\",").replace("]", "]\"");
//		aor.setAnnotation(JsonUtils.toAnnotationObject(annotationJsonString));
		aor.setJson(annotationJsonString);
		return aor;

//		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
//				AnnotationOperationResponse.class);
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
		
		String url = getAnnotationServiceUri().replace("annotations",""); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_LD_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider + WebAnnotationFields.AND;
		if (annotationNr != null)
			url += WebAnnotationFields.ANNOTATION_NR + WebAnnotationFields.EQUALS + annotationNr + WebAnnotationFields.AND;
		url += WebAnnotationFields.INDEXING + WebAnnotationFields.EQUALS + "true";

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
	 * This method creates Europeana Annotation object from JsonLd string.
	 * The HTTP request sample is:
	 *     http://localhost:8081/annotation-web/annotation.jsonld?wskey=ws&provider=historypin&annotationNr=161&indexing=true
	 * @param provider
	 * @param annotationNr
	 * @param europeanaLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createEuropeanaAnnotationLd(
			String provider, Long annotationNr, String europeanaLdStr) throws IOException {
		
		String url = getAnnotationServiceUri().replace("annotations",""); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
		url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider + WebAnnotationFields.AND;
		if (annotationNr != null)
			url += WebAnnotationFields.ANNOTATION_NR + WebAnnotationFields.EQUALS + annotationNr + WebAnnotationFields.AND;
		url += WebAnnotationFields.INDEXING + WebAnnotationFields.EQUALS + "true";

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithBody(url, europeanaLdStr);
		
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotation.jsonld");
		
		
//		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(json);
//		aor.setAnnotation(JsonUtils.toAnnotationObject(annotationJsonString));
//		aor.setAnnotation(JsonUtils.toAnnotationObject(json));
		aor.setJson(json);
		return aor;
		
//		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
//				AnnotationOperationResponse.class);
//		int jj = 4;
//		AnnotationSearchResults asr = getAnnotationSearchResults(json);
//		return null;
	}

	/**
	 * This method creates Annotation object from JsonLd string.
	 * @param annotationJsonLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createAnnotation(String annotationJsonLdStr) throws IOException {
//		String url = getAnnotationServiceUri();
//		url += JsonUtils.extractEuropeanaIdFromJsonLdStr(annotationJsonLdStr);
//		url += ModelConst.JSON_REST;

		String url = getAnnotationServiceUri().replace("annotations",""); // current annotation service uri is .../annotation-web/annotations
		url += WebAnnotationFields.ANNOTATION_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
		url += WebAnnotationFields.WSKEY + WebAnnotationFields.EQUALS + "ws" + WebAnnotationFields.AND;
//		url += WebAnnotationFields.PROVIDER + WebAnnotationFields.EQUALS + provider + WebAnnotationFields.AND;
//		if (annotationNr != null)
//			url += WebAnnotationFields.ANNOTATION_NR + WebAnnotationFields.EQUALS + annotationNr + WebAnnotationFields.AND;
//		url += WebAnnotationFields.INDEXING + WebAnnotationFields.EQUALS + "true";
		
		
		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResultWithBody(url, annotationJsonLdStr);		
//		String json = getJSONResult(url, ModelConst.ANNOTATION, annotationJsonLdStr);
		
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
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationSearchResults search(String query, String startOn, String limit) 
			throws IOException {
		String url = buildUrl(query, startOn, limit, ModelConst.ANNOTATION);
		
		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResult(url);
		
		return getAnnotationSearchResults(json);
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
	 * This method consturcts url dependent on search parameter.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @param type The type of the object. E.g. annotation or tag
	 * @return query
	 */
	private String buildUrl(String query, String startOn, String limit, String type) {
		String url = getAnnotationServiceUri();
		url += "/search?wsKey=" + getApiKey() + "&profile=" + type;
		if (StringUtils.isNotEmpty(query)) {
//			url += "&query=" + query;
			url += "&value=" + query;
			if (!query.contains("field"))
				url += "&field=" + "all";
			if (!query.contains("language"))
				url += "&language=en";
		}
		if (StringUtils.isNotEmpty(startOn))
			url += "&startOn=" + startOn;
		else
			url += "&startOn=0";
		if (StringUtils.isNotEmpty(limit))
			url += "&limit=" + limit;
		else
			url += "&limit=10";
		return url;
	}

	public AnnotationSearchResults search(String query) throws IOException {
		return search(query, null, null);
	}

	/**
	 * This method returns a list of Tag objects for the passed query.
     * Sample url: "http://localhost:8081/annotation-web/tags/search?value=Vlad&field=multilingual&startOn=0&limit=10&language=en";
	 * @param query The query string
	 * @return tag search results
	 * @throws IOException
	 */
	public TagSearchResults searchTags(String query, String startOn, String limit) throws IOException {
		
		String url = buildUrl(query, startOn, limit, ModelConst.TAG);
		url = url.replace("annotations", "tags");
		
		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResult(url);
		
		TagSearchResults tsr = new TagSearchResults();
		tsr.setSuccess("true");
		tsr.setAction("create:/tags/search");
		String tagListJsonString = JsonUtils.extractAnnotationListStringFromJsonString(json, "\":(.*?)}]");
		if (StringUtils.isNotEmpty(tagListJsonString)) {
	        if (!tagListJsonString.isEmpty()) {
	        	tagListJsonString = 
	        			tagListJsonString.substring(1, tagListJsonString.length() - 2); // remove braces
		        String[] arrValue = JsonLdParser.splitAnnotationListStringToArray(tagListJsonString);
		        List<TagResource> tagList = new ArrayList<TagResource>();
		        for (String tagJsonStringElem : arrValue) {
		        	if (!tagJsonStringElem.startsWith("{"))
		        		tagJsonStringElem = "{" + tagJsonStringElem;
		        	if (!tagJsonStringElem.endsWith("}"))
		        		tagJsonStringElem = tagJsonStringElem + "}";
		        	TagResource tagObject = toTagObject(tagJsonStringElem);
					tagList.add(tagObject);
		    	}
		        tsr.setItems(tagList);
	        }
		}
		return tsr;
	}

	/**
	 * This method converts json string into TagResource object.
	 * @param json
	 * @return tag object
	 */
	public TagResource toTagObject(String json) {
		BaseTagResource tag = new BaseTagResource();
		String id = JsonUtils.extractValueFromJsonString(WebAnnotationFields.ID, json);
		if (StringUtils.isNotEmpty(id))
			tag.setId(id);
		String label = JsonUtils.extractValueFromJsonString(WebAnnotationFields.LABEL, json);
		if (StringUtils.isNotEmpty(label))
			tag.setLabel(label);
		String httpUri = JsonUtils.extractValueFromJsonString(WebAnnotationFields.HTTP_URI, json);
		if (StringUtils.isNotEmpty(httpUri))
			tag.setHttpUri(httpUri);
		String value = JsonUtils.extractValueFromJsonString(WebAnnotationFields.VALUE, json);
		if (StringUtils.isNotEmpty(value))
			tag.setValue(value);
		String language = JsonUtils.extractValueFromJsonString(WebAnnotationFields.LANGUAGE, json);
		if (StringUtils.isNotEmpty(language))
			tag.setLanguage(language);
		return tag;
	}
	
	public TagSearchResults searchTags(String query) throws IOException {
	    return searchTags(query, null, null);
	}

	public AnnotationOperationResponse getAnnotation(
			String europeanaId, String provider, Integer annotationNr) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!europeanaId.startsWith(WebAnnotationFields.SLASH))
			url += WebAnnotationFields.SLASH ;
		
		url += europeanaId;
		url += WebAnnotationFields.SLASH + provider;
		url += WebAnnotationFields.SLASH + annotationNr;
		
		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		
		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
				AnnotationOperationResponse.class);

	}

	
}
