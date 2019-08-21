package eu.europeana.annotation.client.connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

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
	Logger logger = LogManager.getLogger(getClass().getName());

	private Gson gson;

	public String getApiKey() {
		return apiKey;
	}

	public String getAdminApiKey() {
		//TODO create properties entry 
		return "apiadmin";
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
	
	public Gson getAnnotationGson() {
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
	
	String getJSONResultWithHeader(String url, String headerName, String headerValue) throws IOException {
		logger.trace("Call to Annotation API (GET): " + url);
		return getHttpConnection().getURLContentWithHeader(url, headerName, headerValue);
	}
	
	String getJSONResult(String url, String paramName, String jsonPost) throws IOException {
		logger.trace("Call to Annotation API (POST): " + url);
		return getHttpConnection().getURLContent(url, paramName, jsonPost);
	}
	
	String getJSONResultWithBody(String url, String jsonPost) throws IOException {
		logger.trace("Call to Annotation API (POST) with body: " + url);
		return getHttpConnection().getURLContentWithBody(url, jsonPost);
	}
	
	String getJSONResultWithBodyAndHeader(String url, String jsonPost, String headerName, String headerValue) throws IOException {
		logger.trace("Call to Annotation API (POST) with body: " + url);
		return getHttpConnection().getURLContentWithBody(url, jsonPost, headerName, headerValue);
	}
	
	
	/**
	 * This method makes POST request for given URL and JSON body parameter that returns
	 * response body, response headers and status code.
	 * @param url
	 * @param jsonPost
	 * @return The response body, response headers and status code.
	 * @throws IOException
	 */
	ResponseEntity<String> postURL(String url, String jsonPost) throws IOException {
		logger.trace("Call to Annotation API (POST) with body: " + url + 
				". Returns body, headers and status code.");
		//System.out.println("post: " + url);
		return getHttpConnection().postURL(url, jsonPost);
	}
	
	
	/**
	 * This method makes PUT request for given URL and JSON body parameter that returns
	 * response body, response headers and status code.
	 * @param url
	 * @param jsonPost
	 * @return The response body, response headers and status code.
	 * @throws IOException
	 */
	ResponseEntity<String> putURL(String url, String jsonPut) throws IOException {
		logger.trace("Call to Annotation API (PUT) with body: " + url + 
				". Returns body, headers and status code.");
		
		ResponseEntity<String> response = getHttpConnection().putURL(url, jsonPut);
		
		response.getStatusCode();
		
		return response;
	}
	

	/**
	 * This method makes GET request for given URL and returns
	 * response body, response headers and status code.
	 * @param url
	 * @return The response body, response headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> getURL(String url) throws IOException {
		logger.trace("Call to Annotation API (GET): " + url + 
				". Returns body, headers and status code.");
		return getHttpConnection().getURL(url);
	}
	
	
	/**
	 * This method makes DELETE request for given URL that returns
	 * response headers and status code.
	 * @param url
	 * @return The response headers and status code.
	 * @throws IOException
	 */
	ResponseEntity<String> deleteURL(String url) throws IOException {
		logger.trace("Call to Annotation API (DELETE): " + url + 
				". Returns headers and status code.");
		return getHttpConnection().deleteURL(url);
	}
	

	/**
	 * This method encodes URLs for HTTP connection
	 * @param url The input URL
	 * @return encoded URL
	 * @throws UnsupportedEncodingException
	 */
	String encodeUrl(String url) throws UnsupportedEncodingException {
		return URLEncoder.encode(url,"UTF-8");
	}
}
