package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
	static final String QUERY_ID_2 = "https://viaf.org/viaf/96987389/";
	static final String QUERY_ID_3 = "https://viaf.org/viaf/24602065/";
	static final String SEARCH_VALUE_TEST = "body_uri:\"" + QUERY_ID + "\"";
	static final String TEST_LANGUAGE = "en,en-US";
	static final String TEST_LANGUAGE_MULTI = "en,en-US,it,fr";
	static final int NUM_TEST_ANNOTATIONS = 3;

	protected Logger log = LogManager.getLogger(getClass());
	
	private Annotation[] testAnnotations = new Annotation[NUM_TEST_ANNOTATIONS];
	
    @BeforeEach
	public void createTestAnnotations() throws Exception {
	    // create new annotations first. (3 with different VIAF IDS)
    	Annotation testAnnotation = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
		testAnnotations[0] = testAnnotation;
		testAnnotation = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2, false, true);
		testAnnotations[1] = testAnnotation;
		testAnnotation = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3, false, true);		
		testAnnotations[2] = testAnnotation;
    }
	
	/**
	 * Delete annotations data set after each test execution
	 */
	@AfterEach
	public void deleteAnnotationDataSet() {
		deleteAnnotations(testAnnotations);
	}
    
	/**
	 * Delete annotations
	 * 
	 * @param annotations
	 */
	protected void deleteAnnotations(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			deleteAnnotation(
					annotation.getIdentifier());
		}
	}
	
	/**
	 * This is an example dereferenciation test for PoC.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void dereferencedSemanticTag() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = testAnnotations[0];
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		
		// retrieve dereferenced annotation
		ResponseEntity<String> response = getAnnotation(storedAnno, SearchProfiles.DEREFERENCE);
		
		assertNotNull(response.getBody());
		
		Annotation retrievedAnnotation = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(retrievedAnnotation.getBody().getHttpUri());		
		assertEquals(retrievedAnnotation.getBody().getHttpUri(),storedAnno.getBody().getValue());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());		
		log.info("Input body:" + storedAnno.getBody());
		log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
		log.info("Identifier of the dereferenced annotation:" + retrievedAnnotation.getIdentifier());
	}

	/**
	 * This is a retrieval test for dereferenciation entity with only JWT token - without wskey.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void retrieveByJwtTokenDereferencedSemanticTagEntity() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = testAnnotations[0];
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		
		// retrieve dereferenced annotation
		ResponseEntity<String> response = getAnnotationByJwtToken(storedAnno, SearchProfiles.DEREFERENCE);
		
		assertNotNull(response.getBody());
		
		Annotation retrievedAnnotation = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(retrievedAnnotation.getBody().getHttpUri());		
		assertEquals(retrievedAnnotation.getBody().getHttpUri(),storedAnno.getBody().getValue());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());		
		log.info("Input body:" + storedAnno.getBody());
		log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
		log.info("Identifier of the dereferenced annotation:" + retrievedAnnotation.getIdentifier());
	}

	/**
	 * Test search query and verify dereferenced search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchDereferencedAnnotation() throws Exception {
		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations(SEARCH_VALUE_TEST, SearchProfiles.DEREFERENCE, TEST_LANGUAGE);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there must be annotations in database after initial insert in this test class
		assertTrue(0 <= annPg.getTotalInCollection());
		assertEquals(annPg.getCurrentPage(), 0);
		for (Annotation foundAnnotation : annPg.getAnnotations()) {
			log.info(foundAnnotation.getIdentifier());
			log.info(foundAnnotation.getBody().getHttpUri());
			assertEquals(foundAnnotation.getBody().getHttpUri(),QUERY_ID);
		}
	}	

	/**
	 * Test search query with multiple languages and verify dereferenced search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchDereferencedAnnotationMultiLanguage() throws Exception {
		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations(
				SEARCH_VALUE_TEST, SearchProfiles.DEREFERENCE, TEST_LANGUAGE_MULTI);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there must be annotations in database after initial insert in this test class
		assertTrue(0 <= annPg.getTotalInCollection());
		assertEquals(annPg.getCurrentPage(), 0);
		for (Annotation foundAnnotation : annPg.getAnnotations()) {
			log.info(foundAnnotation.getIdentifier());
			log.info(foundAnnotation.getBody().getHttpUri());
			assertEquals(foundAnnotation.getBody().getHttpUri(),QUERY_ID);
		}
	}	

}
