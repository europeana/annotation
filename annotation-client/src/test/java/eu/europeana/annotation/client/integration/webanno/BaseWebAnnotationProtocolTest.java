package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApi;
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class BaseWebAnnotationProtocolTest {

	Logger log = Logger.getLogger(getClass());

	String TEST_STATUS = StatusTypes.PRIVATE.name().toLowerCase();
	
	String START = "{";
	String END = "}";
	String TYPE =  
		    "\"@context\": \"http://www.europeana.eu/annotation/context.jsonld\"," +
		    "\"@type\": \"oa:Annotation\",";
	
	String ANNOTATED_SERIALIZED =  
		    "\"annotatedBy\": {" +
		        "\"@id\": \"https://www.historypin.org/en/person/55376/\"," +
		        "\"@type\": \"foaf:Person\"," +
		        "\"name\": \"John Smith\"" +
		    "}," +
		    "\"annotatedAt\": \"2015-02-27T12:00:43Z\"," +
		    "\"serializedAt\": \"2015-02-28T13:00:34Z\"," +
		    "\"serializedBy\": \"http://www.historypin.org\",";
	
	String EQUIVALENT_TO = 
			"\"oa:equivalentTo\": \"https://www.historypin.org/en/item/456\",";

	public String TAG_CORE = TYPE + ANNOTATED_SERIALIZED +   		
		    "\"body\": \"church\"," +
			"\"target\": \"http://data.europeana.eu/item/123/xyz\"," +
			EQUIVALENT_TO;

	public String TAG_JSON_BY_TYPE_JSONLD = 
			START + TAG_CORE + END;
	
	public String LINK_CORE = 
			TYPE + ANNOTATED_SERIALIZED +
			"\"target\": [" +
			"\"http://www.europeana.eu/portal/record/123/xyz.html\"," +
			"\"http://www.europeana.eu/portal/record/333/xyz.html\"" +
			"]," +
			EQUIVALENT_TO;

	public String LINK_JSON_BY_TYPE_JSONLD = 
			START + LINK_CORE + END;
	
    public String TAG_JSON = 
    		START + TAG_CORE +   		
		    "\"motivation\": \"oa:tagging\"," +	
		    END;
		
    public String LINK_JSON = 
    		START + LINK_CORE +
		    "\"motivation\": \"oa:linking\"," +	
		    END;
	
    public String UPDATE_BODY =
    		"\"body\": \"Buccin Trombone\"";
    
    public String UPDATE_TARGET =
    		"\"target\": \"http://data.europeana.eu/item/09102/_UEDIN_214\"";
    
    public String UPDATE_JSON =
    		START +
    		UPDATE_BODY + "," + 
    		UPDATE_TARGET +
    		END;
    
    public String TEST_WSKEY = "apidemo";
    
    public String TEST_USER_TOKEN = "anonymous";
   
    private WebAnnotationProtocolApi apiClient;
    
    @Before
    public void initObjects() {
    	apiClient = new WebAnnotationProtocolApiImpl();
    }
    
    public WebAnnotationProtocolApi getApiClient() {
		return apiClient;
	}
   
    /**
	 * This method creates test annotation object
	 * @return response entity that contains response body, headers and status code.
	 */
	protected ResponseEntity<String> storeTestAnnotation() {
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = getApiClient().createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		return storedResponse;
	}
	
	/**
	 * This method creates test annotation object
	 * @return response entity that contains response body, headers and status code.
	 * @throws JsonParseException 
	 */
	protected Annotation createTestAnnotation() throws JsonParseException {
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> response = storeTestAnnotation(); 
		Annotation annotation = parseAndVerifyTestAnnotation(response);
		
		return annotation;
	}

	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response) throws JsonParseException {
		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(null, response.getBody());
		assertEquals(WebAnnotationFields.PROVIDER_WEBANNO, annotation.getAnnotationId().getProvider());
		return annotation;
	}

}
