package eu.europeana.annotation.client;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public class EuropeanaLdSearchApiTest extends EuropeanaLdApiTest {
	
	@Test
	public void searchSemanticTagHistoryPinAnnotationByTarget() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, semanticTagAnnotation
				);
		annotationStr = europeanaLdApi.searchLd(
				TEST_TARGET
				, null
				);
		System.out.println("historypin search annotation by target test: " + annotationStr);
		assertNotNull(annotationStr);
		verifySearchResult(annotationStr);
	}

	@Test
	public void searchSemanticTagHistoryPinAnnotationByResourceId() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleTagAnnotation
				);
		annotationStr = europeanaLdApi.searchLd(
				null
				, TEST_RESOURCE_ID
				);
		System.out.println("historypin search annotation by resourceId test: " + annotationStr);
		assertNotNull(annotationStr);
		verifySearchResult(annotationStr);
	}

	@Test
	public void searchObjectLinkingHistoryPinAnnotationByTarget() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		annotationStr = europeanaLdApi.searchLd(
				TEST_TARGET
				, null
				);
		System.out.println("historypin search annotation by target test: " + annotationStr);
		assertNotNull(annotationStr);
		verifySearchResult(annotationStr);
	}

	@Test
	public void searchObjectLinkingHistoryPinAnnotationByResourceId() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		annotationStr = europeanaLdApi.searchLd(
				null
				, TEST_RESOURCE_ID
				);
		System.out.println("historypin search annotation by resourceId test: " + annotationStr);
		assertNotNull(annotationStr);
		verifySearchResult(annotationStr);
	}

	@Test
	public void searchAnnotationsWithMultipleTargetsByEachTarget() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		List<Long> idList = new ArrayList<Long>();
		Iterator<String> itr = annotation.getTarget().getValues().iterator();
		while (itr.hasNext()) {
			String target = itr.next();
			String annotationStrRes = europeanaLdApi.searchLd(
					target
					, null
					);
			System.out.println("historypin search annotation by target test: " + annotationStrRes);
			assertNotNull(annotationStrRes);
			Annotation annotationRes = europeanaParser.parseAnnotation(annotationStrRes);
			idList.add(annotationRes.getAnnotationId().getAnnotationNr());
		}
		assertTrue(idList.contains(annotationNr));
	}
	
	@Test
	public void searchAnnotationsWithMultipleTargetsByEachResearchId() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				WebAnnotationFields.PROVIDER_HISTORY_PIN
				, annotationNr
				, simpleLinkAnnotation
				);
		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
		List<Long> idList = new ArrayList<Long>();
		Iterator<String> itr = annotation.getTarget().getResourceIds().iterator();
		while (itr.hasNext()) {
			String resourceId = itr.next();
			String annotationStrRes = europeanaLdApi.searchLd(
					null
					, resourceId
					);
			System.out.println("historypin search annotation by target test: " + annotationStrRes);
			assertNotNull(annotationStrRes);
			Annotation annotationRes = europeanaParser.parseAnnotation(annotationStrRes);
			idList.add(annotationRes.getAnnotationId().getAnnotationNr());
		}
		assertTrue(idList.contains(annotationNr));
	}
	
	
}
