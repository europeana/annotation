package eu.europeana.annotation.dereferenciation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.apache.logging.log4j.core.util.IOUtils;

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
	
	String TEST_JSON_LD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<metis:results xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:edm=\"http://www.europeana.eu/schemas/edm/\" xmlns:rdaGr2=\"http://RDVocab.info/ElementsGr2/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema\" xmlns:cc=\"http://creativecommons.org/ns\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:wgs84_pos=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:metis=\"http://www.europeana.eu/schemas/metis\">\n" + 
			"	<metis:enrichmentBaseWrapperList>\n" + 
			"		<edm:Agent rdf:about=\"http://viaf.org/viaf/51961439\">\n" + 
			"			<skos:prefLabel xml:lang=\"pt-PT\">Vermeer op Delft</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"he-IL\">יוהנס ורמיר</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"it-IT\">Jan Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"en-KR\">Jan Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"fr-FR\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"no-NO\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"es-ES\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"en-US\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"ja-JP\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"nl-NL\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"sv-SE\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"it-VA\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"lv-LV\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"en-IL\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"en-AU\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"ca-ES\">Johannes Vermeer</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"fr-CH\">Jan Vermeer van Delft</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"de-DE\">Jan Vermeer van Delft</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"hr-HR\">Jan Vermeer van Delft</skos:prefLabel>\n" + 
			"			<skos:prefLabel xml:lang=\"cs-CZ\">Jan Vermeer van Delft</skos:prefLabel>\n" + 
			"			<rdaGr2:dateOfBirth>1632-10-31</rdaGr2:dateOfBirth>\n" + 
			"			<rdaGr2:dateOfDeath>1675-12-15</rdaGr2:dateOfDeath>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"			<owl:sameAs/>\n" + 
			"		</edm:Agent>\n" + 
			"	</metis:enrichmentBaseWrapperList>\n" + 
			"</metis:results>";
	
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
			String queryUri = baseUrl+"?uri="+URLEncoder.encode(uri,"UTF-8");;			
		    String xmlResponse = getHttpConnection().getURLContent(queryUri);
		    String jsonLdStr = convertDereferenceOutputToJsonLd(uri, xmlResponse).toString();
		    res.put(uri,jsonLdStr);
		}
		return res;
	}
	
	
	/**
	 * An XSLT converts dereference output to JSON-LD. 
	 * @param xmlStr
	 * @return dereferenced output in JSON-LD format
	 */
	public StringWriter convertDereferenceOutputToJsonLd(String uri, String xmlStr) {
		
		InputStream xsltFileAsStream = getClass().getResourceAsStream(XSLT_TRANSFORMATION_FILE);

		Transformer transformer;
		//OutputStream outStream = null;
		StringWriter result = new StringWriter();
		
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(xsltFileAsStream);
			transformer = factory.newTransformer(xslt);
			transformer.setParameter("uri",uri);
			transformer.setParameter("langs","en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru");
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
