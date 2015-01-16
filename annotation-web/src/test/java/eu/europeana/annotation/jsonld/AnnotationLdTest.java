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
import static org.junit.Assert.assertNotNull;

import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.factory.ModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationPartTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

import eu.europeana.annotation.jsonld.AnnotationTestObjectBuilder;


/**
 *
 */
public class AnnotationLdTest {

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";

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
    	
        BaseObjectTag baseObjectTag = AnnotationTestObjectBuilder.createEmptyBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@type\":\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\"},\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\"},\"styledBy\":},\"target\":]}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@type\":     \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\"\n    },\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\"\n    },\n    \"styledBy\": \n    },\n    \"target\": \n    ]\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            
    /**
     * This test converts Annotation object to AnnotationLd
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLd() {
    	
        BaseObjectTag baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\"},\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\"},\"type\":\"oa:Annotation\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TYPE\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\n        \"chars\": \"Vlad Tepes\",\n        \"dc:language\": \"ro\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\"\n    },\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"targetType\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\"\n    },\n    \"type\": \"oa:Annotation\"\n}";
        
        assertEquals(expectedIndent, actualIndent);
    }
            
    @Test
    public void testGsonSerializationForAnnotationLd() {
    	
        BaseObjectTag baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
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
    	
        BaseObjectTag baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
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
    	
        BaseObjectTag baseObjectTag = AnnotationTestObjectBuilder.createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\"},\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\"},\"type\":\"oa:Annotation\"}";
        
        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TYPE\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": {\n        \"@type\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\n        \"chars\": \"Vlad Tepes\",\n        \"dc:language\": \"ro\",\n        \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n        \"format\": \"text/plain\"\n    },\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"targetType\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\"\n    },\n    \"type\": \"oa:Annotation\"\n}";
        
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
        BaseObjectTag originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
        
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
        assertEquals(originalAnnotation, annotationFromAnnotationLd);
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
        BaseObjectTag originalAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance(); 
        
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
//        Annotation annotationFromAnnotationLd = parsedAnnotationLd.getAnnotationExt();
//        Annotation annotationFromAnnotationLd = parsedAnnotationLd.getAnnotationByJson();
        System.out.println("originalAnnotation: " + originalAnnotation.toString());
        System.out.println("annotationFromAnnotationLd: " + annotationFromAnnotationLd.toString());
        
        assertEquals(originalAnnotation.getMotivatedBy(), annotationFromAnnotationLd.getMotivatedBy());
        assertEquals(originalAnnotation.getAnnotationId(), annotationFromAnnotationLd.getAnnotationId());
        assertEquals(originalAnnotation, annotationFromAnnotationLd);
    }
            
    @Test
    public void testExtractEuType() {
    	
    	String annotationPart = AnnotationPartTypes.BODY.name();
    	String bodyPart = BodyTypes.SEMANTIC_TAG.name();
    	String actualEuType = annotationPart + WebAnnotationFields.SPLITTER + bodyPart;
    	String typesString = "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:" 
    			+ annotationPart + WebAnnotationFields.SPLITTER + bodyPart + "]";
    	String expectedEuType = ModelObjectFactory.extractEuType(typesString);
        assertEquals(actualEuType, expectedEuType);
    }    
}
