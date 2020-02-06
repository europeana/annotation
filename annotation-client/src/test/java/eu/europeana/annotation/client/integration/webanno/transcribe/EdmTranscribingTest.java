package eu.europeana.annotation.client.integration.webanno.transcribe;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;

public class EdmTranscribingTest extends BaseTranscribingTest {

    @Test
    public void createMinimalTranscription() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(TRANSCRIPTION_MINIMAL);
	Annotation inputAnno = parseTranscription(requestBody);

	Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_MINIMAL);

	// validate the reflection of input in output!
	validateOutputAgainstInput(storedAnno, inputAnno);
    }

}
