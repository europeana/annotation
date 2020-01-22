package eu.europeana.annotation.client.integration.webanno.transcribe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;

public class EdmTranscribingWithRightsTest extends BaseTranscribingTest {

	@Test
	public void createTranscriptionWithRights() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
		
		Annotation inputAnno = parseTranscription(requestBody);
		
		Annotation storedAnno = createTranscription(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		assertEquals(storedAnno.getBody().getInternalType(), ResourceTypes.SPECIFIC_RESOURCE.name());
		assertTrue(storedAnno.getBody().getEdmRights().equals(inputAnno.getBody().getEdmRights()));		
	}
	

}
