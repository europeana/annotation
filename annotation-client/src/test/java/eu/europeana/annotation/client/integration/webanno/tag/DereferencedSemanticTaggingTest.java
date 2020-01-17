package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class is for testing of semantic tag entity dereferenciation.
 * 
 * @author GrafR
 *
 */
public class DereferencedSemanticTaggingTest extends BaseTaggingTest {

	protected Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void createDereferencedSemanticTagEntity() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_MOZART_ENTITY, false);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		
		// retrieve dereferenced annotation
		ResponseEntity<String> response = getAnnotation(storedAnno);
		
		assertNotNull(response.getBody());
		
		Annotation retrievedAnnotation = getApiClient().parseResponseBody(response);
		assertNotNull(retrievedAnnotation.getDereferenced());		
		log.info(retrievedAnnotation.getDereferenced());
	}

}
