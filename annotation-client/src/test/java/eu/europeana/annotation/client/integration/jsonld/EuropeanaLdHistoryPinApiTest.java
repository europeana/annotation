package eu.europeana.annotation.client.integration.jsonld;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

//disabled as the rest interface is disabled 
@Ignore
public class EuropeanaLdHistoryPinApiTest extends BaseJsonLdApiTest {
	
	@Test
	public void createSimpleTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.TAGGING.name()
				, providerHistoryPin
				, annotationNr
				, simpleTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyInternalTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createMultipleSimpleTagsHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.TAGGING.name()
				, providerHistoryPin
				, annotationNr
				, multipleSimpleTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyInternalTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createSemanticTagHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.TAGGING.name()
				, providerHistoryPin
				, annotationNr
				, semanticTagAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertTrue(BodyInternalTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void createSimpleLinkHistoryPinAnnotation() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.LINKING.name()
				, providerHistoryPin
				, annotationNr
				, simpleLinkAnnotation
				);
		assertNotNull(annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(providerHistoryPin, annotationNr, annotation);
		assertEquals(AnnotationTypes.OBJECT_LINKING.name(),  annotation.getInternalType());
		assertTrue(2 == annotation.getTarget().getValues().size());
	}
	
	@Test
	public void getSemanticTagHistoryPinAnnotationByProviderAndAnnotationNumber() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.TAGGING.name()
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, semanticTagAnnotation
				);
		annotationStr = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(WebAnnotationFields.PROVIDER_HISTORY_PIN, annotationNr, annotation);
		assertTrue(BodyInternalTypes.isTagBody(annotation.getBody().getInternalType()));
	}
	
	@Test
	public void getObjectLinkingHistoryPinAnnotationByProviderAndAnnotationNumber() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.LINKING.name()
				, WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		annotationStr = europeanaLdApi.getAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				);
		System.out.println("historypin get annotation test: " + annotationStr);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
		validateAnnotation(WebAnnotationFields.PROVIDER_HISTORY_PIN, annotationNr, annotation);
	}
	
	@Test
	public void createSimpleTagHistoryPinAnnotationWithoutAnnotationNr() throws JsonParseException {
		
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.TAGGING.name()
				, providerHistoryPin
				, null
				, simpleTagAnnotation
				);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		//TODO: move the test values from WebAnnotationFields. Use the values from the Exception.MESSAGE_(eventualy move exceptions to definitions)...
		//assertTrue(annotationStr.contains(WebAnnotationFields.INVALID_ANNOTATION_NR));
	}
		
}
