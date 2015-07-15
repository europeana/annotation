package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationJsonApi;
import eu.europeana.annotation.client.AnnotationJsonApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.utils.JsonUtils;


public class AnnotationJsonApiTest extends AnnotationTestObjectBuilder{

	String TEST_STATUS = StatusTypes.PRIVATE.name().toLowerCase();
	
    private AnnotationJsonApiImpl annotationJsonApi;
    
    @Before
    public void initObjects() {
    	annotationJsonApi = new AnnotationJsonApiImpl();
    }

	public static String annotationJson = 
		"{" +
		"\"annotatedAt\": 1403852113248," + 
		"\"type\": \"OBJECT_TAG\"," + 
		"\"annotatedBy\": {" +
			"\"agentType\": \"foaf:Person\"," +
			"\"name\": \"annonymous web user\"," +
			"\"homepage\": null," +
			"\"mbox\": null," + 
			"\"openId\": null" + 
		"}," +
		"\"body\": {" + 
			"\"contentType\": \"Link\"," + 
			"\"mediaType\": null," + 
			"\"httpUri\": \"https://www.freebase.com/m/035br4\"," + 
			"\"language\": \"ro\"," + 
			"\"value\": \"Vlad Tepes\"," + 
			"\"multilingual\": \"[ro:Vlad Tepes,en:Vlad the Impaler]\"," + 
			"\"bodyType\": \"[oa:Tag,euType:SEMANTIC_TAG]\"" + 
		"}," + 
		"\"target\": {" + 
			"\"contentType\": \"text-html\"," + 
			"\"mediaType\": \"image\"," + 
			"\"language\": \"en\"," + 
			"\"value\": \"Vlad IV. Tzepesch, der Pfaehler, Woywode der Walachei 1456-1462 (gestorben 1477)\"," + 
			"\"httpUri\": \"http://europeana.eu/portal/record/15502/GG_8285.html\"," + 
			"\"targetType\": \"oa:WebPage\"" + 
		"}," + 
		"\"serializedAt\": \"\"," + 
		"\"serializedBy\": {" + 
			"\"agentType\": \"prov:Software\"," + 
			"\"name\": \"annonymous web user\"," + 
			"\"homepage\": null," + 
			"\"mbox\": null," + 
			"\"openId\": null" + 
		"}," + 
		"\"styledBy\":{" + 
			"\"contentType\": \"style\"," + 
			"\"mediaType\": \"text/css\"," + 
			"\"httpUri\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\"," + 
			"\"value\": null," + 
			"\"annotationClass\": \".annotorious-popup\"" + 
		"}" + 
	"}";

	@Test
	public void createAnnotation() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
//		Annotation annotation = annotationJsonApi.createAnnotation(testAnnotation);
		String annotation = annotationJsonApi.createAnnotation(testAnnotation);
//		String annotation = annotationJsonApi.createAnnotation(annotationJson);
		assertNotNull(annotation);
	}
	
	@Test
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
	
	@Test
	public void setAnnotationStatus() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * set annotation status
		 */
		String updatedAnnotation = annotationJsonApi.setAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr()
				, TEST_STATUS);
		assertNotNull(updatedAnnotation);
		
		/**
		 * retrieve updated annotation
		 */
		String retrievedAnnotationStatusResponse = annotationJsonApi.getAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr());
//		String annotationJsonString = JsonUtils.extractAnnotationStringFromJsonString(retrievedAnnotation);
//		Annotation updatedAnnotationObject = JsonUtils.toAnnotationObject(annotationJsonString);
//		Annotation retrievedAnnotationObject = JsonUtils.toAnnotationObject(retrievedAnnotation);
//		assertEquals(updatedAnnotationObject.getStatus(), TEST_STATUS);
		assertTrue(retrievedAnnotationStatusResponse.contains(TEST_STATUS));
	}
	
	@Test
	public void searchAnnotationStatusLogs() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * set annotation status
		 */
		String updatedAnnotation = annotationJsonApi.setAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr()
				, TEST_STATUS);
		assertNotNull(updatedAnnotation);
		
		/**
		 * retrieve updated annotation
		 */
		String retrievedAnnotationStatusResponse = annotationJsonApi.getAnnotationStatus(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr());
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

	
	@Test(expected=Exception.class)
	public void checkAnnotationVisibility() {
		
		Annotation annotationObject = createTestAnnotationObject();
		
		/**
		 * check original annotation visibility
		 */
		String enabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
		assertNotNull(enabledAnnotation);
//		assertFalse(enabledAnnotation.isDisabled());
		
		/**
		 * checking visibility with wrong user should throw an exception 
		 */
		annotationJsonApi.checkVisibility(annotationObject, "anonymous");
		
		/**
		 * disable annotation
		 */
		String updatedAnnotation = annotationJsonApi.disableAnnotation(
				annotationObject.getAnnotationId().getProvider()
				, annotationObject.getAnnotationId().getAnnotationNr());
		assertNotNull(updatedAnnotation);
		annotationObject.setDisabled(true);
		
		/**
		 * check visibility of the disabled annotation
		 */
		String disabledAnnotation = annotationJsonApi.checkVisibility(annotationObject, null);
		assertNotNull(disabledAnnotation);
//		assertTrue(disabledAnnotation.isDisabled());
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
