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
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
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
	
	public AnnotationSearchResults getAnnotationsForObject(String collectionId,
			String objectHash) throws IOException {
		String url = getAnnotationServiceUri();
		url += "/" + collectionId + "/" + objectHash + ".json";
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
//		url += annotation.getAnnotationId().getResourceId();
		url += annotation.getTarget().getEuropeanaId();
		url += ModelConst.JSON_REST;
//		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String jsonPost = getAnnotationGson().toJson(annotation);
		String json = getJSONResult(url, ModelConst.ANNOTATION, jsonPost);
		
		//JSONObject jsonObj = (JSONObject) new JSONParser().parse(json);
//		AnnotationOperationResponse aor = new ObjectMapper().readValue(json, AnnotationOperationResponse.class);
		AnnotationOperationResponse aor = new AnnotationOperationResponse();
		aor.setSuccess("true");
		aor.setAction("create:/annotations/collection/object.json");
		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(json);
		annotationJsonString = annotationJsonString
				.replace("\"\\\"", "").replace("\\\"\"","").replace(":[",":\"[").replace("],","]\",");
		aor.setAnnotation(JsonUtils.toAnnotationObject(annotationJsonString));
		return aor;

//		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
//				AnnotationOperationResponse.class);
	}

	/**
	 * This method creates Annotation object from JsonLd string.
	 * @param annotationJsonLdStr The Annotation
	 * @return annotation operation response
	 * @throws IOException
	 */
	public AnnotationOperationResponse createAnnotation(String annotationJsonLdStr) throws IOException {
		String url = getAnnotationServiceUri();
		url += JsonUtils.extractEuropeanaIdFromJsonLdStr(annotationJsonLdStr);
		url += ModelConst.JSON_REST;

		/**
		 * Execute Europeana API request
		 */
		String json = getJSONResult(url, ModelConst.ANNOTATION, annotationJsonLdStr);
		
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
     *     wskey=key&profile=webtest&query=vlad&field=all&language=en&startOn=0&limit=&search=search	 
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
	private AnnotationSearchResults getAnnotationSearchResults(String json) {
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
			url += "&query=" + query;
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
			url += "&limit=";
		return url;
	}

	public AnnotationSearchResults search(String query) throws IOException {
		return search(query, null, null);
	}

	/**
	 * This method returns a list of Tag objects for the passed query.
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
		        List<SolrTag> tagList = new ArrayList<SolrTag>();
		        for (String tagJsonStringElem : arrValue) {
		        	if (!tagJsonStringElem.startsWith("{"))
		        		tagJsonStringElem = "{" + tagJsonStringElem;
		        	if (!tagJsonStringElem.endsWith("}"))
		        		tagJsonStringElem = tagJsonStringElem + "}";
		    		SolrTag tagObject = toTagObject(tagJsonStringElem);
					tagList.add(tagObject);
		    	}
		        tsr.setItems(tagList);
	        }
		}
		return tsr;
	}

	/**
	 * This method converts json string into SolrTag object.
	 * @param json
	 * @return tag object
	 */
	public SolrTag toTagObject(String json) {
		SolrTagImpl tag = new SolrTagImpl();
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

	public AnnotationOperationResponse getAnnotation(String europeanaId, Integer annotationNr) throws IOException {
		
		String url = getAnnotationServiceUri();
		if(!europeanaId.startsWith("/"))
			url += "/" ;
		
		url += europeanaId;
		url += "/" + annotationNr;
		
		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		
		return (AnnotationOperationResponse) getAnnotationGson().fromJson(json,
				AnnotationOperationResponse.class);

	}

	
}
