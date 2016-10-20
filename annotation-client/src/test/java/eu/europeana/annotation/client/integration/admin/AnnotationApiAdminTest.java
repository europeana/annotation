package eu.europeana.annotation.client.integration.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.AnnotationJsonLdApiImpl;
import eu.europeana.annotation.client.abstracts.BaseJsonLdApiTest;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApi;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApiImpl;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
* Annotation API Admin Test class
* @author Sven Schlarb 
*/
public class AnnotationApiAdminTest extends BaseWebAnnotationProtocolTest {
	
	protected final Logger logger = Logger.getLogger(this.getClass());

	@Test
	public void createAndDeleteAnnotation() throws JsonParseException {
		
		// create
		Annotation annotation = this.createTestAnnotation();
		
		// read
		assertNotNull(annotation);
		String annotIdUriStr = annotation.getAnnotationId().toString();
		Integer numericId = Integer.parseInt(annotIdUriStr.substring(annotIdUriStr.lastIndexOf("/") + 1));
		assertTrue(numericId > 0);
		logger.debug("Created annotation: " + annotIdUriStr);
				
		// delete
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(numericId);
		assertEquals(re.getStatusCode(), HttpStatus.OK);
		logger.debug("Annotation deleted: " + annotIdUriStr);
		
	}

}
