package eu.europeana.annotation.tests.webanno.subtitle;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class CaptioningTest extends AbstractIntegrationTest {

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
	createdAnnotations.add(storedAnno.getIdentifier());

	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

}
