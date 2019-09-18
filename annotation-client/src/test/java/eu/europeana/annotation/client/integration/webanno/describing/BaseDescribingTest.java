package eu.europeana.annotation.client.integration.webanno.describing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseDescribingTest extends BaseWebAnnotationProtocolTest {

	public static final String DESCRIBING_CHO = "/describing/cho.json";
	public static final String DESCRIBING_WEB_RESOURCE = "/describing/web_resource.json";

	/**
	 * This method creates annotation object from JSON file
	 * @param inputFile
	 * @return annotation object
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	protected Annotation createAndValidateDescribing(String inputFile) throws IOException, JsonParseException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		System.out.println("Input File: " + inputFile);

		String requestBody = getJsonStringInput(inputFile);

		Annotation storedAnno = createTag(requestBody);

		MotivationTypes motivationType = MotivationTypes.DESCRIBING;
		Annotation inputAnno = parseAnnotation(requestBody, motivationType);

		// validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

		return storedAnno;
	}

}
