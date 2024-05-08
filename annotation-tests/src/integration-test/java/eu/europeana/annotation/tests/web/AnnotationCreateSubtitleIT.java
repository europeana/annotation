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
class AnnotationCreateSubtitleIT extends AbstractIntegrationTest {



  protected Annotation parseCaption(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.CAPTIONING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }

  @Test
  void createMinimalCaption() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_MINIMAL);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
  }

  protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }
  
  @Test
  void createCaptionWithCopyright() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_WITH_COPYRIGHT);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_WITH_COPYRIGHT, true, USER_PROVIDER_SAZ);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertEquals(inputAnno.getBody().getEdmRights(), storedAnno.getBody().getEdmRights());

  }
  
  @Test
  void createCaptionWithCopyrightNotOwner() throws Exception {

    //creation should fail if the user is not the owner
    ResponseEntity<String> response = storeTestAnnotation(CAPTION_WITH_COPYRIGHT, true, USER_REGULAR);
    
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //the error must indicate the invalid field
    assertTrue(response.getBody().contains("body.edmRights"));
    
  }

  @Test
  void createMinimalSubtitle() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);

    Annotation inputAnno = parseSubtitle(requestBody);

    Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
  }

  @Test
  void createSubtitleWithCopyright() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_WITH_COYRIGHT);
    Annotation inputAnno = parseSubtitle(requestBody);

    Annotation storedAnno = createTestAnnotation(SUBTITLE_WITH_COYRIGHT, true, USER_PROVIDER_SAZ);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertEquals(inputAnno.getBody().getEdmRights(), storedAnno.getBody().getEdmRights());

  }
  
  @Test
  void createSubtitleWithCopyrightNotOwner() throws Exception {

    //creation should fail if the user is not the owner
    ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_WITH_COYRIGHT, true, USER_REGULAR);
    
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //the error must indicate the invalid field
    assertTrue(response.getBody().contains("body.edmRights"));
    
  }

  
  @Test
  void checkAnnotationDuplicatesCaptionsThenSubtitles() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void checkAnnotationDuplicatesCreateSubtitles() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void checkAnnotationDuplicatesSubtitlesThenCaptions() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void checkAnnotationDuplicatesCreateCaptions() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    response = storeTestAnnotation(CAPTION_MINIMAL_EN, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
 
 
}
