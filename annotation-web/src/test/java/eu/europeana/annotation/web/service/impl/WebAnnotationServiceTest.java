/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.annotation.web.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
//import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.jsonld.AnnotationLdTest;
import eu.europeana.annotation.jsonld.AnnotationTestObjectBuilder;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.controller.AnnotationControllerHelper;


/**
 * Unit test for the Web Annotation service
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml", "/annotation-mongo-test.xml", "/annotation-solr-test.xml" })
public class WebAnnotationServiceTest {

	@Resource 
	AnnotationService webAnnotationService;
	
	@Test
	public void testStoreAnnotationInDbRetrieveAndSerialize() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance();

		/**
		 * Serialize an original Annotation test object.
		 */
        AnnotationLd origAnnotationLd = new AnnotationLd(testAnnotation);
        
        String original = origAnnotationLd.toString();
        AnnotationLd.toConsole("", original);
        String expectedOrig = "{\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expectedOrig, original);
		
        String origIndent = origAnnotationLd.toString(4);
        AnnotationLd.toConsole("", origIndent);
        
		/**
		 * Check types and replace if necessary 
		 */
		if (testAnnotation.getType().equals(WebAnnotationFields.OA_ANNOTATION)) {
			testAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		if (testAnnotation.getMotivatedBy().equals(WebAnnotationFields.OA_TAGGING)) {
			testAnnotation.setMotivatedBy(MotivationTypes.TAGGING.name());
		}
		
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.createAnnotation(persistentAnnotation);
		
		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation webAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);
		
		/**
		 * Put original types if necessary 
		 */
//		AnnotationRest.putOriginalTypes(webAnnotation);
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
//		} else {
//		    if (webAnnotation.getType().equals(AnnotationTypes.OBJECT_TAG.name())) {
//		    	webAnnotation.setType(WebAnnotationFields.OA_ANNOTATION);
//		    }
		}
		if (StringUtils.isBlank(webAnnotation.getMotivatedBy())) {
			webAnnotation.setMotivatedBy(WebAnnotationFields.OA_TAGGING);
		} else {
			if (webAnnotation.getMotivatedBy().equals(MotivationTypes.TAGGING.name())) {
				webAnnotation.setMotivatedBy(WebAnnotationFields.OA_TAGGING);
			}
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null && webAnnotation.getAnnotationId().toString().length() > 0);
//		assertEquals(testAnnotation, webAnnotation);
//		assertEquals(testAnnotation.toString(), webAnnotation.toString());
		assertEquals(testAnnotation.getBody(), webAnnotation.getBody());
		assertEquals(testAnnotation.getTarget(), webAnnotation.getTarget());
		
		/**
		 * Serialize Annotation object that was retrieved from a database.
		 */
        AnnotationLd annotationLd = new AnnotationLd(webAnnotation);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        // annotationId is always different
//        String expected = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@id\":\"/testCollection/testObject/33\",\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@type\":\"http://xmlns.com/foaf/0.1/person\",\"name\":\"annonymous web user\"},\"body\":[{\"bodyType\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"dc:language\":\"ro\",\"format\":\"text/plain\"},{\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"mediaType\":\"oa:SemanticTag\"}],\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@type\":\"SOFTWARE_AGENT\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CssStyle\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:TARGET#IMAGE]\"},\"type\":\"OBJECT_TAG\"}";
//        assertEquals(expected, actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
//        String expectedIndent = "{\n    \"@context\": {\n        \"oa\": \"http://www.w3.org/ns/oa-context-20130208.json\"\n    },\n    \"@type\": \"OBJECT_TYPE\",\n    \"annotatedAt\": \"2012-11-10T09:08:07\",\n    \"annotatedBy\": {\n        \"@type\": \"http://xmlns.com/foaf/0.1/person\",\n        \"name\": \"annonymous web user\"\n    },\n    \"body\": [\n        {\n            \"bodyType\": \"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:BODY#SEMANTIC_TAG]\",\n            \"chars\": \"Vlad Tepes\",\n            \"dc:language\": \"ro\",\n            \"format\": \"text/plain\"\n        },\n        {\n            \"foaf:page\": \"https://www.freebase.com/m/035br4\",\n            \"mediaType\": \"oa:SemanticTag\"\n        }\n    ],\n    \"motivatedBy\": \"TAGGING\",\n    \"serializedAt\": \"2012-11-10T09:08:07\",\n    \"serializedBy\": {\n        \"@type\": \"SOFTWARE_AGENT\",\n        \"foaf:homepage\": \"http://annotorious.github.io/\",\n        \"name\": \"Annotorious\"\n    },\n    \"styledBy\": {\n        \"@type\": \"oa:CssStyle\",\n        \"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\n        \"styleClass\": \"annotorious-popup\"\n    },\n    \"target\": {\n        \"@type\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\",\n        \"contentType\": \"image/jpeg\",\n        \"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\n        \"selector\": {\n            \"@type\": \"\"\n        },\n        \"source\": {\n            \"@id\": \"http://europeana.eu/portal/record//15502/GG_8285.html\",\n            \"contentType\": \"text/html\",\n            \"format\": \"dctypes:Text\"\n        },\n        \"targetType\": \"[oa:SpecificResource,euType:TARGET#IMAGE]\"\n    },\n    \"type\": \"OBJECT_TAG\"\n}";        
//        assertEquals(expectedIndent, actualIndent);
        
        /**
         * read Annotation object from the serialized AnnotationLd object.
         */
        Annotation annotationFromAnnotationLd = annotationLd.getAnnotation();
        
        /**
         * Compare original Annotation object with retrieved serialized Annotation object.
         */       
        assertEquals(testAnnotation, annotationFromAnnotationLd);        
	}
		
	@Test
	public void testMultilingualAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance();
		
		testAnnotation.getBody().addLabelInMapping(
				SolrAnnotationConst.SolrAnnotationLanguages.RO.getSolrAnnotationLanguage(), AnnotationLdTest.TEST_RO_VALUE);
		testAnnotation.getBody().addLabelInMapping(
				SolrAnnotationConst.SolrAnnotationLanguages.EN.getSolrAnnotationLanguage(), AnnotationLdTest.TEST_EN_VALUE);

//        String annotationJsonLdObjectString = 
//    		"{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"}," 
//    		+ "\"@type\":\"OBJECT_TYPE\",\"annotatedAt\":\"2012-11-10T09:08:07\","
//    		+ "\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},"
//    		+ "\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\","
//    		+ "\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\","
//    		+ "\"format\":\"text/plain\",\"language\":\"ro\","
//    		+ "\"multilingual\":\"[ro:Vlad Tepes,en:Vlad the Impaler]\"},"
//    		+ "\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\","
//    		+ "\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},"
//    		+ "\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},"
//    		+ "\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//15502/GG_8285.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},"
//    		+ "\"type\":\"oa:Annotation\"}";
//
//        /**
//         * parse JsonLd string using JsonLdParser.
//         * JsonLd string -> JsonLdParser -> JsonLd object -> AnnotationLd object
//         */
//        AnnotationLd parsedAnnotationLd = null;
//        JsonLd parsedJsonLd = null;
//        try {
//        	parsedJsonLd = JsonLdParser.parseExt(annotationJsonLdObjectString);
//        	parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        
//        String original = parsedAnnotationLd.toString();
//        AnnotationLd.toConsole("", original);      
//        assertTrue(original.contains(TEST_EN_VALUE) && original.contains(TEST_RO_VALUE));
//		
//        String origIndent = parsedAnnotationLd.toString(4);
//        AnnotationLd.toConsole("", origIndent);
//        
//        /**
//         * AnnotationLd object -> Annotation object.
//         */
//        Annotation annotationFromAnnotationLd = parsedAnnotationLd.getAnnotation();
//        System.out.println("annotationFromAnnotationLd: " + annotationFromAnnotationLd.toString());

        /**
		 * Check types and replace if necessary 
		 */
		if (testAnnotation.getType().equals(WebAnnotationFields.OA_ANNOTATION)) {
			testAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		if (testAnnotation.getMotivatedBy().equals(WebAnnotationFields.OA_TAGGING)) {
			testAnnotation.setMotivatedBy(MotivationTypes.TAGGING.name());
		}
//		if (annotationFromAnnotationLd.getType().equals(WebAnnotationFields.OA_ANNOTATION)) {
//			annotationFromAnnotationLd.setType(AnnotationTypes.OBJECT_TAG.name());
//		}
//		if (annotationFromAnnotationLd.getMotivatedBy().equals(WebAnnotationFields.OA_TAGGING)) {
//			annotationFromAnnotationLd.setMotivatedBy(MotivationTypes.TAGGING.name());
//		}
		
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
//		.copyIntoPersistantAnnotation(annotationFromAnnotationLd);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.createAnnotation(persistentAnnotation);
		
		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation webAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);
		
		/**
		 * Put original types if necessary 
		 */
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
//		if (StringUtils.isBlank(webAnnotation.getMotivatedBy())) {
//			webAnnotation.setMotivatedBy(WebAnnotationFields.OA_TAGGING);
//		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
//		System.out.println("annotationFromAnnotationLd: " + annotationFromAnnotationLd.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null && webAnnotation.getAnnotationId().toString().length() > 0);
        assertTrue(webAnnotation.getBody().getMultilingual().containsValue(AnnotationLdTest.TEST_EN_VALUE) 
        		&& webAnnotation.getBody().getMultilingual().containsValue(AnnotationLdTest.TEST_RO_VALUE));
		assertEquals(testAnnotation, webAnnotation);
		assertEquals(testAnnotation.getBody(), webAnnotation.getBody());
	}
		
//	@Test
//	public void testSearchAnnotationByLabel() 
//			throws MalformedURLException, IOException, AnnotationServiceException {
//		
//		/**
//		 * Search for Annotation in Solr by query.
//		 */
//		List<? extends Annotation> annotationList = webAnnotationService.getAnnotationByQuery(
//				WebAnnotationFields.DEFAULT_EURIPEANA_ID
//				, AnnotationLdTest.TEST_RO_VALUE);
//		
//		assertTrue(annotationList != null && annotationList.size() > 0);
//	}
				
}
