package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
class AnnotationCreateHighlightingIT extends AbstractIntegrationTest {

  protected Annotation parseHighlighting(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.HIGHLIGHTING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }

  @Test
  void createHighlighting() throws Exception {
    String requestBody = AnnotationTestUtils.getJsonStringInput(HIGHLIGHTING);
    Annotation inputAnno = parseHighlighting(requestBody);

    Annotation storedAnno = createTestAnnotation(HIGHLIGHTING, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
  }
  
  @Test
  void createHighlightingWihoutPredicate() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(HIGHLIGHTING_WITHOUT_PREDICATE, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void createHighlightingWihoutExact() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(HIGHLIGHTING_WITHOUT_EXACT, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void checkAnnotationDuplicatesCreateHighlighting() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(HIGHLIGHTING, true);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    response = storeTestAnnotation(HIGHLIGHTING, true, null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
     
}
