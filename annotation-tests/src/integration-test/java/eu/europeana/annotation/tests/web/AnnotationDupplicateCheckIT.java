package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationDupplicateCheckIT extends AbstractIntegrationTest {

    /*
     * For the below duplicate tests to pass please make sure that there are no annotations 
     * stored in Solr that are the same as the ones defined in the test, prior to the test execution.
     */
    @Test
    public void checkAnnotationDuplicatesCreateTranscriptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);    
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(CAPTION_MINIMAL_EN, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCaptionsThenSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesSubtitlesThenCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        response = storeTestAnnotation(CAPTION_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesCreateTags() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TAG_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        response = storeTestAnnotation(TAG_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateSemanticTags() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateLinkForContributing() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        
    }
    
    /*
     *  
     */
    @Test
    public void checkAnnotationDuplicatesCreateObjectLinks() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        //there is no dupplicate check for linking, it is tricky to check dupplication when multiple targets are provided
        response = storeTestAnnotation(LINK_MINIMAL, true);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
