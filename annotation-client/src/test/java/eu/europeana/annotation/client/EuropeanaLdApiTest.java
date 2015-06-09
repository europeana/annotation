package eu.europeana.annotation.client;

import static junit.framework.Assert.assertEquals;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Before;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;


public class EuropeanaLdApiTest {
	
    protected static String TEST_RESOURCE_ID = "/123/xyz";
	protected static String TEST_TARGET = "http://data.europeana.eu/item" + TEST_RESOURCE_ID;
	
    protected EuropeanaLdApiImpl europeanaLdApi;
    protected AnnotationLdParser europeanaParser;
    
    public static String simpleTagAnnotation = 
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

    public static String multipleSimpleTagAnnotation =
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
    		"\"serializedBy\": \"http://data.europeana.eu/provider/Historypin\"," +
    		"\"motivation\": \"oa:tagging\"," +
    		"\"body\": [\"church\", \"orthodox\"]," +
    		"\"target\": \"" + TEST_TARGET + "\"," +
       	 	"\"oa:equivalentTo\": \"" + TEST_TARGET + "\"" +
    		"}";
    
    public static String semanticTagAnnotation =
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
    		"\"body\": {" +
    	    	"\"@type\": \"oa:SemanticTag\"," +
    	    	"\"related\": \"http://dbpedia.org/resource/Paris\"" + 
    	    "}," +
    	    "\"target\": \"" + TEST_TARGET + "\"" +
    		"}";
    
    		
    public static String simpleLinkAnnotation =
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
    		"\"motivation\": \"oa:linking\"," +
            "\"target\": [" +
                       "\"http://www.europeana.eu/portal/record/123/xyz.html\"," + 
                       "\"http://www.europeana.eu/portal/record/333/xxx.html\"" +
                   "]," +
       	 	"\"oa:equivalentTo\": \"" + TEST_TARGET + "\"" +
    		"}";
    
    @Before
    public void initObjects() {
    	europeanaLdApi = new EuropeanaLdApiImpl();
		europeanaParser = new AnnotationLdParser();
    }
 
	/**
	 * This method validates annotation object. It verifies annotationNr, provider and content.
	 * @param provider
	 * @param annotationNr
	 * @param annotation
	 * @throws JsonParseException
	 */
	protected void validateAnnotation(String provider, long annotationNr, Annotation annotation)
			throws JsonParseException {
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		
		assertEquals(provider, annotation.getAnnotationId().getProvider());
		assertEquals((Long)annotationNr, annotation.getAnnotationId().getAnnotationNr());
		
	}
	
    
}
