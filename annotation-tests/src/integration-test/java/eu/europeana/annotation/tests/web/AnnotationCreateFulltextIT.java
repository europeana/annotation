package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
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
public class AnnotationCreateFulltextIT extends AbstractIntegrationTest {

    public static final String TRANSCRIPTION_WITHOUT_RIGHTS = "/transcription/transcription-without-rights.json";
    public static final String TRANSCRIPTION_WITHOUT_LANG = "/transcription/transcription-without-language.json";
    public static final String TRANSCRIPTION_WITHOUT_VALUE = "/transcription/transcription-without-value.json";
    public static final String TRANSCRIPTION_WITH_ALTO_BODY = "/transcription/transcription-with-alto-body.json";
    public static final String TRANSCRIPTION_WITH_ALTO_BODY_WRONG = "/transcription/transcription-with-alto-body-wrong.json";
    public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY = "/transcription/transcription-with-page-xml-body.json";
    public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG = "/transcription/transcription-with-page-xml-body-wrong.json";
    public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY_TP = "/transcription/transcription-with-page-xml-body-tp.json";
	
	/*
     * For the below duplicate tests to pass please make sure that there are no annotations 
     * stored in Solr that are the same as the ones defined in the test, prior to the test execution.
     */
    @Test
    public void checkAnnotationDuplicatesCreateTranscriptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);    
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(CAPTION_MINIMAL_EN, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCaptionsThenSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesSubtitlesThenCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesCreateLinkForContributing() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        
    }
    
    
    protected Annotation parseCaption(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.CAPTIONING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }

    @Test
    public void createMinimalCaption() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_MINIMAL);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
  
    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    public void createMinimalSubtitle() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);

    Annotation inputAnno = parseSubtitle(requestBody);

    Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    
    Assertions.assertTrue(true);
    }
 
    protected Annotation parseTranscription(String jsonString) throws JsonParseException {
      MotivationTypes motivationType = MotivationTypes.TRANSCRIBING;
      return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);     
    }
    
    @Test
    public void createMinimalTranscription() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());
    
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
   
    @Test
    public void createTranscriptionWithRights() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_RIGHTS, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertTrue(storedAnno.getBody().getEdmRights().equals(inputAnno.getBody().getEdmRights()));
    
    }

    @Test
    public void createTranscriptionWithoutRights() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_RIGHTS, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.edmRights";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutLanguage() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_LANG, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.language";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutValue() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_VALUE, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.value";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithAltoBody() throws Exception {
        Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY, true, null);
        createdAnnotations.add(storedAnno.getIdentifier());
    	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY_WRONG, true, null);
    	assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
	public void createTranscriptionWithPageXmlBody() throws Exception {
        Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY, true, null);
        createdAnnotations.add(storedAnno.getIdentifier());        
    }
    
    @Test
    public void createTranscriptionWithPageXmlBodyTP() throws Exception {
        Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY_TP, true, null);
        createdAnnotations.add(storedAnno.getIdentifier());        
    }  
    
    @Test
    public void createTranscriptionWithPageXmlInvalidBody() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG, true, null);
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());        
    }
}
