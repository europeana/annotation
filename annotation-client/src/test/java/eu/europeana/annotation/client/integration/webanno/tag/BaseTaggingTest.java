package eu.europeana.annotation.client.integration.webanno.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTaggingTest extends BaseWebAnnotationTest {

	protected Annotation createAndValidateTag(String inputFile) throws IOException, JsonParseException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return createTag(inputFile, true, true);
	}
	
	protected Annotation createTag(String inputFile, boolean validate, boolean indexOnCreate) throws IOException, JsonParseException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		System.out.println("Input File: " + inputFile);

		String requestBody = getJsonStringInput(inputFile);

		Annotation storedAnno = createTestAnnotation(inputFile, indexOnCreate, null);

		Annotation inputAnno = parseTag(requestBody);

		// validate the reflection of input in output!
		if (validate) {
			validateOutputAgainstInput(storedAnno, inputAnno);
		}

		return storedAnno;
	}

	protected Annotation parseTag(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.TAGGING;
		return parseAnnotation(jsonString, motivationType);
	}
	
}
