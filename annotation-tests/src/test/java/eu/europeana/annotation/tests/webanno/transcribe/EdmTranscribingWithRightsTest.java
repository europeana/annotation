package eu.europeana.annotation.tests.webanno.transcribe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;

public class EdmTranscribingWithRightsTest extends BaseTranscribingTest {

    @Test
    public void createTranscriptionWithRights() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
	Annotation inputAnno = parseTranscription(requestBody);

	Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_RIGHTS, true, null);

	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

	assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
	assertTrue(storedAnno.getBody().getEdmRights().equals(inputAnno.getBody().getEdmRights()));
	
	removeAnnotationManually(storedAnno.getIdentifier());
    }

    @Test
    public void createTranscriptionWithoutRights() throws Exception {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_RIGHTS, true, null);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.edmRights";
	assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutLanguage() throws Exception {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_LANG, true, null);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.language";
	assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutValue() throws Exception {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_VALUE, true, null);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.value";
	assertTrue(response.getBody().contains(expectedMessage));
    }
}
