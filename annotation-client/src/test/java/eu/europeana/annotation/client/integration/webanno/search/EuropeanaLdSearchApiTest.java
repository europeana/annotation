package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

//import org.junit.jupiter.api.Test;

/**
 * TODO: migrate when the search functionality is enabled 
 * @Deprecated
 * @author Sergiu Gordea @ait
 */
@Deprecated
public class EuropeanaLdSearchApiTest extends BaseJsonLdApiTest {
	
//	@Test
	public void searchAnnotationsWithMultipleTargetsByEachTarget() throws JsonParseException, IOException {
		
		String requestBody = getJsonStringInput(SIMPLE_LINK_ANNOTATION);
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.LINKING.name()
				, annotationNr
				, requestBody
				);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
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
		assertTrue(resJson.contains("\"" + WebAnnotationFields.IDENTIFIER + "\":" + annotationNr));
//		assertTrue(idList.contains(annotationNr));
	}
	
//	@Test
	public void searchAnnotationsWithMultipleTargetsByEachResearchId() throws JsonParseException, IOException {
		
		String requestBody = getJsonStringInput(SIMPLE_LINK_ANNOTATION);

		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				MotivationTypes.LINKING.name()
				, annotationNr
				, requestBody
				);
		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
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
