package eu.europeana.annotation.client;

import org.junit.Before;


public class EuropeanaLdApiTest {
	
    protected static String TEST_RESOURCE_ID = "/123/xyz";
	protected static String TEST_TARGET = "http://data.europeana.eu/item" + TEST_RESOURCE_ID;
	
    protected EuropeanaLdApiImpl europeanaLdApi;
    
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
    		"\"@id\": \"http://example.org/anno1\"," + 
    		"\"@type\": \"oa:Annotation\"," +
//    		"\"motivation\": \"oa:tagging\"," +
    		"\"body\": {" +
    	    	"\"@type\": \"oa:SemanticTag\"," +
    	    	"\"related\": \"http://dbpedia.org/resource/Paris\"" + 
    	    "}," +
    	    "\"target\": {" +
    	    	"\"@id\": \"http://example.org/target1\"," +
    	    	"\"@type\": \"dctypes:Image\"" + 
    	  	"}" +
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
    }
    
}
