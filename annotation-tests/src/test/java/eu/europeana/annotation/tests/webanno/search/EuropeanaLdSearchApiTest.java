package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseJsonLdApiTest;

//import org.junit.jupiter.api.Test;

/**
 * TODO: migrate when the search functionality is enabled 
 * @Deprecated
 * @author Sergiu Gordea @ait
 */
@Deprecated
public class EuropeanaLdSearchApiTest extends BaseJsonLdApiTest {
	
    Logger log = LogManager.getLogger(getClass());
    
//	@Test
	public void searchAnnotationsWithMultipleTargetsByEachTarget() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_LINK_ANNOTATION);
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = createAnnotationLd(
				MotivationTypes.LINKING.name()
				, annotationNr
				, requestBody
				, null
				);
		Annotation annotation = AnnotationTestUtils.parseAnnotation(annotationStr, null);
		String resJson = "";
//		List<Long> idList = new ArrayList<Long>();
		Iterator<String> itr = annotation.getTarget().getValues().iterator();
		while (itr.hasNext()) {
			String target = itr.next();
			String annotationStrRes = searchAnnotationLd(
					target
					, null
					);
			log.debug("historypin search annotation by target test: " + annotationStrRes);
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
		removeAnnotationManually(annotation.getIdentifier());
	}
	
//	@Test
	public void searchAnnotationsWithMultipleTargetsByEachResearchId() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_LINK_ANNOTATION);

		long annotationNr = System.currentTimeMillis();
		String annotationStr = createAnnotationLd(
				MotivationTypes.LINKING.name()
				, annotationNr
				, requestBody
				, null
				);
		Annotation annotation = AnnotationTestUtils.parseAnnotation(annotationStr, null);
		Iterator<String> itr = annotation.getTarget().getResourceIds().iterator();
		while (itr.hasNext()) {
			String resourceId = itr.next();
			String annotationStrRes = searchAnnotationLd(
					null
					, resourceId
					);
			log.debug("historypin search annotation by target test: " + annotationStrRes);
			assertNotNull(annotationStrRes);
			assertTrue(annotationStrRes.contains(resourceId));
		}
		removeAnnotationManually(annotation.getIdentifier());
	}
	
	
}
