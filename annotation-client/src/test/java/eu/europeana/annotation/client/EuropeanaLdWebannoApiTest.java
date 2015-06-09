package eu.europeana.annotation.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;


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
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		System.out.println("historypin annotation test: " + annotationStr);
		
		assertEquals(WebAnnotationFields.PROVIDER_WEBANNO, annotation.getAnnotationId().getProvider());
		assertTrue(annotation.getAnnotationId().getAnnotationNr() != null);
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
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
		assertTrue(annotationStr.contains(WebAnnotationFields.INVALID_PROVIDER));
	}
	
}
