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
import eu.europeana.annotation.utils.parse.AnnotationPageParser;

/**
 * This class is for testing of semantic tag entity dereferenciation.
 * 
 * @author GrafR
 *
 */
public class DereferencedSemanticTaggingTest extends BaseTaggingTest {

	static final String SEARCH_VALUE_TEST = "\"body\": \"http://www.wikidata.org/entity/Q41264\""; 
//			"generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";	

	protected Logger log = LogManager.getLogger(getClass());
	
	/**
	 * This is an example dereferenciation test for PoC.
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
//	@Test
	public void createExampleDereferencedSemanticTagEntity() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		
		// retrieve dereferenced annotation
		ResponseEntity<String> response = getAnnotation(storedAnno, SearchProfiles.DEREFERENCE);
		
		assertNotNull(response.getBody());
		
		Annotation retrievedAnnotation = getApiClient().parseResponseBody(response);
//		assertNotNull(retrievedAnnotation.getDereferenced());		
		assertNotNull(retrievedAnnotation.getBody().getHttpUri());		
		assertEquals(retrievedAnnotation.getBody().getHttpUri(),storedAnno.getBody().getValue());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());		
		assertNotNull(((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());		
//		log.info(retrievedAnnotation.getDereferenced());
		log.info("Input body:" + storedAnno.getBody());
		log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
		log.info("ID of dereferenced annotation:" + retrievedAnnotation.getAnnotationId());
	}

	/**
	 * This is a Test for Mozart entity dereferenciation
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
//	@Test
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
	
	/**
	 * Test search query and verify dereferenced search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchDereferencedAnnotation() throws Exception {

		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations(SEARCH_VALUE_TEST, SearchProfiles.DEREFERENCE);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there might be old annotations of failing tests in the database
//		assertTrue(TOTAL_IN_COLLECTION <= annPg.getTotalInCollection());
//		assertEquals(annPg.getCurrentPage(), 0);
//		assertEquals(TOTAL_IN_PAGE, annPg.getTotalInPage());
//		assertEquals(TOTAL_IN_PAGE, annPg.getItems().getResultSize());
//		assertNextPageNumber(annPg, 1);

//		// second page
//		String npUri = annPg.getNextPageUri();
//		String nextPageJson = annSearchApi.getApiConnection().getHttpConnection().getURLContent(npUri);
//		AnnotationPageParser annoPageParser = new AnnotationPageParser();
//		AnnotationPage secondAnnoPg = annoPageParser.parseAnnotationPage(nextPageJson);
//		String currentPageUri = secondAnnoPg.getCurrentPageUri();
//		log.debug("currentPageUri" + currentPageUri);
//		String nextCurrentPageUri = secondAnnoPg.getNextPageUri();
//		log.debug("nextCurrentPageUri" + nextCurrentPageUri);
//		assertNotNull(secondAnnoPg);
//		assertEquals(secondAnnoPg.getCurrentPage(), 1);
//		assertNextPageNumber(secondAnnoPg, 2);
//		assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getTotalInPage());
//		assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getItems().getResultSize());
		
		// last page
//		int lastPageNum = (int)Math.ceil((TOTAL_IN_COLLECTION - 1) / TOTAL_IN_PAGE);
//		AnnotationPage lastPage = annSearchApi.searchAnnotations(VALUE_TESTSET, Integer.toString(lastPageNum), Integer.toString(TOTAL_IN_PAGE), null, null);
//		assertEquals(lastPage.getCurrentPage(), lastPageNum);
	}	

}
