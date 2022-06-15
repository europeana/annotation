/*
 * HttpConnector.java - europeana4j
 * (C) 2011 Digibis S.L.
 */
package eu.europeana.annotation.tests.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The class encapsulating simple HTTP access.
 *
 * @author GrafR
 */
public class HttpConnection {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    
    private static final int CONNECTION_RETRIES = 3;
    private static final int TIMEOUT_CONNECTION = 40000;
    private static final int STATUS_OK_START = 200;
    private static final int STATUS_OK_END = 299;
    private HttpClient httpClient = null;

    /**
     * This method makes POST request for given URL and JSON body parameter.
     *
     * @param url
     * @param requestBody
     * @param contentType
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    public ResponseEntity<String> post(String url, String requestBody, String contentType) throws IOException {
	PostMethod post = buildPostMethod(url, requestBody, HttpHeaders.CONTENT_TYPE, contentType);
	return fetchHttpMethodResponse(post);
    }

    /**
     * This method makes POST request for given URL and JSON body parameter with
     * header.
     * 
     * @param url
     * @param body
     * @param requestHeaderName
     * @param authorizationHeaderValue
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    public ResponseEntity<String> postURL(String url, String body, String authorizationHeaderValue) throws IOException {
	PostMethod post = buildPostMethod(url, body, HEADER_AUTHORIZATION,
		authorizationHeaderValue);
	return fetchHttpMethodResponse(post);
    }

    @SuppressWarnings("deprecation")
    private PostMethod buildPostMethod(String url, String body, String headerName, String headerValue) {
	PostMethod post = new PostMethod(url);
	if (StringUtils.isNotBlank(headerValue)) {
	    post.setRequestHeader(headerName, headerValue);
	}
	post.setRequestBody(body);
	return post;
    }

    /**
     * This method makes PUT request for given URL and JSON body parameter.
     *
     * @param url
     * @param jsonParamValue
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    public ResponseEntity<String> putURL(String url, String jsonParamValue) throws IOException {
	return putURL(url, jsonParamValue, null);
    }

    /**
     * This method makes PUT request for given URL and JSON body parameter.
     * 
     * @param url
     * @param jsonParamValue
     * @param requestHeaderName
     * @param authorizationHeaderValue
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public ResponseEntity<String> putURL(String url, String jsonParamValue, String authorizationHeaderValue)
	    throws IOException {
	PutMethod put = new PutMethod(url);
	if (StringUtils.isNotBlank(authorizationHeaderValue)) {
	    put.setRequestHeader(HEADER_AUTHORIZATION, authorizationHeaderValue);
	}
	put.setRequestBody(jsonParamValue);

	return fetchHttpMethodResponse(put);
    }

    /**
     * This method makes DELETE request for given identifier URL.
     *
     * @param url The identifier URL
     * @return ResponseEntity that comprises response headers and status code.
     * @throws IOException
     */
    public ResponseEntity<String> deleteURL(String url) throws IOException {
	return deleteURL(url, null);
    }

    /**
     * This method makes DELETE request for given identifier URL.
     * 
     * @param url                       The identifier URL
     * @param requestHeaderName
     * @param authorizationtHeaderValue
     * @return ResponseEntity that comprises response headers and status code.
     * @throws IOException
     */
    public ResponseEntity<String> deleteURL(String url, String authorizationtHeaderValue) throws IOException {
	DeleteMethod delete = new DeleteMethod(url);
	if (StringUtils.isNotBlank(authorizationtHeaderValue)) {
	    delete.setRequestHeader(HEADER_AUTHORIZATION, authorizationtHeaderValue);
	}

	return fetchHttpMethodResponse(delete);
    }

    /**
     * This method builds a response entity that comprises response body, headers
     * and status code for the passed HTTP method
     *
     * @param method The HTTP method (e.g. post, put, delete or get)
     * @return response entity
     * @throws IOException
     */
    private ResponseEntity<String> buildResponseEntity(HttpMethod method) throws IOException {

	MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(15);
	for (Header header : method.getResponseHeaders())
	    headers.add(header.getName(), header.getValue());

	String res = null;
	if (method.getResponseBody() != null && method.getResponseBody().length > 0) {
	    byte[] byteResponse = method.getResponseBody();
	    res = new String(byteResponse, StandardCharsets.UTF_8);
	}
	return new ResponseEntity<>(res, headers, HttpStatus.valueOf(method.getStatusCode()));
    }

    /**
     * This method makes GET request for given URL.
     *
     * @param url
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    public ResponseEntity<String> getURL(String url) throws IOException {
	return getURL(url, null);
    }

    /**
     * This method makes GET request for given URL.
     * 
     * @param url
     * @param requestHeaderName
     * @param authorizationHeaderValue
     * @return ResponseEntity that comprises response body in JSON format, headers
     *         and status code.
     * @throws IOException
     */
    public ResponseEntity<String> getURL(String url, String authorizationHeaderValue) throws IOException {
	GetMethod get = new GetMethod(url);
	if (StringUtils.isNotBlank(authorizationHeaderValue)) {
	    get.setRequestHeader(HEADER_AUTHORIZATION, authorizationHeaderValue);
	}

	return fetchHttpMethodResponse(get);
    }

    private ResponseEntity<String> fetchHttpMethodResponse(HttpMethod http) throws IOException{
	HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
	try {
	    client.executeMethod(http);
	    return buildResponseEntity(http);
	} finally {
	    http.releaseConnection();
	}
    }

    @SuppressWarnings("deprecation")
    public String getURLContentWithBody(String url, String jsonParamValue) throws IOException {
	PostMethod post = new PostMethod(url);
	post.setRequestHeader("Content-Type", "application/json");
	post.setRequestBody(jsonParamValue);

	return callHttpMethod(post);
    }
    
    public String getJsonResponse(String url) throws IOException {
        GetMethod get = new GetMethod(url);
        get.setRequestHeader("Accept", "application/json");
        return callHttpMethod(get);
    }
    
    private String callHttpMethod(HttpMethod httpMethod) throws IOException {
	HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
	try {
	    client.executeMethod(httpMethod);

	    if (httpMethod.getStatusCode() >= STATUS_OK_START && httpMethod.getStatusCode() <= STATUS_OK_END) {
		InputStream responseBodyAsStream = httpMethod.getResponseBodyAsStream();
		return IOUtils.toString(responseBodyAsStream, StandardCharsets.UTF_8);
	    } else {
		return null;
	    }

	} finally {
	    httpMethod.releaseConnection();
	}
    }

    private HttpClient getHttpClient(int connectionRetry, int conectionTimeout) {
	if (this.httpClient == null) {
	    HttpClient client = new HttpClient();

	    // configure retry handler
	    client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		    new DefaultHttpMethodRetryHandler(connectionRetry, false));

	    // when using a http proxy
	    String proxyHost = System.getProperty("http.proxyHost");
	    if ((proxyHost != null) && (proxyHost.length() > 0)) {
		String proxyPortSrt = System.getProperty("http.proxyPort");
		if (proxyPortSrt == null) {
		    proxyPortSrt = "8080";
		}
		int proxyPort = Integer.parseInt(proxyPortSrt);

		client.getHostConfiguration().setProxy(proxyHost, proxyPort);
	    }

	    // configure timeouts
	    boolean bTimeout = false;
	    String connectTimeOut = System.getProperty("sun.net.client.defaultConnectTimeout");
	    if ((connectTimeOut != null) && (connectTimeOut.length() > 0)) {
		client.getParams().setIntParameter("sun.net.client.defaultConnectTimeout",
			Integer.parseInt(connectTimeOut));
		bTimeout = true;
	    }
	    String readTimeOut = System.getProperty("sun.net.client.defaultReadTimeout");
	    if ((readTimeOut != null) && (readTimeOut.length() > 0)) {
		client.getParams().setIntParameter("sun.net.client.defaultReadTimeout", Integer.parseInt(readTimeOut));
		bTimeout = true;
	    }
	    if (!bTimeout) {
		client.getParams().setIntParameter(HttpMethodParams.SO_TIMEOUT, conectionTimeout);
	    }

	    this.httpClient = client;
	}
	return this.httpClient;
    }
}
