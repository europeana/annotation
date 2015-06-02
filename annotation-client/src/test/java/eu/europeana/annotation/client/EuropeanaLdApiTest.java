package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public class EuropeanaLdApiTest {

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
    		"\"target\": \"http://data.europeana.eu/item/123/xyz\"" +
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
	
}
