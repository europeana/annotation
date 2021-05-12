package eu.europeana.annotation.client.integration.webanno.transcribe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;

public class EdmTranscribingWithRightsTest extends BaseTranscribingTest {

    @Test
    public void createTranscriptionWithRights() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
	Annotation inputAnno = parseTranscription(requestBody);

	Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_RIGHTS, null);

	// validate the reflection of input in output!
	validateOutputAgainstInput(storedAnno, inputAnno);

	assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
	assertTrue(storedAnno.getBody().getEdmRights().equals(inputAnno.getBody().getEdmRights()));
    }

    @Test
    public void createTranscriptionWithoutRights() throws IOException {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_RIGHTS);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.edmRights";
	assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutLanguage() throws IOException {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_LANG);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.language";
	assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutValue() throws IOException {

	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_VALUE);
	assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
	String expectedMessage = "Missing mandatory field! transcription.body.value";
	assertTrue(response.getBody().contains(expectedMessage));
    }
}
