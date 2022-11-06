/*
 * HttpConnector.java - europeana4j
 * (C) 2011 Digibis S.L.
 */
package eu.europeana.annotation.connection;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * The class encapsulating simple HTTP access.
 *
 * @author Sergiu Gordea, Roman Graf
 */
public class HttpConnection {

    public static final int DEFAULT_CONNECTION_RETRIES = 3;
    public static final int DEFAULT_TIMEOUT_CONNECTION = 40000;
    private static final int STATUS_OK_START = 200;
    private static final int STATUS_OK_END = 299;
    private HttpClient httpClient = null;
    private int connectionRetries, connectionTimeout;
    
    public HttpConnection(int retries, int timeout) {
	if(retries > 0) {
	    this.connectionRetries = retries;
	}else {
	    this.connectionRetries = DEFAULT_CONNECTION_RETRIES;
	}
	if(timeout > 0) {
	    this.connectionTimeout = timeout;
	}else{
	    this.connectionTimeout = timeout;
	}
    }
    
    public HttpConnection() {
	this(DEFAULT_CONNECTION_RETRIES, DEFAULT_TIMEOUT_CONNECTION);
    }
    
    public InputStream getURLContent(String url) throws IOException {
        HttpClient client = this.getHttpClient(DEFAULT_CONNECTION_RETRIES, DEFAULT_TIMEOUT_CONNECTION);
        GetMethod get = new GetMethod(url);
        get.setRequestHeader("Accept", "application/xml");

        client.executeMethod(get);
        //ignore metis response if dereferencing fails
        if (get.getStatusCode() >= STATUS_OK_START && get.getStatusCode() <= STATUS_OK_END) {
            return get.getResponseBodyAsStream();
        } else {
            return null;
        }
    }
    
    public InputStream postRequest(String url, String body) throws IOException {
        HttpClient client = this.getHttpClient(DEFAULT_CONNECTION_RETRIES, DEFAULT_TIMEOUT_CONNECTION);
        PostMethod post = new PostMethod(url);
        post.setRequestHeader("Accept", "application/xml");
        StringRequestEntity requestBody = new StringRequestEntity(body, "application/json;charset=UTF-8", null);
        post.setRequestEntity(requestBody);

        client.executeMethod(post);
        //ignore metis response if dereferencing fails
        if (post.getStatusCode() >= STATUS_OK_START && post.getStatusCode() <= STATUS_OK_END) {
            return post.getResponseBodyAsStream();
        } else {
            return null;
        }
    }
   
    /**
     * Perform HTTP request
     * @param connectionRetry
     * @param conectionTimeout
     * @return HTTP response
     */
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

    public int getConnectionRetries() {
        return connectionRetries;
    }

    public void setConnectionRetries(int connectionRetries) {
        this.connectionRetries = connectionRetries;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
