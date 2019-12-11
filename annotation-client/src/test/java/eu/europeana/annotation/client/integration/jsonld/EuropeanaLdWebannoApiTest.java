package eu.europeana.annotation.client.integration.jsonld;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
 * TODO: move relevant tests to WebAnnotationProtocol classes.
 * @Deprecated
 * @author GordeaS
 *
 */
@Deprecated
public class EuropeanaLdWebannoApiTest extends BaseJsonLdApiTest {
	
	@Test
	public void createSimpleTagWebannoAnnotation() throws JsonParseException {
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
				"oa:tagging"
				, null
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
//		Annotation annotation = europeanaParser.parseAnnotation(null, annotationStr);
//		validateAnnotation(WebAnnotationFields.PROVIDER_WEBANNO, -1, annotation);
	}
	
//	@Test
	public void createSimpleTagAnnotationWithoutProvider() throws JsonParseException {
		
		String annotationStr = europeanaLdApi.createAnnotationLd(
//				"oa:tagging"
				"tagging"
				, null
				, null
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
	}
	
	@Test
	public void createSimpleTagWebannoAnnotationWithAnnotationNr() throws JsonParseException {
		
		long annotationNr = System.currentTimeMillis();
		String annotationStr = europeanaLdApi.createAnnotationLd(
				"oa:tagging"
				, annotationNr
				, simpleTagAnnotation
				);
		System.out.println("webanno annotation test: " + annotationStr);
		assertNotNull(annotationStr);
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_FALSE));
		assertTrue(annotationStr.contains(WebAnnotationFields.UNNECESSARY_ANNOTATION_NR));
	}
	
}
