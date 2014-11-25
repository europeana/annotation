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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;

/**
 *
 */
public class JsonLdAnnotationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGsonDeserializationFromJsonLd() {
    	
        String jsonLd = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"hasBody\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"hasTarget\":{\"@type\":\"[oa:SpecificResource]\",\"hasSelector\":{\"@type\":\"\"},\"hasSource\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"dctypes:Text\",\"format\":\"text/html\"}},\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"prov:SoftwareAgent\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"hasSource\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"}}";
        Gson gson = new Gson();
        JsonLd jsonLdObject = gson.fromJson(jsonLd, JsonLd.class);
        String actual = jsonLdObject.toString();
        toConsole(actual);

        assertEquals(jsonLd, actual);        
    }
    
    @Test
    public void testAnnotationToJsonLd() {
    	
    	SolrAnnotationImpl annotation = new SolrAnnotationImpl();
		annotation.setAnnotationIdString("/testCollection/TestObject/1");
		annotation.setLanguage("EN");
		annotation.setLabel("test annotation");
    	
        JsonLd jsonLd = new JsonLd();
        jsonLd.setUseTypeCoercion(false);
        jsonLd.setUseCuries(true);
        jsonLd.addNamespacePrefix("http://www.w3.org/ns/oa-context-20130208.json", "oa");

        JsonLdResource jsonLdResource = new JsonLdResource();
        jsonLdResource.setSubject("");
        jsonLdResource.addType("oa:Annotation");
        jsonLdResource.putProperty("annotatedAt", "2012-11-10T09:08:07");
        JsonLdProperty annotatedByProperty = new JsonLdProperty("annotatedBy");
        JsonLdPropertyValue v1 = new JsonLdPropertyValue();
        
        v1.setType("http://xmlns.com/foaf/0.1/person");
        v1.getValues().put("name", "annonymous web user");
        annotatedByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(annotatedByProperty);
        
        jsonLdResource.putProperty("serializedAt", "2012-11-10T09:08:07");
        
        JsonLdProperty serializedByProperty = new JsonLdProperty("serializedBy");
        v1 = new JsonLdPropertyValue();
        
        v1.setType("prov:SoftwareAgent");
        v1.getValues().put("name", "Annotorious");
        v1.getValues().put("foaf:homepage", "http://annotorious.github.io/");
        serializedByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(serializedByProperty);
        
        jsonLdResource.putProperty("motivatedBy", "oa:tagging");

        JsonLdProperty styledByProperty = new JsonLdProperty("styledBy");
        v1 = new JsonLdPropertyValue();
        
        v1.setType("oa:CssStyle");
        v1.getValues().put("hasSource", "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css");
        v1.getValues().put("styleClass", "annotorious-popup");
        styledByProperty.addValue(v1);        
        
        jsonLdResource.putProperty(styledByProperty);
        			
        JsonLdProperty hasBodyProperty = new JsonLdProperty("hasBody");
        v1 = new JsonLdPropertyValue();
        
        v1.addType("oa:Tag");
        v1.addType("cnt:ContentAsText");
        v1.addType("dctypes:Text");
        v1.getValues().put("chars", "Vlad Tepes");
        v1.getValues().put("dc:language", "ro");
        v1.getValues().put("format", "text/plain");
        hasBodyProperty.addValue(v1);        

        JsonLdPropertyValue v2 = new JsonLdPropertyValue();
        
        v2.addType("oa:SemanticTag");
        v2.getValues().put("foaf:page", "https://www.freebase.com/m/035br4");
        hasBodyProperty.addValue(v2);        
        
        jsonLdResource.putProperty(hasBodyProperty);

        JsonLdProperty hasTargetProperty = new JsonLdProperty("hasTarget");
        v1 = new JsonLdPropertyValue();
        
        v1.addType("oa:SpecificResource");

        JsonLdProperty hasSourceProperty = new JsonLdProperty("hasSource");
        JsonLdPropertyValue v3 = new JsonLdPropertyValue();
        
        v3.setType("dctypes:Text");
        v3.getValues().put("@id", "http://europeana.eu/portal/record//15502/GG_8285.html");
        v3.getValues().put("format", "text/html");
        hasSourceProperty.addValue(v3);        
        v1.putProperty(hasSourceProperty);
        
        JsonLdProperty hasSelectorProperty = new JsonLdProperty("hasSelector");
        JsonLdPropertyValue v4 = new JsonLdPropertyValue();
        v4.setType(""); // if property is empty - set empty type
        
        hasSelectorProperty.addValue(v4);        
        v1.putProperty(hasSelectorProperty);
        
        hasTargetProperty.addValue(v1);        
        
        jsonLdResource.putProperty(hasTargetProperty);

        
        jsonLd.put(jsonLdResource);

        String actual = jsonLd.toString();
        toConsole(actual);
        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@type\":\"oa:Annotation\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"hasBody\":[{\"@type\":\"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"@type\":\"[oa:SemanticTag]\",\"foaf:page\":\"https://www.freebase.com/m/035br4\"}],\"hasTarget\":{\"@type\":\"[oa:SpecificResource]\",\"hasSelector\":{\"@type\":\"\"},\"hasSource\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"@type\":\"dctypes:Text\",\"format\":\"text/html\"}},\"motivatedBy\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"prov:SoftwareAgent\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"hasSource\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"}}";

        assertEquals(expected, actual);
        
        String actualIndent = jsonLd.toString(4);
        toConsole(actualIndent);
        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"oa:Annotation\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"hasBody\": [\n        {\n            \"@type\": \"[oa:Tag, cnt:ContentAsText, dctypes:Text]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"@type\": \"[oa:SemanticTag]\",\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\"\n        }\n    ],\n    \"hasTarget\": {\n        \"@type\": \"[oa:SpecificResource]\",\n        \"hasSelector\": {\n            \"@type\": \"\"\n        },\n        \"hasSource\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"@type\": \"dctypes:Text\",\n            \"format\": \"text/html\"\n        }\n    },\n    \"motivatedBy\": \"oa:tagging\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"prov:SoftwareAgent\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"hasSource\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    }\n}";

        assertEquals(expectedIndent, actualIndent);
    }
    
    private void toConsole(String actual) {
        System.out.println(actual);
        String s = actual;
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replace("\"", "\\\"");
        s = s.replace("\n", "\\n");
        System.out.println(s);
    }
}
