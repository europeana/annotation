package eu.europeana.annotation.tests.webanno.subtitle;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.AbstractIntegrationTest;

public class SubtitlingTest extends AbstractIntegrationTest {

    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
	MotivationTypes motivationType = MotivationTypes.SUBTITLING;
	return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    public void createMinimalSubtitle() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);

	Annotation inputAnno = parseSubtitle(requestBody);

	Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
	createdAnnotations.add(storedAnno.getIdentifier());

	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
	
	Assertions.assertTrue(true);
    }
}
