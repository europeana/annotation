package eu.europeana.annotation.dereferenciation;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.stanbol.commons.jsonld.JsonSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.connection.HttpConnection;
import eu.europeana.annotation.definitions.exception.AnnotationDereferenciationException;

/**
 * This class supports requests to Metis API and XSLT conversion of response.
 * 
 * @author GrafR
 *
 */
@Service(AnnotationConfiguration.BEAN_METIS_DEREFERENCE_CLIENT)
@PropertySource(
    value = {"classpath:config/annotation.properties", "classpath:config/annotation.user.properties"},
    ignoreResourceNotFound = true)
public class MetisDereferenciationClient implements InitializingBean {
    @Value("${metis.baseUrl}")
    private String baseUrl;  
    @Value("${metis.connection.retries:3}")
    private int connRetries;
    @Value("${metis.connection.timeout:30000}")
    private int connTimeout;

    static final String XSLT_TRANSFORMATION_FILE = "/deref2json.xsl";
    static final String PARAM_URI = "uri";
    static final String PARAM_LANGS = "langs";
    Transformer transformer;
    private HttpConnection httpConnection;

    public HttpConnection getHttpConnection() {
      if(httpConnection == null) {
    	httpConnection = new HttpConnection(connRetries,connTimeout);
      }
      return httpConnection;
    }

    public void setHttpConnection(HttpConnection httpConnection) {
	this.httpConnection = httpConnection;
    }

    public void afterPropertiesSet() {
	try {
	    TransformerFactory factory = TransformerFactory.newInstance();
	    InputStream xslFileAsStream = new ClassPathResource(
	        XSLT_TRANSFORMATION_FILE).getInputStream();
	    StreamSource xslSource = new StreamSource(xslFileAsStream);
	    Templates templates = factory.newTemplates(xslSource);
	    transformer = templates.newTransformer();
	} catch (TransformerConfigurationException | IOException e) {
	    throw new AnnotationDereferenciationException(
		    "Cannot instantiate XSLT Transformer",
		    e);
	}
    }

    protected Transformer getTransformer() {
        if(transformer == null) {
          afterPropertiesSet();
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
	return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    /**
     * This method applies the XSLT to the XML output for each of the URIs in the
     * list and fills the map with the URI and JSON string. It sends GET HTTP
     * request to dereference URI.
     * 
     * @param uris     The list of query URIs. The URI is composed from the base URI
     *                 to Metis API completed with query URI from the entity.
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return response from Metis API in JSON-LD format
     * @throws AnnotationDereferenciationException
     */
    public synchronized Map<String, String> dereferenceOne(String uri, String language) throws AnnotationDereferenciationException {
	Map<String, String> res = new HashMap<String, String>();
	String jsonLdStr;
	InputStream streamResponse;
	    
	try {
	    UriBuilder uriBuilder = UriBuilder.fromPath(baseUrl).queryParam("uri", uri);
        streamResponse = getHttpConnection().getURLContent(uriBuilder.build().toString());
	    jsonLdStr = convertToJsonLd(uri, streamResponse, language).toString();
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
     * @param uris     The list of query URIs. The URI is composed from the base URI
     *                 to Metis API completed with query URI from the entity.
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return response from Metis API in JSON-LD format
     * @throws AnnotationDereferenciationException
     */
    public synchronized Map<String, String> dereferenceMany(List<String> uris, String language) throws AnnotationDereferenciationException {
	Map<String, String> res = new HashMap<String, String>();
	String jsonLdStr;
	InputStream streamResponse;
	try {
      	@SuppressWarnings({ "rawtypes", "unchecked" })
      	String urisJson = JsonSerializer.toString((List)uris);
      	streamResponse = getHttpConnection().postRequest(baseUrl, urisJson);
      	//the commented code below can be used to save the metis response for the mock in the integration tests (btw import the apache commons-io maven library)
//      	StringWriter writer = new StringWriter();
//      	IOUtils.copy(streamResponse, writer, StandardCharsets.UTF_8);
//      	System.out.println(writer.toString());
      	String[] urisArray = new String[uris.size()];
        urisArray = uris.toArray(urisArray);
        jsonLdStr = convertToJsonLd(urisArray, streamResponse, language).toString();
        //we need to separate the individual jsons manually
        List<Integer> startingPositions = findSubstringIndexes(jsonLdStr, "{ \"@context\":");
        for (int i = 0; i < startingPositions.size(); i++) {
          if(i==startingPositions.size()-1) {
            res.put(uris.get(i), jsonLdStr.substring(startingPositions.get(i), jsonLdStr.length()));
          }
          else {
            res.put(uris.get(i), jsonLdStr.substring(startingPositions.get(i), startingPositions.get(i+1)));
          }
        }          
	}catch(IOException ex) {
	    throw new AnnotationDereferenciationException(ex);
	}catch(RuntimeException ex) {
	    throw new AnnotationDereferenciationException(ex);
	}
	return res;
    }
    
    /**
     * TODO: move this function to utils
     * This method finds all indexes of a substring within a string, e.g. for the substring "abc" 
     * within a string "abcdefabc abc dejjabc", the output will be [0,6,10,18].
     * @param input
     * @param substring
     * @return
     */
    private List<Integer> findSubstringIndexes(String input, String substring) {
      List<Integer> indexes = new ArrayList<Integer>();
      int substringLength = substring.length();
      int index = input.indexOf(substring, 0);
      if(index!=-1) {
        indexes.add(index);
        while(index != -1){
          index = input.indexOf(substring, index + substringLength);
          if (index != -1) {
              indexes.add(index);
          }
        }
      }      
      return indexes;
    }
  
    /**
     * An XSLT converts dereference output to JSON-LD.
     * 
     * @param response
     * @param language e.g.
     *                 "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return dereferenced output in JSON-LD format
     */
    public synchronized StringWriter convertToJsonLd(Object uris, InputStream response, String language) {

	StringWriter result = new StringWriter();
	try {
	    //reuse transformer but update params
	    getTransformer().clearParameters();
	    getTransformer().setParameter(PARAM_URI, uris);
	    if (language != null) {
		getTransformer().setParameter(PARAM_LANGS, language);
	    }
	    StreamSource text = new StreamSource(response);
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

    public String getBaseUrl() {
      return baseUrl;
    }

    public int getConnRetries() {
      return connRetries;
    }

    public int getConnTimeout() {
      return connTimeout;
    }
}
