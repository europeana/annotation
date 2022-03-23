package eu.europeana.annotation.jsonld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.annotation.Resource;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.google.gson.Gson;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.utils.serialize.AnnotationLdSerializer;


/**
 * @deprecated need to update the tests according to correctly use the EuropeanaAnnotationLd
 */
@Deprecated
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-web-context.xml"})
public class EuropeanaAnnotationLdTest  extends AnnotationTestObjectBuilder{
  
    @Resource
    AnnotationLdSerializer annotationLdSerializer;

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	
	public static String TEST_RO_VALUE = "Vlad Tepes";
	public static String TEST_EN_VALUE = "Vlad the Impaler";
	
	/**
	 * Create a test JsonLd annotation object string.
	 */
    public static String annotationJsonLdObjectString = 
		"{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"}," 
		+ "\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\","
		+ "\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},"
		+ "\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\","
		+ "\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\","
		+ "\"format\":\"text/plain\",\"language\":\"ro\","
		+ "\"multilingual\": \"[ro:Vlad Tepes,en:Vlad the Impaler]\"},"
		+ "\"motivation\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\","
		+ "\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},"
		+ "\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},"
		+ "\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},"
		+ "\"type\":\"oa:Annotation\"}";


		
	@BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * This test checks EuropeanaAnnotationLd created from an empty Annotation object.
     */
    @Test
    public void testCreateEmptyEuropeanaAnnotationLd() {
    	
        Annotation baseObjectTag = createEmptyBaseObjectTagInstance();
        
        annotationLdSerializer.setAnnotation(baseObjectTag);
        String actual = annotationLdSerializer.toString(0);

        //EuropeanaAnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"[oa:annotation,euType:OBJECT_TAG]\",\"annotatedBy\":{\"@type\":\"SOFTWARE\"},\"body\":{\"@type\":\"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"multilingual\":\"\"},\"serializedBy\":{\"@type\":\"SOFTWARE\"}}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLdSerializer.toString();
        //EuropeanaAnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"[oa:annotation,euType:OBJECT_TAG]\",\n    \"annotatedBy\": {\n        \"@type\": \"SOFTWARE\"\n    },\n    \"body\": {\n        \"@type\": \"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"multilingual\": \"\"\n    },\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE\"\n    }\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            

	/**
     * This test converts Annotation object to EuropeanaAnnotationLd
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToEuropeanaAnnotationLd() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();        
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String actual = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"foaf:Person\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"equivalentTo\":\"http://historypin.com/annotation/1234\",\"motivation\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"prov:Software\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CSS\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//testCollection/testObject.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"type\":\"oa:IMAGE\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLdSerializer.toString(4);
//        EuropeanaAnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TAG\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@id\": \"open_id_1\",\n        \"@type\": \"foaf:Person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\n        \"chars\": \"Vlad Tepes\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"language\": \"ro\",\n        \"multilingual\": \"\"\n    },\n    \"equivalentTo\": \"http://historypin.com/annotation/1234\",\n    \"motivation\": \"TAGGING\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@id\": \"open_id_2\",\n        \"@type\": \"prov:Software\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CSS\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\",\n            \"dimensionMap\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//testCollection/testObject.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"type\": \"oa:IMAGE\"\n    },\n    \"type\": \"OBJECT_TAG\"\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            
    /**
     * This test converts Annotation object to EuropeanaAnnotationLd by empty target
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToEuropeanaAnnotationLdWithMissingTarget() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();     
        baseObjectTag.setTarget(null);
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String actual = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.TARGET));
    }
            
    /**
     * This test converts Annotation object to EuropeanaAnnotationLd by empty target entries.
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToEuropeanaAnnotationLdWithMissingTargetEntries() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();     
        baseObjectTag.getTarget().setContentType(null);
        baseObjectTag.getTarget().setHttpUri(null);
//        baseObjectTag.getTarget().setEuropeanaId(null);
        baseObjectTag.getTarget().setLanguage(null);
//        baseObjectTag.getTarget().setMediaType(null);
        baseObjectTag.getTarget().setSelector(null);
        baseObjectTag.getTarget().setSource(null);
        baseObjectTag.getTarget().setState(null);
        baseObjectTag.getTarget().setStyleClass(null);
        baseObjectTag.getTarget().setType(null);
        baseObjectTag.getTarget().setValue(null);
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String actual = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.TARGET));
    }
            
    /**
     * This test converts Annotation object to EuropeanaAnnotationLd by empty body
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToEuropeanaAnnotationLdWithMissingBody() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();     
        baseObjectTag.setBody(null);
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String actual = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.BODY));
    }
            
    @Test
    public void testGsonSerializationForEuropeanaAnnotationLd() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();        
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String EuropeanaAnnotationLdOriginal = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("### EuropeanaAnnotationLd original ###", EuropeanaAnnotationLdOriginal);

        Gson gson = new Gson();
        String EuropeanaAnnotationLdSerializedString = gson.toJson(annotationLdSerializer, AnnotationLdSerializer.class);
//        EuropeanaAnnotationLd.toConsole("### EuropeanaAnnotationLdGsonSerializedString ###", EuropeanaAnnotationLdSerializedString);
        
        AnnotationLdSerializer EuropeanaAnnotationLdDeserializedObject = gson.fromJson(EuropeanaAnnotationLdSerializedString, AnnotationLdSerializer.class);
        String EuropeanaAnnotationLdDeserializedString = EuropeanaAnnotationLdDeserializedObject.toString();
//        EuropeanaAnnotationLd.toConsole("### EuropeanaAnnotationLdGsonDeserializedString ###", EuropeanaAnnotationLdDeserializedString);

        assertEquals(EuropeanaAnnotationLdDeserializedString, EuropeanaAnnotationLdOriginal);  
    }
    
    @Test
    public void testEuropeanaAnnotationLdToJsonLd() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();        
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String annotationLdStr = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("### EuropeanaAnnotationLd original ###", EuropeanaAnnotationLdStr);

        JsonLd jsonLd = (JsonLd) annotationLdSerializer;
        String jsonLdStr = jsonLd.toString();
//        EuropeanaAnnotationLd.toConsole("### jsonLd ###", jsonLdStr);

        assertEquals(jsonLdStr, annotationLdStr);  
    }
    
//    @Test
//    public void testJsonLdToEuropeanaAnnotationLd() {
//    	        
//        String jsonLdStr = "{\n    \"@context\": {\n        \":Annotation\": \"oa:Annotation\",\n        \":tagging\": \"oa:tagging\"\n    },\n    \"@type\": \":Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivation\": \":tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        }\n    },\n    \"type\": \":Annotation\"\n}";
//        String jsonLdIndentStr = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivation\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        }\n    },\n    \"type\": \"oa:Annotation\"\n}";
//        
//        EuropeanaAnnotationLd EuropeanaAnnotationLd = null;
//        try {
//        	JsonLd parsedJsonLd = JsonLdParser.parseExt(jsonLdStr);
//            EuropeanaAnnotationLd = new EuropeanaAnnotationLd(parsedJsonLd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        
//        String EuropeanaAnnotationLdStr = EuropeanaAnnotationLd.toString();        
//        String EuropeanaAnnotationLdIndentStr = EuropeanaAnnotationLd.toString(4);        
//        System.out.println("### EuropeanaAnnotationLdStr ###");
//        EuropeanaAnnotationLd.toConsole("jsonLdStr      ", jsonLdStr);
//        EuropeanaAnnotationLd.toConsole("EuropeanaAnnotationLdStr", EuropeanaAnnotationLdStr);
//        EuropeanaAnnotationLd.toConsole("", EuropeanaAnnotationLdIndentStr);
//
//        assertEquals(jsonLdStr, EuropeanaAnnotationLdStr);
//        assertEquals(jsonLdIndentStr, EuropeanaAnnotationLdIndentStr);
//    }            
    
    @Test
    public void testParseEuropeanaAnnotationLdStringToJsonLd() {
    	
        Annotation baseObjectTag = createBaseObjectTagInstance();        
        annotationLdSerializer.setAnnotation(baseObjectTag);
        
        String actual = annotationLdSerializer.toString();
//        EuropeanaAnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"foaf:Person\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"equivalentTo\":\"http://historypin.com/annotation/1234\",\"motivation\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"prov:Software\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CSS\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//testCollection/testObject.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"type\":\"oa:IMAGE\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLdSerializer.toString(4);
//        EuropeanaAnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TAG\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@id\": \"open_id_1\",\n        \"@type\": \"foaf:Person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\n        \"chars\": \"Vlad Tepes\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"language\": \"ro\",\n        \"multilingual\": \"\"\n    },\n    \"equivalentTo\": \"http://historypin.com/annotation/1234\",\n    \"motivation\": \"TAGGING\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@id\": \"open_id_2\",\n        \"@type\": \"prov:Software\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CSS\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\",\n            \"dimensionMap\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//testCollection/testObject.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"type\": \"oa:IMAGE\"\n    },\n    \"type\": \"OBJECT_TAG\"\n}";
        
        assertEquals(expectedIndent, actualIndent);

        AnnotationLdParser deserializer = new AnnotationLdParser();
//        JsonLd parsedJsonLd = null;
        try {
//        	parsedJsonLd = JsonLdParser.parseExt(actual);
        	deserializer.parseAnnotation(null, actualIndent);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        annotationLdSerializer.setUseTypeCoercion(false);
        annotationLdSerializer.setUseCuries(true);
        
//        String parsedJsonLdStr = parsedJsonLd.toString();        
        String parsedEuropeanaAnnotationLdStr = annotationLdSerializer.toString();        
//        EuropeanaAnnotationLd.toConsole("", parsedEuropeanaAnnotationLdStr);
//        EuropeanaAnnotationLd.toConsole("", parsedEuropeanaAnnotationLd.toString(4));

//        assertEquals(actual, parsedJsonLdStr);
//        assertEquals(actual, parsedEuropeanaAnnotationLdStr);
//        assertEquals(expected, parsedEuropeanaAnnotationLdStr);
        assertNotNull(parsedEuropeanaAnnotationLdStr);
//        assertEquals(actualIndent, parsedEuropeanaAnnotationLd.toString(4));
    }            
    
    /**
     * This test converts EuropeanaAnnotationLd object to an Annotation object.
     * @throws JsonParseException 
     */
//    @Test
    public void testEuropeanaAnnotationLdToAnnotation() throws JsonParseException {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = createBaseObjectTagInstance(); 
        
        /**
         * convert Annotation object to EuropeanaAnnotationLd object.
         */
//        AnnotationLdDeserializerDeprecated deserializer = new AnnotationLdDeserializerDeprecated(originalAnnotation);
        AnnotationLdParser deserializer = new AnnotationLdParser();
        annotationLdSerializer.setAnnotation(originalAnnotation);
        
        String initialAnnotationIndent = annotationLdSerializer.toString(4);
//        EuropeanaAnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * read Annotation object from EuropeanaAnnotationLd object.
         */
        Annotation annotationFromEuropeanaAnnotationLd = deserializer.parseAnnotation(null, initialAnnotationIndent);

//        AnnotationLdSerializer convertedEuropeanaAnnotationLd = new AnnotationLdSerializer(annotationFromEuropeanaAnnotationLd);
//        String convertedAnnotationIndent = convertedEuropeanaAnnotationLd.toString(4);
//        EuropeanaAnnotationLd.toConsole("### convertedAnnotation ###", convertedAnnotationIndent);

        assertEquals(originalAnnotation.getMotivation(), annotationFromEuropeanaAnnotationLd.getMotivation());
        assertEquals(originalAnnotation.getIdentifier(), annotationFromEuropeanaAnnotationLd.getIdentifier());
        // Original object does not have EuropeanaUri
//        originalAnnotation.getTarget().setEuropeanaId(annotationFromEuropeanaAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(originalAnnotation, annotationFromEuropeanaAnnotationLd);
    }
            
    /**
     * This test ensures that no double entries are possible in the Agent type.
     */
    @Test
    public void testAddExistingAgentType() {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = createBaseObjectTagInstance(); 
//        String originalAgentType = TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType());
        String originalAgentType = originalAnnotation.getGenerator().getType();
//        originalAnnotation.getSerializedBy().setType(AgentTypes.SOFTWARE.name());
        originalAnnotation.getGenerator().setType("prov:Software");
//        assertEquals(originalAgentType, TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType()));
        assertEquals(originalAgentType, originalAnnotation.getGenerator().getType());
////        originalAnnotation.getSerializedBy().addType("new type");
//        System.out.println("Agent type: " + TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType()));
        System.out.println("Agent type: " + originalAnnotation.getGenerator().getType());
//        assertFalse(originalAgentType.equals(TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType())));
        assertTrue(originalAgentType.equals(originalAnnotation.getGenerator().getType()));
    }
            
    /**
     * This test demonstrates the chain of conversions from Annotation object
     * to Annotation object using following steps:
     * 1. Annotation object -> EuropeanaAnnotationLd object
     * 2. EuropeanaAnnotationLd object -> JsonLd string
     * 3. JsonLd string -> JsonLdParser -> JsonLd object
     * 4. JsonLd object -> EuropeanaAnnotationLd object
     * 5. EuropeanaAnnotationLd object -> Annotation object.
     */
//    @Test
//    public void testAnnotationToAnnotation() {
//    	
//    	/**
//    	 * create initial Annotation object.
//    	 */
//        Annotation originalAnnotation = createBaseObjectTagInstance(); 
//		
//        /**
//         * convert Annotation object to EuropeanaAnnotationLd object.
//         * 1. Annotation object -> EuropeanaAnnotationLd object
//         */
//        AnnotationLdSerializer annotation = new AnnotationLdSerializer(originalAnnotation);
//        
//        /**
//         * get JsonLd string
//         * 2. EuropeanaAnnotationLd object -> JsonLd string
//         */
//        String initialAnnotationStr = annotation.toString();
//        String initialAnnotationIndent = annotation.toString(4);
////        EuropeanaAnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);
//
//        /**
//         * parse JsonLd string using JsonLdParser.
//         * 3. JsonLd string -> JsonLdParser -> JsonLd object
//         */
//        AnnotationLdParser parsedEuropeanaAnnotationLd = null;
//        JsonLd parsedJsonLd = null;
//        try {
//        	parsedJsonLd = JsonLdParser.parseExt(initialAnnotationStr);
//        	
//        	/**
//        	 * convert JsonLd to EuropeanaAnnotationLd.
//        	 * 4. JsonLd object -> EuropeanaAnnotationLd object
//        	 */
//        	parsedEuropeanaAnnotationLd = ;
////        	AnnotationLdDeserializerDeprecated deserializer = new AnnotationLdDeserializerDeprecated();
//            
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        
//        /**
//         * 5. EuropeanaAnnotationLd object -> Annotation object.
//         */
//        //Annotation annotationFromEuropeanaAnnotationLd = parsedEuropeanaAnnotationLd.getAnnotation();
//        System.out.println("originalAnnotation: " + originalAnnotation.toString());
//        System.out.println("annotationFromEuropeanaAnnotationLd: " + parsedEuropeanaAnnotationLd);
//        
//        assertEquals(originalAnnotation.getMotivation(), parsedEuropeanaAnnotationLd.getMotivation());
//        assertEquals(originalAnnotation.getAnnotationId(), parsedEuropeanaAnnotationLd.getAnnotationId());
//        // Original object does not have EuropeanaUri
////        originalAnnotation.getTarget().setEuropeanaId(annotationFromEuropeanaAnnotationLd.getTarget().getEuropeanaId());
//        assertEquals(originalAnnotation, annotationFromEuropeanaAnnotationLd);
//    }
          
    /**
     * This test converts EuropeanaAnnotationLd object with selector to an Annotation object.
     * @throws JsonParseException 
     * @deprecated update this test 
     */
//    @Test
    public void testAnnotationSelector() throws JsonParseException {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = createBaseObjectTagInstance(); 
        
        /**
         * add Selector to the Target in Annotation object
         */
        originalAnnotation.getTarget().setSelector(AnnotationTestObjectBuilder.buildSelector());
        
        /**
         * convert Annotation object to EuropeanaAnnotationLd object.
         */
        annotationLdSerializer.setAnnotation(originalAnnotation);
        AnnotationLdParser annotationDeserializer = new AnnotationLdParser();
        
        String initialAnnotationIndent = annotationLdSerializer.toString(4);
//        EuropeanaAnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * read Annotation object from EuropeanaAnnotationLd object.
         */
        Annotation annotationFromEuropeanaAnnotationLd = annotationDeserializer.parseAnnotation(null, initialAnnotationIndent);

//        AnnotationLdSerializer convertedEuropeanaAnnotationLd = new AnnotationLdSerializer(annotationFromEuropeanaAnnotationLd);
//        String convertedAnnotationIndent = convertedEuropeanaAnnotationLd.toString(4);
//        EuropeanaAnnotationLd.toConsole("### convertedAnnotation ###", convertedAnnotationIndent);

//        System.out.println(originalAnnotation.toString());
        assertEquals(originalAnnotation.getTarget().getSelector(), annotationFromEuropeanaAnnotationLd.getTarget().getSelector());
        // Original object does not have EuropeanaUri
//        originalAnnotation.getTarget().setEuropeanaId(annotationFromEuropeanaAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(originalAnnotation, annotationFromEuropeanaAnnotationLd);
    }
                
//    @Test
//    public void testExtractEuType() {
//    	
//    	String bodyPart = BodyTypes.SEMANTIC_TAG.name();
//    	String actualEuType = bodyPart;
//    	String typeArray = "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:" 
//    			+ bodyPart + "]";
//    	String expectedEuType = new TypeUtils().getInternalTypeFromTypeArray(typeArray);
//        assertEquals(actualEuType, expectedEuType);
//    }   
    
//	@Test
	public void testMultilingualAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
        /**
         * parse JsonLd string using JsonLdParser.
         * JsonLd string -> JsonLdParser -> JsonLd object -> EuropeanaAnnotationLd object
         */
//		AnnotationLdParser parser = null;
//        JsonLd parsedJsonLd = null;
//        try {
//        	parsedJsonLd = JsonLdParser.parseExt(annotationJsonLdObjectString);
////        	parser = new AnnotationLdParser();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        //TODO:update test
//        String original = parser.toString();
//        //EuropeanaAnnotationLd.toConsole("", original);      
//        assertTrue(original.contains(TEST_EN_VALUE) && original.contains(TEST_RO_VALUE));
//		
//        String origIndent = parser.toString(4);
//        //EuropeanaAnnotationLd.toConsole("", origIndent);
	}        
    
}
