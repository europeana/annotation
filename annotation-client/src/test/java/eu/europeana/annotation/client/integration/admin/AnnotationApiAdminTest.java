package eu.europeana.annotation.client.integration.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
import eu.europeana.annotation.client.integration.webanno.tag.BaseTaggingTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
* Annotation API Admin Test class
* @author Sven Schlarb 
*/
public class AnnotationApiAdminTest extends BaseWebAnnotationProtocolTest {

	@Test
	public void createAndDeleteAnnotation() throws JsonParseException {
		
		// create
		Annotation annotation = this.createTestAnnotation();
		
		// read
		assertNotNull(annotation);
		
		Integer numericId = getNumericAnnotationId(annotation);
		assertTrue(getNumericAnnotationId(annotation) > 0);
		log.debug("Created annotation: " + numericId);
				
		// delete
		this.deleteAnnotation(numericId);
	}
	
	// TODO #502: Unit test

}
