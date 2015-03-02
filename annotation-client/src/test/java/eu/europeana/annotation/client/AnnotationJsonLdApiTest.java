package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.test.AnnotationTestObjectBuilder;


public class AnnotationJsonLdApiTest {

	@Test
	public void createAnnotation() {
		
		AnnotationJsonLdApiImpl annotationJsonLdApi = new AnnotationJsonLdApiImpl();
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance();	
		String jsonPost = annotationJsonLdApi.apiConnection.getAnnotationGson().toJson(testAnnotation);

		Annotation annotation = annotationJsonLdApi.createAnnotation(jsonPost);
		assertNotNull(annotation);
	}
	
}
