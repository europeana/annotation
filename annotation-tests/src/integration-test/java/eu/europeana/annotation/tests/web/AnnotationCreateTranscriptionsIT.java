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
public class AnnotationCreateTranscriptionsIT extends AbstractIntegrationTest {

  public static final String TRANSCRIPTION_WITHOUT_RIGHTS =
      "/transcription/transcription-without-rights.json";
  public static final String TRANSCRIPTION_WITHOUT_LANG =
      "/transcription/transcription-without-language.json";
  public static final String TRANSCRIPTION_WITHOUT_VALUE =
      "/transcription/transcription-without-value.json";
  public static final String TRANSCRIPTION_WITH_ALTO_BODY =
      "/transcription/transcription-with-alto-body.json";
  public static final String TRANSCRIPTION_WITH_ALTO_BODY_WRONG =
      "/transcription/transcription-with-alto-body-wrong.json";
  public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY =
      "/transcription/transcription-with-page-xml-body.json";
  public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG =
      "/transcription/transcription-with-page-xml-body-wrong.json";
  public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY_TP =
      "/transcription/transcription-with-page-xml-body-tp.json";

  /*
   * For the below duplicate tests to pass please make sure that there are no annotations stored in
   * Solr that are the same as the ones defined in the test, prior to the test execution.
   */
  @Test
  public void checkTranscriptionDuplicatesCreate() throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  protected Annotation parseTranscription(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.TRANSCRIBING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }

  @Test
  void createTranscriptionMinimal() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
  }

  @Test
  public void createTranscriptionWithRights() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_RIGHTS, true, null);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertEquals(inputAnno.getBody().getEdmRights(), storedAnno.getBody().getEdmRights());

  }
  
  @Test
  public void createTranscriptionWithCopyright() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_COPYRIGHT);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_COPYRIGHT, true, USER_PROVIDER_SAZ);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertEquals(inputAnno.getBody().getEdmRights(), storedAnno.getBody().getEdmRights());

  }
  
  @Test
  public void createTranscriptionWithCopyrightNotOwner() throws Exception {

    //creation should fail if the user is not the owner
    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_COPYRIGHT, true, USER_REGULAR);
    
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //the error must indicate the invalid field
    assertTrue(response.getBody().contains("body.edmRights"));
    
  }

  @Test
  public void createTranscriptionWithoutRights() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_RIGHTS, true);
    assertEquals(HttpStatus.BAD_REQUEST.value(),
        response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! body.edmRights";
    assertTrue(response.getBody().contains(expectedMessage));
  }
  
  @Test
  public void createTranscriptionWithoutLanguage() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_LANG, true);
    assertEquals(HttpStatus.BAD_REQUEST.value(),
        response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.language";
    assertTrue(response.getBody().contains(expectedMessage));
  }

  @Test
  public void createTranscriptionWithoutValue() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_VALUE, true);
    assertEquals(HttpStatus.BAD_REQUEST.value(),
        response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.value";
    assertTrue(response.getBody().contains(expectedMessage));
  }

  @Test
  public void createTranscriptionWithAltoBody() throws Exception {
    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY, true);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());
  }

  @Test
  public void createTranscriptionWithInvalidAltoBody() throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY_WRONG, true);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
  }

  @Test
  public void createTranscriptionWithPageXmlBody() throws Exception {
    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY, true);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());
  }

  @Test
  public void createTranscriptionWithPageXmlBodyTP() throws Exception {
    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY_TP, true);
    assertNotNull(storedAnno);
    addToCreatedAnnotations(storedAnno.getIdentifier());
  }

  @Test
  public void createTranscriptionWithPageXmlInvalidBody() throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG, true);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
  }
}
