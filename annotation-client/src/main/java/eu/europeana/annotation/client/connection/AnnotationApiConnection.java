package eu.europeana.annotation.client.connection;

import java.io.IOException;

import com.google.gson.Gson;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.definitions.model.Annotation;
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
	
	public AnnotationSearchResults getAnnotationsForObject(String collectionId,
			String objectHash) throws IOException {
		String url = getAnnotationServiceUri();
		url += collectionId + "/" + objectHash;
		url += "?wsKey=" + getApiKey() + "&profile=annotation";

		// Execute Europeana API request
		String json = getJSONResult(url);
		Gson gson = getAnnotationGson();

		return (AnnotationSearchResults) gson.fromJson(json,
				AnnotationSearchResults.class);
	}
	
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
