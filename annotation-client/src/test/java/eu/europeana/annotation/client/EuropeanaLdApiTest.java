package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public class EuropeanaLdApiTest {
	
    private static String TEST_RESOURCE_ID = "/123/xyz";
	private static String TEST_TARGET = "http://data.europeana.eu/item" + TEST_RESOURCE_ID;
	
    private EuropeanaLdApiImpl europeanaLdApi;
    
    public static String taggingAnnotation = 
    		"{" +
    		"\"@context\": \"http://www.europeana.eu/annotation/context.jsonld\"," +
    		"\"@type\": \"oa:Annotation\"," +
    		"\"annotatedBy\": {" +
    			"\"@id\": \"https://www.historypin.org/en/person/55376/\"," +
    			"\"@type\": \"foaf:Person\"," +
    			"\"name\": \"John Smith\"" +
    		"}," +
    		"\"annotatedAt\": \"2015-02-27T12:00:43Z\"," +
    		"\"serializedAt\": \"2015-02-28T13:00:34Z\"," +
    		"\"serializedBy\": \"http://www.historypin.org\"," +
    		"\"motivation\": \"oa:tagging\"," +
    		"\"body\": \"church\"," +
    		"\"target\": \"" + TEST_TARGET + "\"" +
    		"}";

    @Before
    public void initObjects() {
    	europeanaLdApi = new EuropeanaLdApiImpl();
    }
    
	@Test
	public void createHistoryPinAnnotation() {
		
		String annotation = europeanaLdApi.createAnnotationLd(
//				Annotation annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, System.currentTimeMillis()
				, taggingAnnotation
				);
		System.out.println("historypin annotation test: " + annotation);
		assertNotNull(annotation);
	}
	
	@Test
	public void createWebannoAnnotation() {
		
		String annotation = europeanaLdApi.createAnnotationLd(
//				Annotation annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, taggingAnnotation
				);
		System.out.println("webanno annotation test: " + annotation);
		assertNotNull(annotation);
	}
	
	@Test
	public void getHistoryPinAnnotationByAnnotationNumber() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, taggingAnnotation
				);
		annotation = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotation);
		assertNotNull(annotation);
	}
	
	@Test
	public void searchHistoryPinAnnotationByTarget() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, taggingAnnotation
				);
		annotation = europeanaLdApi.searchLd(
				TEST_TARGET
				, null
				);
		System.out.println("historypin search annotation by target test: " + annotation);
		assertNotNull(annotation);
	}
	
	@Test
	public void searchHistoryPinAnnotationByResourceId() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, taggingAnnotation
				);
		annotation = europeanaLdApi.searchLd(
				null
				, TEST_RESOURCE_ID
				);
		System.out.println("historypin search annotation by resourceId test: " + annotation);
		assertNotNull(annotation);
	}
	
}
