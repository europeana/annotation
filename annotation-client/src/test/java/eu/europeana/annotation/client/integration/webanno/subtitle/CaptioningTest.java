package eu.europeana.annotation.client.integration.webanno.subtitle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class CaptioningTest extends BaseWebAnnotationTest {

  public static final String CAPTION_MINIMAL = "/caption/minimal.json";
  public static final String CAPTION_INCONSISTENT_MIME_TYPE =
      "/caption/caption_inconsistent_mime_type.json";



  protected Annotation parseCaption(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.CAPTIONING;
    return parseAnnotation(jsonString, motivationType);
  }

  @Test
  public void createMinimalCaption() throws IOException, JsonParseException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {

    String requestBody = getJsonStringInput(CAPTION_MINIMAL);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, null);
    addCreatedAnnotation(storedAnno);

    // validate the reflection of input in output!
    validateOutputAgainstInput(storedAnno, inputAnno);

  }

  @Test
  public void createCaptionInconsistentMimeType() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // String requestBody = getJsonStringInput(CAPTION_MINIMAL);
    // Annotation inputAnno = parseCaption(requestBody);
    ResponseEntity<String> response = storeTestAnnotation(CAPTION_INCONSISTENT_MIME_TYPE);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

}
