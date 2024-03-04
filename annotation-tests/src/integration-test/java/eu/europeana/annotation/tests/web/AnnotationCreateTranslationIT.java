package eu.europeana.annotation.tests.web;

import static org.junit.Assert.assertNotNull;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationCreateTranslationIT extends AbstractIntegrationTest {

  
  protected Annotation parseTranslation(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.TRANSLATING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }

  @Test
  public void createMinimalTranslation() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSLATION_MINIMAL);
    Annotation inputAnno = parseTranslation(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSLATION_MINIMAL, true, null);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
  }
}
