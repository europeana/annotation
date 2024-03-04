package eu.europeana.annotation.client.integration.webanno.transcribe;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;

public class TranslatingTest extends BaseTranscribingTest {

  @Test
  public void createMinimalTranscription() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    String requestBody = getJsonStringInput(TRANSLATION_MINIMAL);
    Annotation inputAnno = parseTranslation(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSLATION_MINIMAL, null);
    addCreatedAnnotation(storedAnno);

    // validate the reflection of input in output!
    validateOutputAgainstInput(storedAnno, inputAnno);
  }

}
