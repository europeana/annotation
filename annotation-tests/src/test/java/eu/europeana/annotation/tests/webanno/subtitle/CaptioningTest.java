package eu.europeana.annotation.tests.webanno.subtitle;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;

public class CaptioningTest extends BaseAnnotationTest {

    public static final String CAPTION_MINIMAL = "/caption/minimal.json";

    protected Annotation parseCaption(String jsonString) throws JsonParseException {
	MotivationTypes motivationType = MotivationTypes.CAPTIONING;
	return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }

    @Test
    public void createMinimalCaption() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_MINIMAL);
	Annotation inputAnno = parseCaption(requestBody);

	Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, true, null);

	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
	
	removeAnnotationManually(storedAnno.getIdentifier());
    }

}
