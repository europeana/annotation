package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationCreateLinkForContributingIT extends AbstractIntegrationTest {



  @Test
  void createLinkForContributingBodyObject() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
    AnnotationTestUtils.validateResponse(response);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
  }

  @Test
  void createLinkForContributingBodyString() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING, true);
    AnnotationTestUtils.validateResponse(response);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
  }


  @Test
  void createLinkForContributingSpecificTarget_ShouldFail() throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void createLinkForContributingSpecificTargetWithId_shouldFail() throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC_ID, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
  @Test
  void checkAnnotationDuplicatesCreateLinkForContributing() throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

}
