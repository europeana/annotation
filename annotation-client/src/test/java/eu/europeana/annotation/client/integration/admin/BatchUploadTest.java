package eu.europeana.annotation.client.integration.admin;

import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.BATCH_TOTAL_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_ERRORS_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FAILURECOUNT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.utils.AnnotationHttpUrls;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
public class BatchUploadTest extends BaseWebAnnotationTest {

	// annotation page test resources
	public static final String TAG_ANNO_PAGE = "/tag/batch/annotation_page.json";
	public static final String TAG_ANNO_PAGE_VIA = "/tag/batch/annotation_page_via.json";
	public static final String TAG_ANNO_PAGE_VALIDATION_ERROR = "/tag/wrong/batch/annotation_page_validation_error.json";
	public static final String TAG_ANNO_PAGE_NONEXISTING_ERROR = "/tag/wrong/batch/annotation_page_nonexisting_ids.json";
	
	static final String VALUE_BATCH_TESTSET = "body_value: \"*-ff45d28b-8717-42f4-a486-f3a62f97fb64\"";

	// test annotations
	private List<Annotation> testAnnotations;

	// HTTP URLs of the test annotations
	private List<String> testHttpUrls;

	// number of test annotations created in database
	public static final int TEST_NUM_ANNOTATIONS = 2;

	/**
	 * Create test annotations before test execution.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void createTestAnnotations() throws JsonParseException, IOException {
		testAnnotations = new ArrayList<Annotation>();
		testHttpUrls = new ArrayList<String>();
		// create test annotations (representing the existing annotations to be
		// updated)
		Annotation testAnnotation = null;
		for (int i = 0; i < TEST_NUM_ANNOTATIONS; i++) {
			testAnnotation = createTestAnnotation(TAG_MINIMAL, false, null);
			testAnnotations.add(testAnnotation);
			testHttpUrls.add(testAnnotation.getAnnotationId().getIdentifier());
		}
	}

	/**
	 * Testing successful batch upload of annotations.
	 * 
	 * @throws Exception
	 */
	@Test
	public void successfulBatchUploadTest() throws Exception {

		String requestBody = replaceIdentifiers(getJsonStringInput(TAG_ANNO_PAGE), "httpurl");

		// batch upload request
		ResponseEntity<String> uploadResponse = getApiProtocolClient().uploadAnnotations(
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
			storedAnnotation = getApiProtocolClient().parseResponseBody(response);
			String value = storedAnnotation.getBody().getValue();
			assertTrue(value.startsWith("tag"));
			// assuming equal order of test annotations and updated annotations
			assertEquals(testAnnotations.get(i).getAnnotationId().getHttpUrl(), 
					storedAnnotation.getAnnotationId().getHttpUrl());
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
		
		//delete old entries in case they exist
		try{
			deleteAnnotation("batchvia456");
		}catch(Exception e){
			//do nothing, we just avoid duplicate key exception
		}
		
		String requestBody = getJsonStringInput(TAG_ANNO_PAGE_VIA);
		
		assertNotNull(requestBody);

		// batch upload request (using pundit provider, ID must be provided by via field)
		ResponseEntity<String> uploadResponse = getApiProtocolClient().uploadAnnotations(
				requestBody, true);

		// response status must be 201 CREATED
		assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());
		
		// get response body properties
		JSONObject jsonObj = new JSONObject(uploadResponse.getBody());
		Integer total = (Integer) jsonObj.get(BATCH_TOTAL_FIELD);
		assertEquals(new Integer(1), total);

	}

	/**
	 * Error when trying to update non existing annotations
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateNonExistingAnnotationsError() throws Exception {

		String requestBody = replaceIdentifiers(getJsonStringInput(TAG_ANNO_PAGE_NONEXISTING_ERROR), "httpurl");

		// batch upload request
		ResponseEntity<String> response = getApiProtocolClient().uploadAnnotations(
				requestBody, false);

		// response status must be 404 NOT FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		
		jsonPrettyPrint(response.getBody());
				
		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(2, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// positions 2, 6 and 8 do not exist
		assertFalse(errors.has("1"));
		assertTrue(((String)errors.get("2")).startsWith("Annotation does not exist"));
		assertFalse(errors.has("3"));
		assertFalse(errors.has("4"));
		assertFalse(errors.has("5"));
		assertTrue(((String)errors.get("6")).startsWith("Annotation does not exist"));
		assertFalse(errors.has("7"));
		assertTrue(((String)errors.get("8")).startsWith("Annotation does not exist"));
	}

	/**
	 * Delete test annotations after test execution.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@AfterEach
	public void deleteTestAnnotations() throws JsonParseException, IOException {
		for (Annotation anno : testAnnotations) {
			deleteAnnotation(anno);
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
		String requestBody = getJsonStringInput(TAG_ANNO_PAGE_VALIDATION_ERROR);

		// batch upload request
		ResponseEntity<String> response = getApiProtocolClient().uploadAnnotations(
				requestBody, false);

		// response status must be 400 BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(2, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// positions 1,2,4 validate successfully; 3 and 5 have errors
		assertFalse(errors.has("1"));
		assertFalse(errors.has("2"));
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("3"));
		assertFalse(errors.has("4"));
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("5"));
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
		AnnotationHttpUrls annoList = new AnnotationHttpUrls(testAnnotations);

		String[] httpUrls = new String[annoList.size()];
		httpUrls = annoList.getHttpUrls().toArray(httpUrls);

		String[] replacementVars = new String[annoList.size()];
		for (int i = 0; i < annoList.size(); i++)
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
		System.out.println(gson.toJson(el));
	}

}
