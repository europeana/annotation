package eu.europeana.annotation.client.integration.jsonld;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationJsonLdApiImpl;
import eu.europeana.annotation.client.EuropeanaLdApiImpl;
import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;


public class AnnotationJsonLdApiTest  extends BaseJsonLdApiTest {//extends AnnotationTestObjectBuilder {
	
	@Test
	public void createAnnotation() throws JsonParseException {
		
//		AnnotationJsonLdApiImpl annotationJsonLdApi = new AnnotationJsonLdApiImpl();
//		
//		/**
//		 * Create a test annotation object.
//		 */
//		Annotation testAnnotation = createBaseObjectTagInstance();	
        
		
		long annotationNr = System.currentTimeMillis();
		String providerHistoryPin = WebAnnotationFields.PROVIDER_HISTORY_PIN;
		
		String annotationStr = annotationJsonLdApi.createAnnotationLd(
				providerHistoryPin
				, annotationNr
				, objectTagHistoryPinAnnotation
				);
		assertNotNull(annotationStr);
//		Annotation annotation = europeanaParser.parseAnnotation(annotationStr);
//		validateAnnotation(providerHistoryPin, annotationNr, annotation);
//		assertTrue(BodyTypes.isTagBody(annotation.getBody().getInternalType()));

		
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
        
		/*String jsonPost = annotationJsonLdApi.getApiConnection().getAnnotationGson().toJson(testAnnotation);
//		String jsonPost = annotationJsonLdApi.apiConnection.convertAnnotationToAnnotationLdString(testAnnotation);

		Annotation annotation = annotationJsonLdApi.createAnnotation(jsonPost);
		assertNotNull(annotation);*/
	}
	
}
