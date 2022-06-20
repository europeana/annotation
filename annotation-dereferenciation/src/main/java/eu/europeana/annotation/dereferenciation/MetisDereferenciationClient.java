package eu.europeana.annotation.dereferenciation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.stanbol.commons.jsonld.JsonSerializer;
import eu.europeana.annotation.connection.HttpConnection;
import eu.europeana.annotation.definitions.exception.AnnotationDereferenciationException;
import eu.europeana.annotation.utils.config.AnnotationConfiguration;

/**
 * This class supports requests to Metis API and XSLT conversion of response.
 * 
 * @author GrafR
 *
 */
public class MetisDereferenciationClient {

    static final String XSLT_TRANSFORMATION_FILE = "/deref2json.xsl";
    static final String PARAM_URI = "uri";
    static final String PARAM_LANGS = "langs";
    @Resource
    private AnnotationConfiguration configuration;
    Transformer transformer;
    private HttpConnection httpConnection;

    public HttpConnection getHttpConnection() {
	if(httpConnection == null) {
	    if(getConfiguration() != null) {
		httpConnection = new HttpConnection(
			getConfiguration().getMetisConnectionRetries(), 
			getConfiguration().getMetisConnectionTimeout());
	    }else {
		httpConnection = new HttpConnection();
	    }
	}
	return httpConnection;
    }

    public void setHttpConnection(HttpConnection httpConnection) {
	this.httpConnection = httpConnection;
    }

    public MetisDereferenciationClient() {
	init();
    }

    protected void init() {
	try {
	    TransformerFactory factory = TransformerFactory.newInstance();
	    InputStream xslFileAsStream = getClass().getResourceAsStream(XSLT_TRANSFORMATION_FILE);
	    StreamSource xslSource = new StreamSource(xslFileAsStream);
	    Templates templates = factory.newTemplates(xslSource);
	    transformer = templates.newTransformer();
	} catch (TransformerConfigurationException e) {
	    throw new AnnotationDereferenciationException(
		    "Cannot instantiate XSLT Transformer",
		    e);
	}
    }

    protected Transformer getTransformer() {
        if(transformer == null) {
            init();
        }
	return transformer;
    }
    
    /**
     * This method encodes URLs for HTTP connection
     * 
     * @param url The input URL
     * @return encoded URL
     * @throws UnsupportedEncodingException
     */
    String encodeUrl(String url) throws UnsupportedEncodingException {
	return URLEncoder.encode(url, "UTF-8");
    }

    /**
     * This method applies the XSLT to the XML output for each of the URIs in the
     * list and fills the map with the URI and JSON string. It sends GET HTTP
     * request to dereference URI.
     * 
     * @param baseUrl  The Metis base URL.
     * @param uris     The list of query URIs. The URI is composed from the base URI
     *                 to Metis API completed with query URI from the entity.
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return response from Metis API in JSON-LD format
     * @throws AnnotationDereferenciationException
     */
    public synchronized Map<String, String> dereferenceOne(String baseUrl, String uri, String language) throws AnnotationDereferenciationException {
	Map<String, String> res = new HashMap<String, String>();
	String queryUri, xmlResponse, jsonLdStr;
	    
	try {
	    queryUri = baseUrl + URLEncoder.encode(uri, "UTF-8");		
	    xmlResponse = getHttpConnection().getURLContent(queryUri);
	    jsonLdStr = convertToJsonLd(uri, xmlResponse, language).toString();
	    res.put(uri, jsonLdStr);
	    
	} catch (IOException ex) {
	    throw new AnnotationDereferenciationException(ex);
	} catch (RuntimeException ex) {
	    throw new AnnotationDereferenciationException(ex);
	}
	return res;
    }

    /**
     * This method applies the XSLT to the XML output for each of the URIs in the
     * list and fills the map with the URI and JSON string. It sends GET HTTP
     * request to dereference URI.
     * 
     * @param baseUrl  The Metis base URL.
     * @param uris     The list of query URIs. The URI is composed from the base URI
     *                 to Metis API completed with query URI from the entity.
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return response from Metis API in JSON-LD format
     * @throws AnnotationDereferenciationException
     */
    public synchronized Map<String, String> dereferenceMany(String baseUrl, List<String> uris, String language) throws AnnotationDereferenciationException {
	Map<String, String> res = new HashMap<String, String>();
	String xmlResponse, jsonLdStr;
	try {
        	@SuppressWarnings({ "rawtypes", "unchecked" })
		String urisJson = JsonSerializer.toString((List)uris);
	        xmlResponse = getHttpConnection().postRequest(baseUrl, urisJson);
	        for (Object uri : uris) {
	            jsonLdStr = convertToJsonLd((String)uri, xmlResponse, language).toString();
        	    res.put((String)uri, jsonLdStr);
         	}
	}catch(IOException ex) {
	    throw new AnnotationDereferenciationException(ex);
	}catch(RuntimeException ex) {
	    throw new AnnotationDereferenciationException(ex);
	}
	return res;
    }

  
    /**
     * An XSLT converts dereference output to JSON-LD.
     * 
     * @param xmlStr
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return dereferenced output in JSON-LD format
     */
    public synchronized StringWriter convertToJsonLd(String uri, String xmlStr, String language) {

	StringWriter result = new StringWriter();
	try {
	    //reuse transformer but update params
	    getTransformer().clearParameters();
	    getTransformer().setParameter(PARAM_URI, uri);
	    if (language != null) {
		getTransformer().setParameter(PARAM_LANGS, language);
	    }
	    InputStream inputStringStream = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
	    Source text = new StreamSource(inputStringStream);
	    getTransformer().transform(text, new StreamResult(result));
	    
	} catch (TransformerConfigurationException e) {
	    throw new AnnotationDereferenciationException(
		    "Unexpected exception occured when invoking the MetisDereferenciationClient convertDereferenceOutputToJsonLd method",
		    e);
	} catch (TransformerException e) {
	    throw new AnnotationDereferenciationException(
		    "Exception occured when invoking the MetisDereferenciationClient convertDereferenceOutputToJsonLd method",
		    e);
	}
	return result;
    }

    AnnotationConfiguration getConfiguration() {
        return configuration;
    }
}
