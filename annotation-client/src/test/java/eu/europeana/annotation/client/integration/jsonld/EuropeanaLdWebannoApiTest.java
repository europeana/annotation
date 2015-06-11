package eu.europeana.annotation.client.integration.jsonld;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.client.abstracts.EuropeanaLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public class EuropeanaLdWebannoApiTest extends EuropeanaLdApiTest {
	
	@Test
	public void createSimpleTagWebannoAnnotation() throws JsonParseException {
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_WEBANNO
				, null
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(WebAnnotationFields.PROVIDER_WEBANNO, -1, annotation);
	}
	
	@Test
	public void createSimpleTagAnnotationWithoutProvider() throws JsonParseException {
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				null
				, null
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		assertTrue(annotationStr.contains(WebAnnotationFields.INVALID_PROVIDER));
	}
	
	@Test
	public void createSimpleTagWebannoAnnotationWithAnnotationNr() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_WEBANNO
				, annotationNr
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		assertTrue(annotationStr.contains(WebAnnotationFields.UNNECESSARY_ANNOTATION_NR));
	}
	
}
