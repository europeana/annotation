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


public class EuropeanaLdHistoryPinApiTest extends EuropeanaLdApiTest {
	
//	@Test
	public void createSimpleTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, simpleTagAnnotation
				);
		assertNotNull(annotationStr);
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		System.out.println("historypin annotation test: " + annotationStr);
		
		assertEquals(providerHistoryPin, annotation.getAnnotationId().getProvider());
		assertEquals((Long)annotationNr, annotation.getAnnotationId().getAnnotationNr());
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
//	@Test
	public void createMultipleSimpleTagsHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, multipleSimpleTagAnnotation
				);
		assertNotNull(annotationStr);
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		System.out.println("historypin annotation test: " + annotationStr);
		
		assertEquals(providerHistoryPin, annotation.getAnnotationId().getProvider());
		assertEquals((Long)annotationNr, annotation.getAnnotationId().getAnnotationNr());
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
//	@Test
	public void createSemanticTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, semanticTagAnnotation
				);
		assertNotNull(annotationStr);
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		System.out.println("historypin annotation test: " + annotationStr);
		
		assertEquals(providerHistoryPin, annotation.getAnnotationId().getProvider());
		assertEquals((Long)annotationNr, annotation.getAnnotationId().getAnnotationNr());
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
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		
		System.out.println("Annotation URI: " + annotation.getAnnotationId().toUri());
		System.out.println("historypin annotation test: " + annotationStr);
		
		assertEquals(providerHistoryPin, annotation.getAnnotationId().getProvider());
		assertEquals((Long)annotationNr, annotation.getAnnotationId().getAnnotationNr());
		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	//@Test
	public void getHistoryPinAnnotationByAnnotationNumber() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleTagAnnotation
				);
		annotation = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotation);
		assertNotNull(annotation);
	}
	
	//@Test
	public void searchHistoryPinAnnotationByTarget() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleTagAnnotation
				);
		annotation = europeanaLdApi.searchLd(
				TEST_TARGET
				, null
				);
		System.out.println("historypin search annotation by target test: " + annotation);
		assertNotNull(annotation);
	}
	
	//@Test
	public void searchHistoryPinAnnotationByResourceId() {
		
		long annotationNr = System.currentTimeMillis();
		String annotation = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleTagAnnotation
				);
		annotation = europeanaLdApi.searchLd(
				null
				, TEST_RESOURCE_ID
				);
		System.out.println("historypin search annotation by resourceId test: " + annotation);
		assertNotNull(annotation);
	}

	
}
