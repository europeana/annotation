package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.admin.WebAnnotationAdminApi;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApiImpl;
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApi;
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;

public class BaseWebAnnotationDataSetTest {
	
	protected Logger log = LogManager.getLogger(getClass());

	protected static final String TEST_USER_TOKEN = "tester1";
	public static final String TAG_STANDARD_TESTSET = "/tag/standard_testset.json";

	protected WebAnnotationProtocolApi apiClient;
	private String defaultRequestBody;

	/**
	 * Initialize objects
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void initObjects() throws IOException {
		apiClient = new WebAnnotationProtocolApiImpl();
		defaultRequestBody = this.getJsonStringInput(TAG_STANDARD_TESTSET);
	}

	/**
	 * Create multiple annotations.
	 * 
	 * @param numTestAnno
	 *            Number of test annotations
	 * @return return test annotations array
	 * @throws JsonParseException
	 */
	protected Annotation[] createMultipleTestAnnotations(Integer numTestAnno) throws JsonParseException {

		Annotation[] testAnnotations = new Annotation[numTestAnno];
		for (int i = 0; i < numTestAnno; i++) {
			Annotation annotation = createTestAnnotation();
			assertNotNull(annotation);
			testAnnotations[i] = annotation;
		}
		return testAnnotations;
	}

	/**
	 * Create a single test annotation.
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 */
	protected Annotation createTestAnnotation() throws JsonParseException {
		ResponseEntity<String> response = storeTestAnnotation();
		Annotation annotation = parseAndVerifyTestAnnotation(response);

		return annotation;
	}

	/**
	 * Store test annotation
	 * 
	 * @return Stored response
	 */
	protected ResponseEntity<String> storeTestAnnotation() {
		ResponseEntity<String> storedResponse = apiClient.createAnnotation(
				true,
				defaultRequestBody, null, null);
		return storedResponse;
	}

	/**
	 * Parse and verify annotation
	 * 
	 * @param response
	 *            Response object
	 * @return Annotation
	 * @throws JsonParseException
	 */
	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response) throws JsonParseException {

		return parseAndVerifyTestAnnotation(response, HttpStatus.CREATED);
	}

	/**
	 * Parse and verify annotation
	 * 
	 * @param response
	 *            Response object
	 * @return Annotation
	 * @throws JsonParseException
	 */
	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response, HttpStatus status)
			throws JsonParseException {
		assertEquals("" + status.value(), "" + response.getStatusCode().value());

		Annotation annotation = apiClient.parseResponseBody(response);

		return annotation;
	}

//	/**
//	 * Get numeric annotation id
//	 * 
//	 * @param annotation
//	 *            Annotation object
//	 * @return Numeric annotation id
//	 */
//	private Integer getNumericAnnotationId(Annotation annotation) {
//		String annotIdUriStr = annotation.getAnnotationId().toString();
//		Integer numericId = Integer.parseInt(annotIdUriStr.substring(annotIdUriStr.lastIndexOf("/") + 1));
//		return numericId;
//	}

	/**
	 * Delete annotations
	 * 
	 * @param annotations
	 */
	protected void deleteAnnotations(Annotation[] annotations) {
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		if(annotations == null) {
			return;
		}	
		for (Annotation annotation : annotations) {
			ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(
					annotation.getAnnotationId().getIdentifier());
			assertEquals(re.getStatusCode(), HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * Get JSON string from resource
	 * 
	 * @param resource
	 *            Resource
	 * @return JSON string
	 * @throws IOException
	 */
	protected String getJsonStringInput(String resource) throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream(resource);

		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
		for (String line = br.readLine(); line != null; line = br.readLine())
			out.append(line);
		br.close();
		return out.toString();

	}

}
