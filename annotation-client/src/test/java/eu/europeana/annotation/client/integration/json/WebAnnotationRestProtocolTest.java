package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.AnnotationJsonApi;
import eu.europeana.annotation.client.AnnotationJsonApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
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
    
    public String TEST_WEBANNO_PROVIDER = "webanno";
    
    /*createAnnotation: -tag  -link -historypin
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
				, TEST_WEBANNO_PROVIDER
				, null
				, false
				, TAG_JSON
				, "anonymous");
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	}
	
//	@Test
	public void getAnnotations() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create object within the test and do not rely on the objects stored in the database.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		annotationJsonApi.createAnnotation(testAnnotation);
		
		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
//		List<Annotation> results = retrievalApi.getAnnotations("testCollection", "testObject", "webanno");
		String results = retrievalApi.getAnnotations("testCollection", "testObject", "webanno");
		assertNotNull(results);
//		assertTrue(results.size() > 0);
//		Gson gson = new Gson();
//		
//		for (Annotation annotation : results) {
//			System.out.println(gson.toJson(annotation));			
//		}
		
	}
	
//	@Test(expected = TechnicalRuntimeException.class)
//	public void getAnnotationError() {
//		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
//		retrievalApi.getAnnotation("testCollection", "testObject", -1);		
//	}
	
//	@Test
	public void getAnnotation() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create object within the test and do not rely on the objects stored in the database.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		annotationJsonApi.createAnnotation(testAnnotation);
		
		List<Annotation> results = annotationJsonApi.getAnnotations("testCollection", "testObject");
		
		if(results.isEmpty()){
			System.out.println("No objects found in the database, test skipped");
			return;
		}
		
		Annotation anno = results.get(0);
		
		assertNotNull(anno);
		assertEquals(anno.getType(), testAnnotation.getType());			
	}
	
//	@Test
	public void setAnnotationStatus() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * set annotation status
		 */
		String updatedAnnotation = annotationJsonApi.setAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getIdentifier()
				, TEST_STATUS);
		assertNotNull(updatedAnnotation);
		
		/**
		 * retrieve updated annotation
		 */
		String retrievedAnnotationStatusResponse = annotationJsonApi.getAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getIdentifier());
//		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(retrievedAnnotation);
//		Annotation updatedAnnotationObject = JsonUtils.toAnnotationObject(annotationJsonString);
//		Annotation retrievedAnnotationObject = JsonUtils.toAnnotationObject(retrievedAnnotation);
//		assertEquals(updatedAnnotationObject.getStatus(), TEST_STATUS);
		assertTrue(retrievedAnnotationStatusResponse.contains(TEST_STATUS));
	}
	
//	@Test
	public void searchAnnotationStatusLogs() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * set annotation status
		 */
		String updatedAnnotation = annotationJsonApi.setAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getIdentifier()
				, TEST_STATUS);
		assertNotNull(updatedAnnotation);
		
		/**
		 * retrieve updated annotation
		 */
		String retrievedAnnotationStatusResponse = annotationJsonApi.getAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getIdentifier());
		assertTrue(retrievedAnnotationStatusResponse.contains(TEST_STATUS));
		/*String retrievedAnnotation = annotationJsonApi.getAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr() + 1);
		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(retrievedAnnotation);
		Annotation updatedAnnotationObject = JsonUtils.toAnnotationObject(annotationJsonString);
//		Annotation retrievedAnnotationObject = JsonUtils.toAnnotationObject(retrievedAnnotation);
		assertEquals(updatedAnnotationObject.getStatus(), TEST_STATUS);*/

		String statusLogsStr = annotationJsonApi.searchAnnotationStatusLogs(
				TEST_STATUS, "0", "10");
		assertNotNull(statusLogsStr);
	}

	
//	@Test
	public void checkAnnotationVisibility() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * check original annotation visibility
		 */
		String enabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
		assertNotNull(enabledAnnotation);
		
		/**
		 * disable annotation
		 */
		String updatedAnnotation = annotationJsonApi.disableAnnotation(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getIdentifier());
		assertNotNull(updatedAnnotation);
		annotationObject.setDisabled(true);
		
		/**
		 * check visibility of the disabled annotation
		 */
		String disabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
		assertNotNull(disabledAnnotation);
	}

	
//	@Test(expected=Exception.class)
	public void checkAnnotationVisibilityWithDifferentUser() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * check original annotation visibility
		 */
		String enabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
		assertNotNull(enabledAnnotation);
		
		/**
		 * checking visibility with wrong user should throw an exception 
		 */
		annotationJsonApi.checkVisibility(annotationObject, "guest");
		
	}

	
	/**
	 * Create a test annotation object.
	 * @return annotation object
	 */
	private Annotation createTestAnnotationObject() {
		Annotation testAnnotation = createBaseObjectTagInstance();	
		String annotation = annotationJsonApi.createAnnotation(testAnnotation);
		Annotation annotationObject = JsonUtils.toAnnotationObject(annotation);
		assertNotNull(annotation);
		return annotationObject;
	}
	
	
}
