package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class is for testing of semantic tag entity dereferenciation.
 * 
 * @author GrafR
 *
 */
public class DereferencedSemanticTaggingTest extends BaseTaggingTest {

	static final String QUERY_ID = "http://viaf.org/viaf/51961439";
	static final String SEARCH_VALUE_TEST = "body_uri:\"" + QUERY_ID + "\"";
	static final String TEST_LANGUAGE = "en,en-US";

	protected Logger log = LogManager.getLogger(getClass());
	
	/**
	 * This is an example dereferenciation test for PoC.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void createExampleDereferencedSemanticTagEntity() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		
		// retrieve dereferenced annotation
		ResponseEntity<String> response = getAnnotation(storedAnno, SearchProfiles.DEREFERENCE);
		
		assertNotNull(response.getBody());
		
		Annotation retrievedAnnotation = getApiClient().parseResponseBody(response);
		assertNotNull(retrievedAnnotation.getBody().getHttpUri());		
		assertEquals(retrievedAnnotation.getBody().getHttpUri(),storedAnno.getBody().getValue());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());		
		log.info("Input body:" + storedAnno.getBody());
		log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
		log.info("ID of dereferenced annotation:" + retrievedAnnotation.getAnnotationId());
	}

	/**
	 * Test search query and verify dereferenced search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchDereferencedAnnotation() throws Exception {
	    	//TODO: the test must create new annotations first. (at least 3 with different VIAF IDS)
		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations(SEARCH_VALUE_TEST, SearchProfiles.DEREFERENCE, TEST_LANGUAGE);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there must be annotations in database after initial insert in this test class
		assertTrue(0 <= annPg.getTotalInCollection());
		assertEquals(annPg.getCurrentPage(), 0);
		for (Annotation foundAnnotation : annPg.getAnnotations()) {
			log.info(foundAnnotation.getAnnotationId());
			log.info(foundAnnotation.getBody().getHttpUri());
			assertEquals(foundAnnotation.getBody().getHttpUri(),QUERY_ID);
		}
	}	

}
