package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
 * This class implements api key testing scenarios.
 */
@Disabled
public class ModerationReportTest extends BaseTaggingTest {

	public final String API_KEY_CONFIG_FOLDER = "/config";

	protected Logger log = LogManager.getLogger(getClass());

	public static String TEST_USER_TOKEN = "admin";
	public static String JSON_FORMAT = "json";

	private static final Map<String, String> apiKeyMap = new HashMap<String, String>();

	private static void initApiKeyMap() {
		apiKeyMap.put("apidemo", WebAnnotationFields.DEFAULT_PROVIDER);
	}

	@BeforeEach
	public void setUp() throws Exception {
		initApiKeyMap();
	}

	
	/**
	 * This test performs storage of moderation reports for admin user for all
	 * api keys stored in JSON files in template folder.
	 * 
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
//	@Test
	public void createTagMinimalWithModerationReportAndRemoval() throws IOException, JsonParseException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String requestBody = getJsonStringInput(TAG_MINIMAL);

		for (Map.Entry<String, String> entry : apiKeyMap.entrySet()) {

//			Annotation storedAnno = createTag(requestBody, entry.getValue(), entry.getKey(), TEST_USER_TOKEN);
			Annotation storedAnno = createTag(requestBody);
			long identifier = storedAnno.getIdentifier();

			ResponseEntity<String> reportResponse = storeTestAnnotationReport(entry.getKey(), identifier,
					TEST_USER_TOKEN);
			validateReportResponse(reportResponse, HttpStatus.CREATED);

			ResponseEntity<String> response = getApiProtocolClient().deleteAnnotation(
					identifier);
			validateReportResponse(response, HttpStatus.NO_CONTENT);
		}

	}

	protected void validateReportResponse(ResponseEntity<String> response, HttpStatus status)
			throws JsonParseException {
		assertEquals(status, response.getStatusCode());
	}

}
