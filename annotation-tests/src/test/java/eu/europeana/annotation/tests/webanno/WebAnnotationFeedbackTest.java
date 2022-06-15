package eu.europeana.annotation.tests.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;


/**
 * This class aims at testing of the annotation moderation methods.
 * @author GrafR
 */
public class WebAnnotationFeedbackTest extends BaseAnnotationTest { 

	protected Logger log = LogManager.getLogger(getClass());

	public static String TEST_API_KEY = "apidemo"; //"apiadmin";
	public static String TEST_USER_TOKEN = "tester1";
		
	@Test
	public void createAnnotationReport() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);
		AnnotationTestUtils.validateResponse(response);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		
		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
				TEST_API_KEY
				, storedAnno.getIdentifier()
				, TEST_USER_TOKEN);
		assertEquals(reportResponse.getStatusCode(), HttpStatus.CREATED);
		removeAnnotationManually(storedAnno.getIdentifier());
	}


	@Test
	public void getModerationSummary() 
			throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);
		AnnotationTestUtils.validateResponse(response);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		
		ResponseEntity<String> reportResponse = storeTestAnnotationReport(
				TEST_API_KEY
				, storedAnno.getIdentifier()
				, TEST_USER_TOKEN
				);
		assertEquals(reportResponse.getStatusCode(), HttpStatus.CREATED);

		/**
		 * get annotation by provider and identifier
		 */
		ResponseEntity<String> getResponse = getModerationReport(
				TEST_API_KEY
				, storedAnno.getIdentifier()
				, TEST_USER_TOKEN
				);
		AnnotationTestUtils.validateModerationReportResponse(getResponse);
		removeAnnotationManually(storedAnno.getIdentifier());
	}
	
	

}
