/*
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.jsonld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.test.AnnotationTestObjectBuilder;
//import eu.europeana.annotation.definitions.model.utils.JsonUtils;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;


/**
 *
 */
public class AnnotationLdTest {

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
		+ "\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\","
		+ "\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},"
		+ "\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},"
		+ "\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},"
		+ "\"type\":\"oa:Annotation\"}";


		
	@Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * This test checks AnnotationLd created from an empty Annotation object.
     */
    @Test
    public void testCreateEmptyAnnotationLd() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createEmptyBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"[oa:annotation,euType:OBJECT_TAG]\",\"annotatedBy\":{\"@type\":\"[SOFTWARE_AGENT]\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"multilingual\":\"\"},\"serializedBy\":{\"@type\":\"[SOFTWARE_AGENT]\"}}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"[oa:annotation,euType:OBJECT_TAG]\",\n    \"annotatedBy\": {\n        \"@type\": \"[SOFTWARE_AGENT]\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"multilingual\": \"\"\n    },\n    \"serializedBy\": {\n        \"@type\": \"[SOFTWARE_AGENT]\"\n    }\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            
    /**
     * This test converts Annotation object to AnnotationLd
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLd() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TAG\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@id\": \"open_id_1\",\n        \"@type\": \"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\n        \"chars\": \"Vlad Tepes\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"language\": \"ro\",\n        \"multilingual\": \"\"\n    },\n    \"motivatedBy\": \"TAGGING\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@id\": \"open_id_2\",\n        \"@type\": \"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"[oa:CssStyle,euType:CSS]\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource,euType:IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\",\n            \"dimensionMap\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"targetType\": \"[oa:SpecificResource,euType:IMAGE]\"\n    },\n    \"type\": \"OBJECT_TAG\"\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            
    /**
     * This test converts Annotation object to AnnotationLd by empty target
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLdWithMissingTarget() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();     
        baseObjectTag.setTarget(null);
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.TARGET));
    }
            
    /**
     * This test converts Annotation object to AnnotationLd by empty target entries.
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLdWithMissingTargetEntries() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();     
        baseObjectTag.getTarget().setContentType(null);
        baseObjectTag.getTarget().setHttpUri(null);
        baseObjectTag.getTarget().setEuropeanaId(null);
        baseObjectTag.getTarget().setLanguage(null);
        baseObjectTag.getTarget().setMediaType(null);
        baseObjectTag.getTarget().setSelector(null);
        baseObjectTag.getTarget().setSource(null);
        baseObjectTag.getTarget().setState(null);
        baseObjectTag.getTarget().setStyleClass(null);
        baseObjectTag.getTarget().setTargetType(null);
        baseObjectTag.getTarget().setValue(null);
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.TARGET));
    }
            
    /**
     * This test converts Annotation object to AnnotationLd by empty body
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLdWithMissingBody() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();     
        baseObjectTag.setBody(null);
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);       
        assertTrue(!actual.contains(WebAnnotationFields.BODY));
    }
            
    @Test
    public void testGsonSerializationForAnnotationLd() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String annotationLdOriginal = annotationLd.toString();
        AnnotationLd.toConsole("### annotationLd original ###", annotationLdOriginal);

        Gson gson = new Gson();
        String annotationLdSerializedString = gson.toJson(annotationLd, AnnotationLd.class);
        AnnotationLd.toConsole("### annotationLdGsonSerializedString ###", annotationLdSerializedString);
        
        AnnotationLd annotationLdDeserializedObject = gson.fromJson(annotationLdSerializedString, AnnotationLd.class);
        String annotationLdDeserializedString = annotationLdDeserializedObject.toString();
        AnnotationLd.toConsole("### annotationLdGsonDeserializedString ###", annotationLdDeserializedString);

        assertEquals(annotationLdDeserializedString, annotationLdOriginal);  
    }
    
    @Test
    public void testAnnotationLdToJsonLd() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String annotationLdStr = annotationLd.toString();
        AnnotationLd.toConsole("### annotationLd original ###", annotationLdStr);

        JsonLd jsonLd = (JsonLd) annotationLd;
        String jsonLdStr = jsonLd.toString();
        AnnotationLd.toConsole("### jsonLd ###", jsonLdStr);

        assertEquals(jsonLdStr, annotationLdStr);  
    }
    
    @Test
    public void testJsonLdToAnnotationLd() {
    	        
        String jsonLdStr = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource]\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"text/html\",\"format\":\"dctypes:Text\"}},\"type\":\"oa:Annotation\"}";
        String jsonLdIndentStr = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        }\n    },\n    \"type\": \"oa:Annotation\"\n}";
        
        AnnotationLd annotationLd = null;
        try {
        	JsonLd parsedJsonLd = JsonLdParser.parseExt(jsonLdStr);
            annotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        String annotationLdStr = annotationLd.toString();        
        String annotationLdIndentStr = annotationLd.toString(4);        
        System.out.println("### annotationLdStr ###");
        AnnotationLd.toConsole("jsonLdStr      ", jsonLdStr);
        AnnotationLd.toConsole("annotationLdStr", annotationLdStr);
        AnnotationLd.toConsole("", annotationLdIndentStr);

        assertEquals(jsonLdStr, annotationLdStr);
        assertEquals(jsonLdIndentStr, annotationLdIndentStr);
    }            
    
    @Test
    public void testParseAnnotationLdStringToJsonLd() {
    	
        Annotation baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TAG\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@id\": \"open_id_1\",\n        \"@type\": \"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\n        \"chars\": \"Vlad Tepes\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\",\n        \"language\": \"ro\",\n        \"multilingual\": \"\"\n    },\n    \"motivatedBy\": \"TAGGING\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@id\": \"open_id_2\",\n        \"@type\": \"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"[oa:CssStyle,euType:CSS]\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource,euType:IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\",\n            \"dimensionMap\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"targetType\": \"[oa:SpecificResource,euType:IMAGE]\"\n    },\n    \"type\": \"OBJECT_TAG\"\n}";
        
        assertEquals(expectedIndent, actualIndent);

        AnnotationLd parsedAnnotationLd = null;
        JsonLd parsedJsonLd = null;
        try {
        	parsedJsonLd = JsonLdParser.parseExt(actual);
        	parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        parsedAnnotationLd.setUseTypeCoercion(false);
        parsedAnnotationLd.setUseCuries(true);
        
        String parsedJsonLdStr = parsedJsonLd.toString();        
        String parsedAnnotationLdStr = parsedAnnotationLd.toString();        
        AnnotationLd.toConsole("", parsedAnnotationLdStr);
        AnnotationLd.toConsole("", parsedAnnotationLd.toString(4));

        assertEquals(actual, parsedJsonLdStr);
        assertEquals(actual, parsedAnnotationLdStr);
        assertEquals(expected, parsedAnnotationLdStr);
        assertNotNull(parsedAnnotationLdStr);
        assertEquals(actualIndent, parsedAnnotationLd.toString(4));
    }            
    
    /**
     * This test converts AnnotationLd object to an Annotation object.
     */
    @Test
    public void testAnnotationLdToAnnotation() {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
        
        /**
         * convert Annotation object to AnnotationLd object.
         */
        AnnotationLd annotationLd = new AnnotationLd(originalAnnotation);
        String initialAnnotationIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * read Annotation object from AnnotationLd object.
         */
        Annotation annotationFromAnnotationLd = annotationLd.getAnnotation();

        AnnotationLd convertedAnnotationLd = new AnnotationLd(annotationFromAnnotationLd);
        String convertedAnnotationIndent = convertedAnnotationLd.toString(4);
        AnnotationLd.toConsole("### convertedAnnotation ###", convertedAnnotationIndent);

        assertEquals(originalAnnotation.getMotivatedBy(), annotationFromAnnotationLd.getMotivatedBy());
        assertEquals(originalAnnotation.getAnnotationId(), annotationFromAnnotationLd.getAnnotationId());
        // Original object does not have EuropeanaUri
        originalAnnotation.getTarget().setEuropeanaId(annotationFromAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(originalAnnotation, annotationFromAnnotationLd);
    }
            
    /**
     * This test ensures that no double entries are possible in the Agent type.
     */
    @Test
    public void testAddExistingAgentType() {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
        String originalAgentType = TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType());
        originalAnnotation.getSerializedBy().addType(AgentTypes.SOFTWARE_AGENT.name());
        assertEquals(originalAgentType, TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType()));
        originalAnnotation.getSerializedBy().addType("new type");
        System.out.println("Agent type: " + TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType()));
        assertFalse(originalAgentType.equals(TypeUtils.getTypeListAsStr(originalAnnotation.getSerializedBy().getAgentType())));
    }
            
    /**
     * This test demonstrates the chain of conversions from Annotation object
     * to Annotation object using following steps:
     * 1. Annotation object -> AnnotationLd object
     * 2. AnnotationLd object -> JsonLd string
     * 3. JsonLd string -> JsonLdParser -> JsonLd object
     * 4. JsonLd object -> AnnotationLd object
     * 5. AnnotationLd object -> Annotation object.
     */
    @Test
    public void testAnnotationToAnnotation() {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
		
        /**
         * convert Annotation object to AnnotationLd object.
         * 1. Annotation object -> AnnotationLd object
         */
        AnnotationLd annotationLd = new AnnotationLd(originalAnnotation);
        
        /**
         * get JsonLd string
         * 2. AnnotationLd object -> JsonLd string
         */
        String initialAnnotationStr = annotationLd.toString();
        String initialAnnotationIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * parse JsonLd string using JsonLdParser.
         * 3. JsonLd string -> JsonLdParser -> JsonLd object
         */
        AnnotationLd parsedAnnotationLd = null;
        JsonLd parsedJsonLd = null;
        try {
        	parsedJsonLd = JsonLdParser.parseExt(initialAnnotationStr);
        	
        	/**
        	 * convert JsonLd to AnnotationLd.
        	 * 4. JsonLd object -> AnnotationLd object
        	 */
        	parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        /**
         * 5. AnnotationLd object -> Annotation object.
         */
        Annotation annotationFromAnnotationLd = parsedAnnotationLd.getAnnotation();
        System.out.println("originalAnnotation: " + originalAnnotation.toString());
        System.out.println("annotationFromAnnotationLd: " + annotationFromAnnotationLd.toString());
        
        assertEquals(originalAnnotation.getMotivatedBy(), annotationFromAnnotationLd.getMotivatedBy());
        assertEquals(originalAnnotation.getAnnotationId(), annotationFromAnnotationLd.getAnnotationId());
        // Original object does not have EuropeanaUri
        originalAnnotation.getTarget().setEuropeanaId(annotationFromAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(originalAnnotation, annotationFromAnnotationLd);
    }
          
    /**
     * This test converts AnnotationLd object with selector to an Annotation object.
     */
    @Test
    public void testAnnotationSelector() {
    	
    	/**
    	 * create initial Annotation object.
    	 */
        Annotation originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
        
        /**
         * add Selector to the Target in Annotation object
         */
        originalAnnotation.getTarget().setSelector(AnnotationTestObjectBuilder.buildSelector());
        
        /**
         * convert Annotation object to AnnotationLd object.
         */
        AnnotationLd annotationLd = new AnnotationLd(originalAnnotation);
        String initialAnnotationIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * read Annotation object from AnnotationLd object.
         */
        Annotation annotationFromAnnotationLd = annotationLd.getAnnotation();

        AnnotationLd convertedAnnotationLd = new AnnotationLd(annotationFromAnnotationLd);
        String convertedAnnotationIndent = convertedAnnotationLd.toString(4);
        AnnotationLd.toConsole("### convertedAnnotation ###", convertedAnnotationIndent);

//        System.out.println(originalAnnotation.toString());
        assertEquals(originalAnnotation.getTarget().getSelector(), annotationFromAnnotationLd.getTarget().getSelector());
        // Original object does not have EuropeanaUri
        originalAnnotation.getTarget().setEuropeanaId(annotationFromAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(originalAnnotation, annotationFromAnnotationLd);
    }
                
    @Test
    public void testExtractEuType() {
    	
    	String bodyPart = BodyTypes.SEMANTIC_TAG.name();
    	String actualEuType = bodyPart;
    	String typeArray = "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:" 
    			+ bodyPart + "]";
    	String expectedEuType = new TypeUtils().getEuTypeFromTypeArray(typeArray);
        assertEquals(actualEuType, expectedEuType);
    }   
    
	@Test
	public void testMultilingualAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
        /**
         * parse JsonLd string using JsonLdParser.
         * JsonLd string -> JsonLdParser -> JsonLd object -> AnnotationLd object
         */
        AnnotationLd parsedAnnotationLd = null;
        JsonLd parsedJsonLd = null;
        try {
        	parsedJsonLd = JsonLdParser.parseExt(annotationJsonLdObjectString);
        	parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        String original = parsedAnnotationLd.toString();
        AnnotationLd.toConsole("", original);      
        assertTrue(original.contains(TEST_EN_VALUE) && original.contains(TEST_RO_VALUE));
		
        String origIndent = parsedAnnotationLd.toString(4);
        AnnotationLd.toConsole("", origIndent);
	}        
    
}
