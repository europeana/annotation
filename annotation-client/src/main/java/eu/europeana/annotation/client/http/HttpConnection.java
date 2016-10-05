/*
 * HttpConnector.java - europeana4j
 * (C) 2011 Digibis S.L.
 */
package eu.europeana.annotation.client.http;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The class encapsulating simple HTTP access.
 *
 * @author Sergiu Gordea
 */
public class HttpConnection {

    private static final int CONNECTION_RETRIES = 3;
    private static final int TIMEOUT_CONNECTION = 40000;
    private static final int STATUS_OK_START = 200;
    private static final int STATUS_OK_END = 299;
    private static final String ENCODING = "UTF-8";
    private HttpClient httpClient = null;

    public String getURLContent(String url) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        GetMethod get = new GetMethod(url);

        try {
            client.executeMethod(get);

            if (get.getStatusCode() >= STATUS_OK_START && get.getStatusCode() <= STATUS_OK_END) {
                byte[] byteResponse = get.getResponseBody();
                String res = new String(byteResponse, ENCODING);
                return res;
            } else {
                return null;
            }

        } finally {
            get.releaseConnection();
        }
    }

    public String getURLContent(String url, String jsonParamName, String jsonParamValue) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        PostMethod post = new PostMethod(url);
        post.setParameter(jsonParamName, jsonParamValue);

        try {
            client.executeMethod(post);

            if (post.getStatusCode() >= STATUS_OK_START && post.getStatusCode() <= STATUS_OK_END) {
                byte[] byteResponse = post.getResponseBody();
                String res = new String(byteResponse, ENCODING);
                return res;
            } else {
                return null;
            }

        } finally {
        	post.releaseConnection();
        }
    }

    
	/**
	 * This method makes POST request for given URL and JSON body parameter.
	 * @param url
	 * @param jsonParamValue
	 * @return ResponseEntity that comprises response body in JSON format, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> postURL(String url, String jsonParamValue) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        PostMethod post = new PostMethod(url);
        post.setRequestBody(jsonParamValue);

        try {
            client.executeMethod(post);
   			return buildResponseEntity(post);
        } finally {
        	post.releaseConnection();
        }
    }

	
	/**
	 * This method makes PUT request for given URL and JSON body parameter.
	 * @param url
	 * @param jsonParamValue
	 * @return ResponseEntity that comprises response body in JSON format, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> putURL(String url, String jsonParamValue) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        PutMethod put = new PutMethod(url);
        put.setRequestBody(jsonParamValue);

        try {
            client.executeMethod(put);
   			return buildResponseEntity(put);
        } finally {
        	put.releaseConnection();
        }
    }

	
	/**
	 * This method makes DELETE request for given identifier URL.
	 * @param url The identifier URL
	 * @return ResponseEntity that comprises response headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String> deleteURL(String url) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        DeleteMethod delete = new DeleteMethod(url);

        try {
            client.executeMethod(delete);
   			return buildResponseEntity(delete);
        } finally {
        	delete.releaseConnection();
        }
    }

	
	/**
	 * This method builds a response entity that comprises 
	 * response body, headers and status code for the passed
	 * HTTP method
	 * @param method The HTTP method (e.g. post, put, delete or get)
	 * @return response entity
	 * @throws IOException 
	 */
	private ResponseEntity<String> buildResponseEntity(HttpMethod method) throws IOException {
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(15);
		for (Header header : method.getResponseHeaders())
			  headers.add(header.getName(), header.getValue());

		if (method.getStatusCode() >= STATUS_OK_START && method.getStatusCode() <= STATUS_OK_END) {
			String res = "";
			if (method.getResponseBody() != null && method.getResponseBody().length > 0) {
	            byte[] byteResponse = method.getResponseBody();
	            res = new String(byteResponse, ENCODING);
			}
    		ResponseEntity<String> responseEntity = new ResponseEntity<String>(
    				res
    				, headers
    				, HttpStatus.valueOf(method.getStatusCode())
    				);
    		return responseEntity;
        } else {
            return new ResponseEntity<String>(headers, HttpStatus.valueOf(method.getStatusCode()));
        }		
	}
    
    
	/**
	 * This method makes GET request for given URL.
	 * @param url
	 * @return ResponseEntity that comprises response body in JSON format, headers and status code.
	 * @throws IOException
	 */
	public ResponseEntity<String>  getURL(String url) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        GetMethod get = new GetMethod(url);

        try {
            client.executeMethod(get);
   			return buildResponseEntity(get);
        } finally {
            get.releaseConnection();
        }
    }
    
    
	public String getURLContentWithBody(String url, String jsonParamValue) throws IOException {
        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
        PostMethod post = new PostMethod(url);
        post.setRequestBody(jsonParamValue);

        try {
            client.executeMethod(post);

            if (post.getStatusCode() >= STATUS_OK_START && post.getStatusCode() <= STATUS_OK_END) {
                byte[] byteResponse = post.getResponseBody();
                String res = new String(byteResponse, ENCODING);
                return res;
            } else {
                return null;
            }

        } finally {
        	post.releaseConnection();
        }
    }
    
    
//    public boolean writeURLContent(String url, OutputStream out) throws IOException {
//        return writeURLContent(url, out, null);
//    }
//
//    public boolean writeURLContent(String url, OutputStream out, String requiredMime) throws IOException {
//        HttpClient client = this.getHttpClient(CONNECTION_RETRIES, TIMEOUT_CONNECTION);
//        GetMethod getMethod = new GetMethod(url);
//        try {
//            client.executeMethod(getMethod);
//
//            Header tipoMimeHead = getMethod.getResponseHeader("Content-Type");
//            String tipoMimeResp = "";
//            if (tipoMimeHead != null) {
//                tipoMimeResp = tipoMimeHead.getValue();
//            }
//
//            if (getMethod.getStatusCode() >= STATUS_OK_START && getMethod.getStatusCode() <= STATUS_OK_END
//                    && ((requiredMime == null) || ((tipoMimeResp != null) && tipoMimeResp.contains(requiredMime)))) {
//                InputStream in = getMethod.getResponseBodyAsStream();
//
//                // Copy input stream to output stream
//                byte[] b = new byte[4 * 1024];
//                int read;
//                while ((read = in.read(b)) != -1) {
//                    out.write(b, 0, read);
//                }
//
//                getMethod.releaseConnection();
//                return true;
//            } else {
//                return false;
//            }
//
//        } finally {
//            getMethod.releaseConnection();
//        }
//    }

   
    private HttpClient getHttpClient(int connectionRetry, int conectionTimeout) {
        if (this.httpClient == null) {
            HttpClient client = new HttpClient();
            
            //configure retry handler
            client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(connectionRetry, false));

            //when using a http proxy
            String proxyHost = System.getProperty("http.proxyHost");
            if ((proxyHost != null) && (proxyHost.length() > 0)) {
                String proxyPortSrt = System.getProperty("http.proxyPort");
                if (proxyPortSrt == null) {
                    proxyPortSrt = "8080";
                }
                int proxyPort = Integer.parseInt(proxyPortSrt);

                client.getHostConfiguration().setProxy(proxyHost, proxyPort);
            }

            //configure timeouts
            boolean bTimeout = false;
            String connectTimeOut = System.getProperty("sun.net.client.defaultConnectTimeout");
            if ((connectTimeOut != null) && (connectTimeOut.length() > 0)) {
                client.getParams().setIntParameter("sun.net.client.defaultConnectTimeout", Integer.parseInt(connectTimeOut));
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
