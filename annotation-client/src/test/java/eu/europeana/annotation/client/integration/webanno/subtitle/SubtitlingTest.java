package eu.europeana.annotation.client.integration.webanno.subtitle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SubtitlingTest extends BaseWebAnnotationProtocolTest {

    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
	MotivationTypes motivationType = MotivationTypes.SUBTITLING;
	return parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    public void createMinimalSubtitle() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

    /*
     * To test for the incompatible subtitle formats please use the SUBTITLE_MINIMAL_QT_WRONG file path string 
     * in the code below instead of the SUBTITLE_MINIMAL_QT_RIGHT file path string
     */
    String requestBody = getJsonStringInput(SUBTITLE_MINIMAL_QT_RIGHT);
	Annotation inputAnno = parseSubtitle(requestBody);

	Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL_QT_RIGHT, null);

	// validate the reflection of input in output!
	validateOutputAgainstInput(storedAnno, inputAnno);
	
	Assertions.assertTrue(true);
    }
}
