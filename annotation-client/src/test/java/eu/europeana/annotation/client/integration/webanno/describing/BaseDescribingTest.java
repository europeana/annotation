package eu.europeana.annotation.client.integration.webanno.describing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseDescribingTest extends BaseWebAnnotationTest {

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

		log.debug("Input File: " + inputFile);

		String requestBody = getJsonStringInput(inputFile);

		ResponseEntity<String> response = storeTestAnnotation(inputFile);
		validateResponse(response);
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);				

		MotivationTypes motivationType = MotivationTypes.DESCRIBING;
		Annotation inputAnno = parseAnnotation(requestBody, motivationType);

		// validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

		return storedAnno;
	}
	
//	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
//		validateResponse(response, HttpStatus.CREATED);
//	}
	
//	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
//		assertNotNull(response.getBody());
//		assertEquals(response.getStatusCode(), status);
//		
//		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
//		assertNotNull(storedAnno.getIdentifier());
//	}
	
}
