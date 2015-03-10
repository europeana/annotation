package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.test.AnnotationTestObjectBuilder;


public class AnnotationJsonLdApiTest {

//	@Test
	public void createAnnotation() {
		
		AnnotationJsonLdApiImpl annotationJsonLdApi = new AnnotationJsonLdApiImpl();
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = AnnotationTestObjectBuilder.createBaseObjectTagInstance();	
        
        /**
         * convert Annotation object to AnnotationLd object.
         */
//        AnnotationLd annotationLd = new AnnotationLd(originalAnnotation);
//        String initialAnnotationIndent = annotationLd.toString(4);
//        AnnotationLd.toConsole("### initialAnnotation ###", initialAnnotationIndent);

        /**
         * read Annotation object from AnnotationLd object.
         */
//        Annotation annotationFromAnnotationLd = annotationLd.getAnnotation();
        
		String jsonPost = annotationJsonLdApi.apiConnection.getAnnotationGson().toJson(testAnnotation);
//		String jsonPost = annotationJsonLdApi.apiConnection.convertAnnotationToAnnotationLdString(testAnnotation);

		Annotation annotation = annotationJsonLdApi.createAnnotation(jsonPost);
		assertNotNull(annotation);
	}
	
}
