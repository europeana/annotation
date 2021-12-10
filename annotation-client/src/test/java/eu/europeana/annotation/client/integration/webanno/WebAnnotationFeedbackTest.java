package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;


/**
 * This class aims at testing of the annotation moderation methods.
 * @author GrafR
 */
public class WebAnnotationFeedbackTest extends BaseWebAnnotationTest { 

	protected Logger log = LogManager.getLogger(getClass());

	public static String TEST_API_KEY = "apidemo"; //"apiadmin";
	public static String TEST_USER_TOKEN = "tester1";
	public static String TEST_REPORT_SUMMARY_FIELD = "reportSum";

	
	@Test
	public void createAnnotationReport() throws JsonParseException, IOException {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD);
		validateResponse(response);
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		
		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
				TEST_API_KEY
				, storedAnno.getAnnotationId().getIdentifier()
				, TEST_USER_TOKEN);
		validateReportResponse(reportResponse, HttpStatus.CREATED);
	}


	@Test
	public void getModerationSummary() 
			throws JsonParseException, IllegalAccessException, IllegalArgumentException, 
				   InvocationTargetException, JSONException, IOException {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD);
		validateResponse(response);
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		
		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
				TEST_API_KEY
				, storedAnno.getAnnotationId().getIdentifier()
				, TEST_USER_TOKEN
				);
		validateReportResponse(reportResponse, HttpStatus.CREATED);

		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> getResponse = getApiProtocolClient().getModerationReport(
				TEST_API_KEY
				, storedAnno.getAnnotationId().getIdentifier()
				, TEST_USER_TOKEN
				);
		validateModerationReportResponse(getResponse);
	}
	
	protected void validateModerationReportResponse(ResponseEntity<String> response) 
			throws JsonParseException, JSONException {
		
		assertNotNull(response.getBody());
	    JSONObject jsonObject = new JSONObject(response.getBody());

        String reportSumStr = jsonObject.getString(TEST_REPORT_SUMMARY_FIELD);
        int summary = Integer.parseInt(reportSumStr);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertTrue(summary == 1);
	}
	
					
	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
		validateResponse(response, HttpStatus.CREATED);
	}
	
	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), status);
		
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(storedAnno.getAnnotationId());
		assertTrue(storedAnno.getAnnotationId().toHttpUrl().startsWith("http://"));
	}
	
	protected void validateReportResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(response.getStatusCode(), status);
	}
	
}
