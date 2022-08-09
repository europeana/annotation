package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;

public class BaseWebAnnotationDataSetTest extends BaseWebAnnotationTest{
	
	protected Logger log = LogManager.getLogger(getClass());

	protected static final String TEST_USER_TOKEN = "tester1";
	public static final String TAG_STANDARD_TESTSET = "/tag/standard_testset.json";

	private String defaultRequestBody;

	
	/**
	 * Create multiple annotations.
	 * 
	 * @param numTestAnno
	 *            Number of test annotations
	 * @return return test annotations array
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected List<Annotation> createMultipleTestAnnotations(int numTestAnno) throws JsonParseException, IOException {
		for (int i = 0; i < numTestAnno; i++) {
			Annotation annotation = createTestAnnotation(i);
			addCreatedAnnotation(annotation);
			assertNotNull(annotation);
		}
		return getCreatedAnnotations();
	}

	/**
	 * Create a single test annotation.
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation(int annoNumber) throws JsonParseException, IOException {
		ResponseEntity<String> response = storeTestAnnotation(annoNumber);
		Annotation annotation = parseAndVerifyTestAnnotation(response);

		return annotation;
	}

	/**
	 * Store test annotation
	 * 
	 * @return Stored response
	 * @throws IOException 
	 */
	protected ResponseEntity<String> storeTestAnnotation(int annoNumber) throws IOException {
	    String adaptedRequestBody = StringUtils.replace(getDefaultRequestBody(), "%test_body_to_replace%", "test_body_"+annoNumber);
		ResponseEntity<String> storedResponse = getApiProtocolClient().createAnnotation(
				true,
				adaptedRequestBody, null, null);
		return storedResponse;
	}

  public String getDefaultRequestBody() throws IOException {
    if(defaultRequestBody == null) {
      defaultRequestBody = getJsonStringInput(TAG_STANDARD_TESTSET);  
    }
    return defaultRequestBody;
  }

}
