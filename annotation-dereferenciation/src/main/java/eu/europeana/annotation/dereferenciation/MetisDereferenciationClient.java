package eu.europeana.annotation.dereferenciation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.connection.HttpConnection;


/**
 * This class supports requests to Metis API and XSLT conversion of response.
 * 
 * @author GrafR
 *
 */
public class MetisDereferenciationClient {
	
	protected static final String XSLT_TRANSFORMATION_FILE = "/deref2json.xsl";
	
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
	 * @param language e.g. "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
	 * @return response from Metis API in JSON-LD format
	 * @throws IOException
	 */
	public Map<String,String> queryMetis(String baseUrl, List<String> uris, String language) throws IOException {
		Map<String,String> res = new HashMap<String,String>();
		for (String uri : uris) {
			String queryUri = baseUrl+URLEncoder.encode(uri,"UTF-8");;			
		    String xmlResponse = getHttpConnection().getURLContent(queryUri);
		    String jsonLdStr = convertDereferenceOutputToJsonLd(uri, xmlResponse, language).toString();
		    res.put(uri,jsonLdStr);
		}
		return res;
	}
	
	
	/**
	 * An XSLT converts dereference output to JSON-LD. 
	 * @param xmlStr
	 * @param language e.g. "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
	 * @return dereferenced output in JSON-LD format
	 */
	public StringWriter convertDereferenceOutputToJsonLd(String uri, String xmlStr, String language) {
		
		InputStream xsltFileAsStream = getClass().getResourceAsStream(XSLT_TRANSFORMATION_FILE);

		Transformer transformer;
		StringWriter result = new StringWriter();
		
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(xsltFileAsStream);
			transformer = factory.newTransformer(xslt);
			transformer.setParameter("uri",uri);
			transformer.setParameter("langs",language);
			InputStream inputStringStream = new ByteArrayInputStream(xmlStr.getBytes());
			Source text = new StreamSource(inputStringStream);
			transformer.transform(text,	new StreamResult(result));			
		} catch (TransformerConfigurationException e) {
			logger.error("Error "+e);
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the MetisDereferenciationClient convertDereferenceOutputToJsonLd method", e);
		} catch (TransformerException e) {
			logger.error("Error "+e);
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the MetisDereferenciationClient convertDereferenceOutputToJsonLd method", e);
		}
		
		return result;
	}
}
