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
import eu.europeana.annotation.jsonld.AnnotationTestObjectBuilder;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.controller.AnnotationControllerHelper;


/**
 * Unit test for the Web Annotation service
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-web-context.xml", "/annotation-mongo-test.xml" })
public class WebAnnotationServiceTest {

	@Resource 
	AnnotationService webAnnotationService;
	
	@Test
	public void testStoreAnnotationInDb() throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance();

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
		Annotation persistantAnnotation = controllerHelper
				.copyIntoPersistantAnnotation(testAnnotation);
		
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.createAnnotation(persistantAnnotation);
		
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
		} else {
		    if (webAnnotation.getType().equals(AnnotationTypes.OBJECT_TAG.name())) {
		    	webAnnotation.setType(WebAnnotationFields.OA_ANNOTATION);
		    }
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
	}
		
}
