package eu.europeana.annotation.tests.webanno.transcribe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class EdmTranscribingTest extends BaseTranscribingTest {

    @Test
    public void createMinimalTranscription() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
	Annotation inputAnno = parseTranscription(requestBody);

	Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
	createdAnnotations.add(storedAnno.getIdentifier());
	
	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

}
