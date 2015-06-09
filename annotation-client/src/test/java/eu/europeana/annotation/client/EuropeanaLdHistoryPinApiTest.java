package eu.europeana.annotation.client;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;


public class EuropeanaLdHistoryPinApiTest extends EuropeanaLdApiTest {
	
	@Test
	public void createSimpleTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, simpleTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createMultipleSimpleTagsHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, multipleSimpleTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createSemanticTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, semanticTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createSimpleLinkHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, simpleLinkAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertEquals(AnnotationTypes.OBJECT_LINKING.name(),  annotation.getInternalType());
		assertTrue(2 == annotation.getTarget().getValues().size());
	}
	
	@Test
	public void getSemanticTagHistoryPinAnnotationByProviderAndAnnotationNumber() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, semanticTagAnnotation
				);
		annotationStr = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(WebAnnotationFields.PROVIDER_HISTORY_PIN, annotationNr, annotation);
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void getObjectLinkingHistoryPinAnnotationByProviderAndAnnotationNumber() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		annotationStr = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		validateAnnotation(WebAnnotationFields.PROVIDER_HISTORY_PIN, annotationNr, annotation);
	}
	
	@Test
	public void createSimpleTagHistoryPinAnnotationWithoutAnnotationNr() throws JsonParseException {
		
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, null
				, simpleTagAnnotation
				);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		assertTrue(annotationStr.contains(WebAnnotationFields.INVALID_ANNOTATION_NR));
	}
		
}
