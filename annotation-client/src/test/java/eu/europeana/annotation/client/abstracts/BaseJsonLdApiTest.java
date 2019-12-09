package eu.europeana.annotation.client.abstracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.BeforeEach;

import eu.europeana.annotation.client.AnnotationJsonLdApiImpl;
import eu.europeana.annotation.client.EuropeanaLdApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;


public class BaseJsonLdApiTest {
	
    protected static String TEST_RESOURCE_ID = "/123/xyz";
	protected static String TEST_TARGET = "http://data.europeana.eu/item" + TEST_RESOURCE_ID;
	
    protected AnnotationJsonLdApiImpl annotationJsonLdApi;
    protected EuropeanaLdApiImpl europeanaLdApi;
    protected AnnotationLdParser europeanaParser;
    
    
    public static String simpleTagAnnotation = 
    		"{" +
    		"\"@context\": \"http://www.w3.org/ns/anno.jsonld\"," +    		
    		"\"type\": \"Annotation\"," +
    		"\"creator\": {" +
				"\"id\": \"https://www.historypin.org/en/person/55376/\"," +
    			"\"type\": \"Person\"," +
    			"\"name\": \"John Smith\"" +
    		"}," +
    		"\"created\": \"2015-02-27T12:00:43Z\"," +
    		"\"generated\": \"2015-02-28T13:00:34Z\"," +
    		"\"generator\": \"http://www.historypin.org\"," +
			"\"motivation\": \"tagging\"," +
			"\"bodyValue\": \"church\"," +
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
    		"\"body\": [\"church\", \"orthodox\"]," +
    		"\"target\": \"" + TEST_TARGET + "\"," +
       	 	"\"oa:equivalentTo\": \"" + TEST_TARGET + "\"" +
    		"}";
        
    public static String semanticTagAnnotation =
    		"{" +
    		"\"@context\": \"http://www.w3.org/ns/anno.jsonld\"," +
    	    "\"type\": \"Annotation\"," +
    	    "\"motivation\": \"tagging\"," +
    		"\"body\": {" +
    	    	"\"id\": \"http://www.geonames.org/2988507\"," +
    	    	"\"format\": \"application/rdf+xml\"" + 
    	    "}," +
    	    "\"target\": \"http://data.europeana.eu/item/09102/_UEDIN_214\"" +
    		"}";
    
    public static String simpleLinkAnnotation =
    		"{" +
    	    "\"@context\": \"http://www.w3.org/ns/anno.jsonld\"," +
    	    "\"type\": \"Annotation\"," +
    	    "\"motivation\": \"linking\"," +
            "\"target\": [" +
            "\"http://data.europeana.eu/item/2059207/data_sounds_T471_5\"," + 
            "\"http://data.europeana.eu/item/2059207/data_sounds_T471_4\"" +
            "]," +
        	"}";
    
    public static String objectTagHistoryPinAnnotation =
        "{" +
        "\"@context\": {" +
            "\"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"" +
        "}," +
        "\"@type\": \"oa:annotation\"," +
        "\"sameAs\": \"http://historypin.com/annotation/1234\"," +
        "\"equivalentTo\": \"http://historypin.com/annotation/1234\"," +
        "\"annotatedAt\": \"2012-11-10T09:08:07\"," +
        "\"annotatedBy\": {" +
            "\"@id\": \"open_id_1\"," +
            "\"@type\": \"foaf:Person\"," +
            "\"name\": \"annonymous web user\"" +
        "}," +
        "\"body\": {" +
            "\"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\"," +
            "\"chars\": \"Vlad Tepes\"," +
            "\"foaf:page\": \"https://www.freebase.com/m/035br4\"," +
            "\"format\": \"text/plain\"," +
            "\"language\": \"ro\"," +
            "\"multilingual\": \"[ro:Vlad Tepes,en:Vlad the Impaler]\"" +
        "}," +
        "\"serializedAt\": \"2012-11-10T09:08:07\"," +
        "\"serializedBy\": {" +
            "\"@id\": \"open_id_2\"," +
            "\"@type\": \"prov:Software\"," +
            "\"foaf:homepage\": \"http://annotorious.github.io/\"," +
            "\"name\": \"Annotorious\"" +
        "}," +
        "\"styledBy\": {" +
            "\"@type\": \"oa:Css\"," +
            "\"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\"," +
            "\"styleClass\": \"annotorious-popup\"" +
        "}," +
        "\"target\": {" +
            "\"type\": \"oa:Image\"," +
            "\"contentType\": \"image/jpeg\"," +
            "\"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\"," +
            "\"selector\": {" +
                "\"@type\": \"[oa:SvgRectangle,euType:SVG_RECTANGLE_SELECTOR]\"," +
                "\"dimensionMap\": \"[left:5,right:3]\"" +
            "}," +
            "\"source\": {" +
                "\"@id\": \"/15502/GG_8285\"," +
                "\"contentType\": \"text/html\"," +
                "\"format\": \"dctypes:Text\"" +
            "}," +
            "\"targetType\": \"oa:Image\"" +
        "}," +
        "\"type\": \"OBJECT_TAG\"" +
    "}";
    		
    
    @BeforeEach
    public void initObjects() {
    	annotationJsonLdApi = new AnnotationJsonLdApiImpl();
    	europeanaLdApi = new EuropeanaLdApiImpl();
		europeanaParser = new AnnotationLdParser();
    }
 
	/**
	 * This method validates annotation object. It verifies annotationNr, provider and content.
	 * @param annotationNr
	 * @param annotation
	 * @throws JsonParseException
	 */
	protected void validateAnnotation(String provider, long annotationNr, Annotation annotation)
			throws JsonParseException {
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toRelativeUri());
		
		assertTrue(annotation.getAnnotationId().getIdentifier() != null);
		assertEquals((Long)annotationNr, Long.valueOf(annotation.getAnnotationId().getIdentifier()));		
	}
	
	/**
	 * This method extracts an items count parameter from a HTML response in search methods.
	 * @param annotationStr
	 * @return
	 */
	protected int extractItemsCount(String annotationStr) {
		int res = 0;
		String itemsCountStr = "";
		if (StringUtils.isNotEmpty(annotationStr)) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.ITEMS_COUNT + "\":(\\d+),");
			Matcher matcher = pattern.matcher(annotationStr);
			if (matcher.find())
			{
				itemsCountStr = matcher.group(1);
				res = Integer.valueOf(itemsCountStr);
			}
		}
		
		return res;
	}
    
	/**
	 * @param annotationStr
	 */
	protected void verifySearchResult(String annotationStr) {
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_TRUE));
		int itemsCount = extractItemsCount(annotationStr);
		assertTrue(itemsCount > 0);
	}
	
}
