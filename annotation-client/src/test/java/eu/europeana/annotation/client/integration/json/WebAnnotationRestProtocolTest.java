package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.AnnotationJsonApiImpl;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.utils.JsonUtils;


public class WebAnnotationRestProtocolTest extends AnnotationTestObjectBuilder{

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

	
    private AnnotationJsonApiImpl annotationJsonApi;
    
    @Before
    public void initObjects() {
    	annotationJsonApi = new AnnotationJsonApiImpl();
    }

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
		    "\"motivation\": \"oa:Tagging\"," +	
		    END;
		
    public String LINK_JSON = 
    		START + LINK_CORE +
		    "\"motivation\": \"oa:Linking\"," +	
		    END;
	
    public String TEST_WSKEY = "apidemo";
    
    public String TEST_USER_TOKEN = "anonymous";
    
    /*
    identifier is null
    type wrong
    type null
    not existing a
    format, descr fields or body corrupted


    createAnnotationByTypeJsonld: -tag  -link
    
    getA
    
    getAJsonld

    updateA
    exeptions
    
    deleteA*/
    

	@Test
	public void createWebannoAnnotationTag() {
		
		ResponseEntity<String> response = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	@Test
	public void createWebannoAnnotationLink() {
		
		ResponseEntity<String> response = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	@Test
	public void createWebannoAnnotationTagByTypeJsonld() {
		
		ResponseEntity<String> response = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WebAnnotationFields.TAG
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	@Test
	public void createWebannoAnnotationLinkByTypeJsonld() {
		
		ResponseEntity<String> response = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, LINK_JSON_BY_TYPE_JSONLD
				, TEST_USER_TOKEN
				, WebAnnotationFields.LINK
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
	/*
	@Test
	public void createHistorypinAnnotationTag() {
		
		ResponseEntity<String> response = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, "500"
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	*/
	

	@Test
	public void getAnnotation() {
		
		getAnnotationWorkflow(false);
	}

	
	/**
	 * Parameter isJsonld makes a difference between the two 
	 * getAnnotation and getAnnotationJsonld methods.
	 * @param isJsonld
	 */
	private void getAnnotationWorkflow(boolean isJsonld) {
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = annotationJsonApi.createAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, false
				, TAG_JSON
				, TEST_USER_TOKEN
				, null
				);
		String storedAnnotation = storedResponse.getBody();
		
		/**
		/* extract stored annotation ID
		 */
		String annotationId = JsonUtils.extractValueFromJsonString(
				WebAnnotationFields.AT_ID, storedAnnotation);
		
		/**
		 * retrieve identifier
		 */
		String identifier = JsonUtils.extractIdentifierFromAnnotationIdString(annotationId);

		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> response = annotationJsonApi.getAnnotation(
				TEST_WSKEY
				, WebAnnotationFields.PROVIDER_WEBANNO
				, identifier
				, isJsonld
				);
		
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), storedResponse.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
			
				
	@Test
	public void getAnnotationJsonLd() {
		
		getAnnotationWorkflow(true);
	}
			
				
//	@Test(expected=Exception.class)
//	public void checkAnnotationVisibilityWithDifferentUser() {
//		
//		Annotation annotationObject = createTestAnnotationObject();
//		
//		/**
//		 * check original annotation visibility
//		 */
//		String enabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
//		assertNotNull(enabledAnnotation);
//		
//		/**
//		 * checking visibility with wrong user should throw an exception 
//		 */
//		annotationJsonApi.checkVisibility(annotationObject, "guest");
//		
//	}
	
	
}
