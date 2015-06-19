package eu.europeana.annotation.client.integration.search;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Iterator;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


public class EuropeanaLdSearchApiTest extends BaseJsonLdApiTest {
	
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
		String resJson = "";
//		List<Long> idList = new ArrayList<Long>();
		Iterator<String> itr = annotation.getTarget().getValues().iterator();
		while (itr.hasNext()) {
			String target = itr.next();
			String annotationStrRes = europeanaLdApi.searchLd(
					target
					, null
					);
			System.out.println("historypin search annotation by target test: " + annotationStrRes);
			assertNotNull(annotationStrRes);
			resJson = resJson + annotationStrRes;
//			AnnotationSearchResults asr = europeanaLdApi.getAnnotationSearchResults(annotationStrRes);
//			List<Annotation> annotationList = asr.getItems();
//			Iterator<Annotation> itrAnnotationList = annotationList.iterator();
//			while (itrAnnotationList.hasNext()) {
//				Annotation anno = itrAnnotationList.next();
//				idList.add(anno.getAnnotationId().getAnnotationNr());
//			}
		}
		assertTrue(resJson.contains("\"" + WebAnnotationFields.ANNOTATION_NR + "\":" + annotationNr));
//		assertTrue(idList.contains(annotationNr));
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
		Iterator<String> itr = annotation.getTarget().getResourceIds().iterator();
		while (itr.hasNext()) {
			String resourceId = itr.next();
			String annotationStrRes = europeanaLdApi.searchLd(
					null
					, resourceId
					);
			System.out.println("historypin search annotation by target test: " + annotationStrRes);
			assertNotNull(annotationStrRes);
			assertTrue(annotationStrRes.contains(resourceId));
		}
	}
	
	
}
