package eu.europeana.annotation.tests.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
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
  
  @Test
  void createTranscriptionWithCopyright() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSLATION_COPYRIGHT);
    Annotation inputAnno = parseTranslation(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSLATION_COPYRIGHT, true, USER_PROVIDER_SAZ);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertEquals(inputAnno.getBody().getEdmRights(), storedAnno.getBody().getEdmRights());

  }
  
  @Test
  void createTranscriptionWithCopyrightNotOwner() throws Exception {

    //creation should fail if the user is not the owner
    ResponseEntity<String> response = storeTestAnnotation(TRANSLATION_COPYRIGHT, true, USER_REGULAR);
    
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //the error must indicate the invalid field
    assertTrue(response.getBody().contains("body.edmRights"));
    
  }
}
