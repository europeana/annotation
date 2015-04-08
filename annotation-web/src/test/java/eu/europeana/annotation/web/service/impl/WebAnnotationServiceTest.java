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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.definitions.model.test.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.jsonld.AnnotationLdTest;
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.controller.AnnotationControllerHelper;


/**
 * Unit test for the Web Annotation service
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml", "/annotation-mongo-test.xml", "/annotation-solr-test.xml" })
public class WebAnnotationServiceTest extends AnnotationTestObjectBuilder{

	@Resource 
	AnnotationService webAnnotationService;
	
	@Rule public ExpectedException thrown= ExpectedException.none();
	
	@Test
	public void testStoreAnnotationInDbRetrieveAndSerialize() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		

		/**
		 * Serialize an original Annotation test object.
		 */
        AnnotationLd origAnnotationLd = new AnnotationLd(testAnnotation);
        
        String original = origAnnotationLd.toString();
        AnnotationLd.toConsole("", original);
        String expectedOrig = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@id\":\"http://data.europeana.eu/annotations/testCollection/testObject/webanno/-1\",\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[SOFTWARE_AGENT,foaf:Person,euType:SOFTWARE_AGENT]\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"motivatedBy\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"[SOFTWARE_AGENT,prov:SoftwareAgent,euType:SOFTWARE_AGENT]\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"[oa:CssStyle,euType:CSS]\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:SpecificResource,euType:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//testCollection/testObject.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"targetType\":\"[oa:SpecificResource,euType:IMAGE]\"},\"type\":\"OBJECT_TAG\"}";
        
        assertEquals(expectedOrig, original);
		
        String origIndent = origAnnotationLd.toString(4);
        AnnotationLd.toConsole("", origIndent);
        
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation webAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null && webAnnotation.getAnnotationId().toString().length() > 0);
		assertEquals(testAnnotation.getBody(), webAnnotation.getBody());
		assertEquals(testAnnotation.getTarget(), webAnnotation.getTarget());
		
		/**
		 * Serialize Annotation object that was retrieved from a database.
		 */
        AnnotationLd annotationLd = new AnnotationLd(webAnnotation);
        
        String actual = annotationLd.toString();
        AnnotationLd.toConsole("", actual);
        
        String actualIndent = annotationLd.toString(4);
        AnnotationLd.toConsole("", actualIndent);
        
        /**
         * read Annotation object from the serialized AnnotationLd object.
         */
        Annotation annotationFromAnnotationLd = annotationLd.getAnnotation();
        
        /**
         * Compare original Annotation object with retrieved serialized Annotation object.
         */     
        // Original object does not have EuropeanaUri
//        testAnnotation.getTarget().setEuropeanaId(annotationFromAnnotationLd.getTarget().getEuropeanaId());
        assertEquals(testAnnotation, annotationFromAnnotationLd);        
	}
		
	@Test
	public void testCreateAnnotationWebanno() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		

		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation webAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null 
			&& webAnnotation.getAnnotationId().toString().contains( 
				WebAnnotationFields.ANNOTATION_ID_PREFIX 
				+ AnnotationTestObjectBuilder.TEST_EUROPEANA_ID
				+ WebAnnotationFields.SLASH 
				+ WebAnnotationFields.PROVIDER_WEBANNO 
				+ WebAnnotationFields.SLASH )
			);
	}

	/**
	 * Create a test annotation object.
	 * @return Annotation
	 */
	Annotation createTestAnnotation() {
		Annotation testAnnotation = createBaseObjectTagInstance();
	    MongoAnnotationId mongoAnnotationId = (new AnnotationControllerHelper())
	    		.initAnnotationId(AnnotationTestObjectBuilder.TEST_EUROPEANA_ID, null);
		testAnnotation.setAnnotationId(mongoAnnotationId);
		return testAnnotation;
	}

	@Test
	public void testCreateAnnotationHistorypin() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = createBaseObjectTagInstanceWithSameAs(
				WebAnnotationFields.TEST_HISTORYPIN_URL);
	    MongoAnnotationId mongoAnnotationId = (new AnnotationControllerHelper()).initAnnotationId(
	    		AnnotationTestObjectBuilder.TEST_EUROPEANA_ID, WebAnnotationFields.PROVIDER_HISTORY_PIN);
	    mongoAnnotationId.setAnnotationNr(1);
		testAnnotation.setAnnotationId(mongoAnnotationId);		

		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation webAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null 
				&& webAnnotation.getAnnotationId().toString().contains( 
					WebAnnotationFields.ANNOTATION_ID_PREFIX 
					+ AnnotationTestObjectBuilder.TEST_EUROPEANA_ID
					+ WebAnnotationFields.SLASH 
					+ WebAnnotationFields.PROVIDER_HISTORY_PIN
					+ WebAnnotationFields.SLASH )
				);
	}
		
	@Test
	public void testCreateAnnotationWrongProvider() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		thrown.expect( AnnotationValidationException.class );
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();
	    MongoAnnotationId mongoAnnotationId = (new AnnotationControllerHelper()).initAnnotationId(
	    		AnnotationTestObjectBuilder.TEST_EUROPEANA_ID, WebAnnotationFields.PROVIDER_WRONG);
		testAnnotation.setAnnotationId(mongoAnnotationId);		

		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		webAnnotationService.storeAnnotation(persistentAnnotation);

	}
		
	@Test
	public void testMultilingualAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
		
		testAnnotation.getBody().addLabelInMapping(
				"ro", AnnotationLdTest.TEST_RO_VALUE);
//		SolrAnnotationConst.SolrAnnotationLanguages.RO.getSolrAnnotationLanguage(), AnnotationLdTest.TEST_RO_VALUE);
		testAnnotation.getBody().addLabelInMapping(
				"en", AnnotationLdTest.TEST_EN_VALUE);
//		SolrAnnotationConst.SolrAnnotationLanguages.EN.getSolrAnnotationLanguage(), AnnotationLdTest.TEST_EN_VALUE);

		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
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
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getAnnotationId() != null && webAnnotation.getAnnotationId().toString().length() > 0);
        assertTrue(webAnnotation.getBody().getMultilingual().containsValue(AnnotationLdTest.TEST_EN_VALUE) 
        		&& webAnnotation.getBody().getMultilingual().containsValue(AnnotationLdTest.TEST_RO_VALUE));
//		assertEquals(testAnnotation, webAnnotation);
		assertEquals(testAnnotation.getBody(), webAnnotation.getBody());
	}
		
	@Test
	public void testDeleteAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Delete Annotation.
		 */
		webAnnotationService.deleteAnnotation(
				storedAnnotation.getAnnotationId().getResourceId()
				, storedAnnotation.getAnnotationId().getProvider()
				, storedAnnotation.getAnnotationId().getAnnotationNr());
		
		/**
		 * Search Annotation.
		 */
		List<? extends Annotation> resList = webAnnotationService.searchAnnotations(
				((PersistentAnnotation) storedAnnotation).getId().toString(), "0", "10");
		assertTrue(resList.size() == 0);
	}
		
	@Test
	public void testIndexAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Search Annotation.
		 */
		List<? extends Annotation> originalList = webAnnotationService.searchAnnotations(
				testAnnotation.getBody().getValue(), "0", "10");

		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Reindex Annotation.
		 */
		webAnnotationService.indexAnnotation(
				storedAnnotation.getAnnotationId().getResourceId()
				, storedAnnotation.getAnnotationId().getProvider()
				, storedAnnotation.getAnnotationId().getAnnotationNr());
		
		/**
		 * Search Annotation.
		 */
		List<? extends Annotation> resList = webAnnotationService.searchAnnotations(
				storedAnnotation.getBody().getValue(), "0", "10");
		assertTrue(resList.size() == originalList.size());
	}
		
	@Test
	public void testDisableAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Reindex Annotation.
		 */
		Annotation disabledAnnotation = webAnnotationService.disableAnnotation(
				storedAnnotation.getAnnotationId().getResourceId()
				, storedAnnotation.getAnnotationId().getProvider()
				, storedAnnotation.getAnnotationId().getAnnotationNr());
		
		/**
		 * Search Annotation.
		 */
		assertTrue(disabledAnnotation.isDisabled() == true);
	}
		
	@Test
	public void testDeleteTag() 
			throws MalformedURLException, IOException, TagServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Convert the test annotation object to the PersistentAnnotation object type.
		 */
		AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();
		Annotation persistentAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(persistentAnnotation);
		
		/**
		 * Delete Annotation.
		 */
		webAnnotationService.deleteAnnotation(
				storedAnnotation.getAnnotationId().getResourceId()
				, storedAnnotation.getAnnotationId().getProvider()
				, storedAnnotation.getAnnotationId().getAnnotationNr());
		
		/**
		 * Delete Annotation.
		 */
//		webAnnotationService.deleteTag(((PersistentAnnotation) storedAnnotation).getId().toString());
		
		/**
		 * Search Tag by TagId.
		 */
		List<? extends TagResource> resList = null;
		resList = webAnnotationService.searchTags(
				((PersistentAnnotation) storedAnnotation).getId().toString());
		assertTrue(resList.size() == 0);
	}
		
}
