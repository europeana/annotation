package eu.europeana.annotation.client.integration.webanno.subtitle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SubtitlingTest extends BaseWebAnnotationTest {

    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
	MotivationTypes motivationType = MotivationTypes.SUBTITLING;
	return parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    public void createMinimalSubtitle() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(SUBTITLE_MINIMAL);

	Annotation inputAnno = parseSubtitle(requestBody);

	Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, null);
	createdAnnotations.add(storedAnno.getIdentifier());

	// validate the reflection of input in output!
	validateOutputAgainstInput(storedAnno, inputAnno);
	
	Assertions.assertTrue(true);
    }
}
