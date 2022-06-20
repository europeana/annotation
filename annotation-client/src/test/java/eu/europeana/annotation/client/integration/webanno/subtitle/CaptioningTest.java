package eu.europeana.annotation.client.integration.webanno.subtitle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class CaptioningTest extends BaseWebAnnotationTest {

    public static final String CAPTION_MINIMAL = "/caption/minimal.json";

    protected Annotation parseCaption(String jsonString) throws JsonParseException {
	MotivationTypes motivationType = MotivationTypes.CAPTIONING;
	return parseAnnotation(jsonString, motivationType);
    }

    @Test
    public void createMinimalCaption() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(CAPTION_MINIMAL);
	Annotation inputAnno = parseCaption(requestBody);

	Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, null);
	createdAnnotations.add(storedAnno.getIdentifier());

	// validate the reflection of input in output!
	validateOutputAgainstInput(storedAnno, inputAnno);
	
    }

}
