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

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Rectangle;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.selector.impl.SvgRectangleSelector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.BaseStyle;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

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

	protected TagBody buildSemanticTagBody(String text, String language) {
		
		TagBody body = new SemanticTagBody();
		
		body.setBodyType("[oa:Tag,cnt:ContentAsText,dctypes:Text]");
		body.setValue(text);
		body.setLanguage(language);
		body.setContentType("text/plain");
		body.setMediaType("oa:SemanticTag");
		body.setHttpUri("https://www.freebase.com/m/035br4");
		
		return body;
	}
    	
	private Target buildTarget() {
		
		Target target = new ImageTarget();

		target.setTargetType("oa:SpecificResource");
		
		target.setContentType("image/jpeg");
		target.setHttpUri("http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE");
		target.setEuropeanaId(TEST_EUROPEANA_ID);
		
		Rectangle selector = new SvgRectangleSelector();
		target.setSelector((Selector)selector);
		
		InternetResource source = new BaseInternetResource();
		source.setContentType("text/html");
		source.setHttpUri("http://europeana.eu/portal/record//15502/GG_8285.html");
		source.setMediaType("dctypes:Text");
		target.setSource(source);
		
		return target;
	}

	protected BaseObjectTag createBaseObjectTagInstance() {
		
		BaseObjectTag baseObjectTag = new BaseObjectTag();
		
		baseObjectTag.setType("oa:Annotation");
        baseObjectTag.setAnnotatedAt(AnnotationLd.convertStrToDate("2012-11-10T09:08:07"));
        baseObjectTag.setSerializedAt(AnnotationLd.convertStrToDate("2012-11-10T09:08:07"));

        // set target
		Target target = buildTarget();
		baseObjectTag.setTarget(target);
			
		//set Body
		String comment = "Vlad Tepes";
		TagBody body = buildSemanticTagBody(comment, "ro");
		baseObjectTag.setBody(body);
				
		// set annotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("annonymous web user");
//		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		baseObjectTag.setAnnotatedBy(creator);
		
		// set serializedBy
		creator = new SoftwareAgent();
		creator.setAgentType(AgentTypes.SOFTWARE_AGENT.name());
//		creator.setAgentType("prov:SoftwareAgent");
		creator.setName("Annotorious");
		creator.setHomepage("http://annotorious.github.io/");
		baseObjectTag.setSerializedBy(creator);
				
		// motivation
		baseObjectTag.setMotivatedBy("oa:tagging");
		
		// set styledBy
		Style style = new BaseStyle();
		style.setMediaType("oa:CssStyle");
		style.setContentType("annotorious-popup");
		style.setValue("http://annotorious.github.io/latest/themes/dark/annotorious-dark.css");
		baseObjectTag.setStyledBy(style);
		
		return baseObjectTag;
	}
	 
	protected BaseObjectTag createEmptyBaseObjectTagInstance() {
		
		BaseObjectTag baseObjectTag = new BaseObjectTag();
		
		baseObjectTag.setType(null);
        baseObjectTag.setAnnotatedAt(null);
        baseObjectTag.setSerializedAt(null);

        // set target
		baseObjectTag.setTarget(null);
			
		//set Body
		TagBody body = buildSemanticTagBody(null, null);
		baseObjectTag.setBody(body);
				
		// set annotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName(null);
		baseObjectTag.setAnnotatedBy(creator);
		
		// set serializedBy
		creator = new SoftwareAgent();
		creator.setAgentType(AgentTypes.SOFTWARE_AGENT.name());
		creator.setName(null);
		creator.setHomepage(null);
		baseObjectTag.setSerializedBy(creator);
				
		// motivation
		baseObjectTag.setMotivatedBy(null);
		
		// set styledBy
		Style style = new BaseStyle();
		style.setMediaType(null);
		style.setContentType(null);
		style.setValue(null);
		baseObjectTag.setStyledBy(style);
		
		return baseObjectTag;
	}
	 
        
    /**
     * This test checks AnnotationLd created from an empty Annotation object.
     */
    @Test
    public void testCreateEmptyAnnotationLd() {
    	
        BaseObjectTag baseObjectTag = createEmptyBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\"},\"body\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\"},\"styledBy\":},\"target\":]}";

        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\"\n    },\n    \"styledBy\": \n    },\n    \"target\": \n    ]\n}";

        assertEquals(expectedIndent, actualIndent);
    }
            
    /**
     * This test converts Annotation object to AnnotationLd
     * object that implements JsonLd format.
     */
    @Test
    public void testAnnotationToAnnotationLd() {
    	
        BaseObjectTag baseObjectTag = createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource]\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"text/html\",\"format\":\"dctypes:Text\"}},\"type\":\"oa:Annotation\"}";

        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        }\n    },\n    \"type\": \"oa:Annotation\"\n}";

        assertEquals(expectedIndent, actualIndent);
    }
            
    @Test
    public void testGsonSerializationForAnnotationLd() {
    	
        BaseObjectTag baseObjectTag = createBaseObjectTagInstance();        
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
    	
        BaseObjectTag baseObjectTag = createBaseObjectTagInstance();        
        AnnotationLd annotationLd = new AnnotationLd(baseObjectTag);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource]\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"text/html\",\"format\":\"dctypes:Text\"}},\"type\":\"oa:Annotation\"}";

        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        }\n    },\n    \"type\": \"oa:Annotation\"\n}";

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
    
}
