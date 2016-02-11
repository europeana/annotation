package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;


/**
 * This class aims at testing of the annotation moderation methods.
 * @author GrafR
 */
public class WebAnnotationFeedbackTest extends BaseWebAnnotationProtocolTest { 

	protected Logger log = Logger.getLogger(getClass());

	public static String TEST_API_KEY = "apiadmin";
	public static String TEST_USER_TOKEN = "tester1";

	
	@Test
	public void createAnnotationReport() throws JsonParseException {
		
		ResponseEntity<String> response = storeTestAnnotation();
		validateResponse(response);
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		
		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
				TEST_API_KEY
				, storedAnno.getAnnotationId().getProvider()
				, storedAnno.getAnnotationId().getIdentifier()
				, TEST_USER_TOKEN);
		validateReportResponse(reportResponse, HttpStatus.CREATED);
	}


	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
		validateResponse(response, HttpStatus.CREATED);
	}
	
	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), status);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getAnnotationId());
		assertTrue(storedAnno.getAnnotationId().toHttpUrl().startsWith("http://"));
	}
	
	protected void validateReportResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(response.getStatusCode(), status);
	}
	
}
