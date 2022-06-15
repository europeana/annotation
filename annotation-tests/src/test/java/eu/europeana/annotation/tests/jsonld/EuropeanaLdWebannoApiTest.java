package eu.europeana.annotation.tests.jsonld;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseJsonLdApiTest;

/**
 * TODO: move relevant tests to WebAnnotationProtocol classes.
 * @Deprecated
 * @author GordeaS
 *
 */
@Deprecated
public class EuropeanaLdWebannoApiTest extends BaseJsonLdApiTest {
	
    Logger log = LogManager.getLogger(getClass());
    
	//@Test
    @Deprecated
	public void createSimpleTagWebannoAnnotation() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_TAG_ANNOTATION);
		
		String annotationStr = createAnnotationLd(
				"oa:tagging"
				, null
				, requestBody
				, null
				);
		log.debug("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		Annotation annotation = AnnotationTestUtils.parseAnnotation(annotationStr, null);
		removeAnnotationManually(annotation.getIdentifier());
//		validateAnnotation(WebAnnotationFields.PROVIDER_WEBANNO, -1, annotation);
	}
		
	//@Test
	@Deprecated 
	/**
	 * provider not supported anymore, all annotation IDs will be generated
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public void createSimpleTagWebannoAnnotationWithAnnotationNr() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_TAG_ANNOTATION);

		long annotationNr = System.currentTimeMillis();
		String annotationStr = createAnnotationLd(
				"oa:tagging"
				, annotationNr
				, requestBody
				, null
				);
		log.debug("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		assertTrue(annotationStr.contains(WebAnnotationFields.UNNECESSARY_ANNOTATION_NR));
	    Annotation annotation = AnnotationTestUtils.parseAnnotation(annotationStr, null);
	    removeAnnotationManually(annotation.getIdentifier());

	}
	
}
