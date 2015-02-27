package eu.europeana.annotation.client.connection;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.europeana.annotation.client.http.HttpConnection;
import eu.europeana.annotation.client.model.json.AnnotationDeserializer;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.utils.ModelConst;

public class BaseApiConnection {

	private String apiKey;
	// private String annotationServiceUri =
	// "http://www.europeana.eu/api/v2/search.json";
	private String annotationServiceUri = "";
	private HttpConnection httpConnection = new HttpConnection();
	Logger logger = Logger.getLogger(getClass().getName());

	private Gson gson;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAnnotationServiceUri() {
		return annotationServiceUri;
	}

	public void setAnnotationServiceUri(String annotationServiceUri) {
		this.annotationServiceUri = annotationServiceUri;
	}

	public HttpConnection getHttpConnection() {
		return httpConnection;
	}

	public void setHttpConnection(HttpConnection httpConnection) {
		this.httpConnection = httpConnection;
	}
	
	Gson getAnnotationGson() {
		if (gson == null) {
			// Load results object from JSON
			GsonBuilder builder = new GsonBuilder();
			AnnotationDeserializer annoDeserializer = new AnnotationDeserializer();
			
			builder.registerTypeHierarchyAdapter(Annotation.class,
					annoDeserializer);
			
//			builder.registerTypeAdapter( Point.class,
//					annoDeserializer);
			
			//builder.registerTypeAdapterFactory();
			// builder.registerTypeAdapterFactory(factory)
			// AnnotationDeserializer annoDeserializer = new
			// AnnotationDeserializer();
			// builder.registerTypeAdapter(Annotation.class, annoDeserializer);
//			.setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
//			gson = builder.create();
			gson = builder.setDateFormat(ModelConst.GSON_DATE_FORMAT).create();
//			gson = builder.setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
//			gson = builder.setDateFormat("yyyy-MM-dd hh:mm:ss.S").create(); yyyy-MM-dd'T'HH:mm:ss.SSSZ
//			gson = builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create(); 
		}
		return gson;
	}

	/**
	 * Create a new connection to the Annotation Service (REST API).
	 * 
	 * @param apiKey
	 *            API Key required to access the API
	 */
	public BaseApiConnection(String annotationServiceUri, String apiKey) {
		this.apiKey = apiKey;
		this.annotationServiceUri = annotationServiceUri;
	}

	
	String getJSONResult(String url) throws IOException {
		logger.trace("Call to Annotation API (GET): " + url);
		return getHttpConnection().getURLContent(url);
	}
	
	String getJSONResult(String url, String paramName, String jsonPost) throws IOException {
		logger.trace("Call to Annotation API (POST): " + url);
		return getHttpConnection(). getURLContent(url, paramName, jsonPost);
	}
}
