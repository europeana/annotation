package eu.europeana.annotation.tests.admin;

import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.BATCH_TOTAL_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_ERRORS_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FAILURECOUNT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BatchUploadTest extends AbstractIntegrationTest {
    
	// annotation page test resources
	public static final String TAG_ANNO_PAGE = "/tag/batch/annotation_page.json";
	public static final String TAG_ANNO_PAGE_VIA = "/tag/batch/annotation_page_via.json";
	public static final String TAG_ANNO_PAGE_VALIDATION_ERROR = "/tag/wrong/batch/annotation_page_validation_error.json";
	public static final String TAG_ANNO_PAGE_NONEXISTING_ERROR = "/tag/wrong/batch/annotation_page_nonexisting_ids.json";
	
	static final String VALUE_BATCH_TESTSET = "body_value: \"*-ff45d28b-8717-42f4-a486-f3a62f97fb64\"";

	// test annotations
	private List<Annotation> testAnnotations;


	// number of test annotations created in database
	public static final int TEST_NUM_ANNOTATIONS = 2;

	/**
	 * Create test annotations before test execution.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void createTestAnnotations() throws Exception {
		testAnnotations = new ArrayList<Annotation>();
		// create test annotations (representing the existing annotations to be
		// updated)
		Annotation testAnnotation = null;
		for (int i = 0; i < TEST_NUM_ANNOTATIONS; i++) {
			testAnnotation = createTestAnnotation(TAG_MINIMAL, false, null);
			testAnnotations.add(testAnnotation);
			createdAnnotations.add(testAnnotation.getIdentifier());
		}
	}
	
	/**
	 * Testing successful batch upload of annotations.
	 * 
	 * @throws Exception
	 */
	@Test
	public void successfulBatchUploadTest() throws Exception {

		String requestBody = replaceIdentifiers(AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE), "httpurl");

		// batch upload request
		ResponseEntity<String> uploadResponse = uploadAnnotations(
			requestBody, true);
		
		// response status must be 201 CREATED
		assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

		// check response
		JSONObject jsonObj = new JSONObject(uploadResponse.getBody());
		assertEquals(5, jsonObj.get(BATCH_TOTAL_FIELD));
		// check if test annotations which have been created previously in the database 
		// have been updated correctly
		ResponseEntity<String> response;
		Annotation storedAnnotation;
		
		for (int i = 0; i < testAnnotations.size(); i++) {
			response =  getAnnotation(testAnnotations.get(i));
			storedAnnotation = AnnotationTestUtils.parseResponseBody(response);
			String value = storedAnnotation.getBody().getValue();
			assertTrue(value.startsWith("tag"));
			// assuming equal order of test annotations and updated annotations
			assertEquals(testAnnotations.get(i).getIdentifier(), storedAnnotation.getIdentifier());
		}
		
		long startingId = testAnnotations.get(0).getIdentifier();
		for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+5; i++) {
		  createdAnnotations.add(i);
		}
	}
	
	

	/**
	 * Testing batch upload of annotations where the ID is provided by the via field.
	 * 
	 * @throws Exception
	 */
	@Test
	@Disabled
	public void viaBatchUploadTest() throws Exception {
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_VIA);
		
		assertNotNull(requestBody);

		// batch upload request (using pundit provider, ID must be provided by via field)
		ResponseEntity<String> uploadResponse = uploadAnnotations(
				requestBody, true);
		// response status must be 201 CREATED
		assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());
		
		// get response body properties
		JSONObject jsonObj = new JSONObject(uploadResponse.getBody());
		Integer total = (Integer) jsonObj.get(BATCH_TOTAL_FIELD);
		assertEquals(Integer.valueOf(1), total);
	}

	/**
	 * Error when trying to update non existing annotations
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateNonExistingAnnotationsError() throws Exception {

		String requestBody = replaceIdentifiers(AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_NONEXISTING_ERROR), "httpurl");

		// batch upload request
		ResponseEntity<String> response = uploadAnnotations(
				requestBody, false);

		// response status must be 404 NOT FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		
		jsonPrettyPrint(response.getBody());
				
		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(5, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// positions 2, 6 and 8 do not exist
		assertTrue(((String)errors.get("-1")).startsWith("Annotation does not exist"));
		assertTrue(((String)errors.get("-2")).startsWith("Annotation does not exist"));
		assertTrue(((String)errors.get("-3")).startsWith("Annotation does not exist"));
		
        long startingId = testAnnotations.get(0).getIdentifier();
        for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+8; i++) {
          createdAnnotations.add(i);
        }
	}

	/**
	 * Annotation Page contains two annotations which do not validate. The test
	 * must correctly identify the validation errors for the 3rd and for the 5th
	 * position (inclusive counting).
	 * 
	 * @throws Exception
	 */
	@Test
	public void validationError() throws Exception {

		// annotation page string which contains two annotations which do not
		// validate.
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_VALIDATION_ERROR);

		// batch upload request
		ResponseEntity<String> response = uploadAnnotations(
				requestBody, false);

		// response status must be 400 BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(2, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// keys 1 and 2 have errors
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("1"));
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("2"));
	
        long startingId = testAnnotations.get(0).getIdentifier();
        for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+5; i++) {
          createdAnnotations.add(i);
        }
	}

	/**
	 * Replace identifiers in annotation page template. It is assumed that the
	 * number of variables in the template corresponds to the number of test
	 * annotations.
	 * 
	 * @param template
	 *            annotation page template
	 * @param varPrefix
	 *            prefix of the variable name used in the template
	 * @return replacement result
	 * @throws IOException
	 */
	private String replaceIdentifiers(String template, String varPrefix) throws IOException {
		List<String> httpUrlsList = testAnnotations.stream().map(x -> String.valueOf(x.getIdentifier())).collect(Collectors.toList());
		String[] httpUrls = new String[httpUrlsList.size()];
		httpUrls = httpUrlsList.toArray(httpUrls);
		String[] replacementVars = new String[httpUrlsList.size()];
		for (int i = 0; i < httpUrlsList.size(); i++)
			replacementVars[i] = "%" + varPrefix + (i + 1) + "%";
		return StringUtils.replaceEach(template, replacementVars, httpUrls);
	}
	
	/**
	 * Pretty print json output helper method
	 * @param jsonStr JSON string
	 */
	private void jsonPrettyPrint(String jsonStr) {
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement el = parser.parse(jsonStr);
		log.debug(gson.toJson(el));
	}

}
