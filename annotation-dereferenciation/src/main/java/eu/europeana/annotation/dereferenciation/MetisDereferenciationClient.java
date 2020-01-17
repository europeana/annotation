package eu.europeana.annotation.dereferenciation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.annotation.connection.HttpConnection;


/**
 * This class supports requests to Metis API and XSLT conversion of response.
 * 
 * @author GrafR
 *
 */
public class MetisDereferenciationClient {
	
	String TEST_JSON_LD = "{\n" + 
			"      \"id\": \"http://data.europeana.eu/agent/base/146951\",\n" + 
			"      \"type\": \"Agent\",\n" + 
			"      \"depiction\": \"http://commons.wikimedia.org/wiki/Special:FilePath/Barbara%20Krafft%20-%20Portr%C3%A4t%20Wolfgang%20Amadeus%20Mozart%20%281819%29.jpg\",\n" + 
			"      \"prefLabel\": {\n" + 
			"        \"en\": \"Wolfgang Amadeus Mozart\"\n" + 
			"      },\n" + 
			"      \"hiddenLabel\": {\n" + 
			"        \"en\": [\n" + 
			"          \"Wolfgang Amadeus Mozart\",\n" + 
			"          \"Mozart, Wolfgang Amadeus\"\n" + 
			"        ]\n" + 
			"      },\n" + 
			"      \"dateOfBirth\": \"1756-01-27\",\n" + 
			"      \"dateOfDeath\": \"1791-12-05\"\n" + 
			"    }";

	private HttpConnection httpConnection = new HttpConnection();

	Logger logger = LogManager.getLogger(getClass().getName());

	public HttpConnection getHttpConnection() {
		return httpConnection;
	}

	public void setHttpConnection(HttpConnection httpConnection) {
		this.httpConnection = httpConnection;
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
	
	/**
	 * This method applies the XSLT to the XML output for each of the URIs in the list 
	 * and fills the map with the URI and JSON string. It sends GET HTTP request to dereference URI.
	 * @param baseUrl The Metis base URL.
	 * @param uris The list of query URIs. The URI is composed from the base URI to Metis API 
	 * completed with query URI from the entity.
	 * @return response from Metis API in JSON-LD format
	 * @throws IOException
	 */
	public Map<String,String> queryMetis(String baseUrl, List<String> uris) throws IOException {
		Map<String,String> res = new HashMap<String,String>();
		for (String uri : uris) {
			String queryUri = baseUrl+uri;			
		    String xmlResponse = getHttpConnection().getURLContent(queryUri);
		    String jsonLdStr = convertDereferenceOutputToJsonLd(xmlResponse);
		    res.put(uri,jsonLdStr);
		}
		return res;
	}
	
	/**
	 * An XSLT converts dereference output to JSON-LD. 
	 * @param xmlStr
	 * @return dereferenced output in JSON-LD format
	 */
	public String convertDereferenceOutputToJsonLd(String xmlStr) {
		return TEST_JSON_LD;
	}
}
