package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationDataSetTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.QueryUtils;
import eu.europeana.annotation.utils.parse.AnnotationPageParser;

/**
 * Annotation search API test class
 * 
 * @author Sergiu Gordea @ait
 * @author Sven Schlarb @ait
 */
public class AnnotationSearchApiTest extends BaseWebAnnotationDataSetTest {

	static final String VALUE_ALL = "*:*";
	static final String VALUE_TESTSET = "generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";

	static final int TOTAL_IN_PAGE = 10;
	static final int TOTAL_IN_COLLECTION = 21;

	private Annotation[] annotations;

	/**
	 * Create annotations data set before each test execution
	 * 
	 * @throws IOException
	 */
	@Before
	public void createAnnotationDataSet() throws JsonParseException, IOException {
		annotations = createMultipleTestAnnotations(TOTAL_IN_COLLECTION);
		assertEquals(TOTAL_IN_COLLECTION, annotations.length);
	}

	/**
	 * Delete annotations data set after each test execution
	 */
	@After
	public void deleteAnnotationDataSet() {
		//TODO delete all annotations for this generator, including old annotations
		deleteAnnotations(annotations);
	}

	/**
	 * Check if the next page URI points to the correct page number
	 * 
	 * @param annPg
	 *            Annotation page object
	 * @param expPgNum
	 *            Expected page number
	 * @throws MalformedURLException
	 */
	private void assertNextPageNumber(AnnotationPage annPg, Integer expPgNum) throws MalformedURLException {
		String nextPageUri = annPg.getNextPageUri();
		Integer nextPgNum = QueryUtils.getQueryParamNumValue(nextPageUri, WebAnnotationFields.PAGE);
		log.debug(nextPageUri);
		log.debug(nextPgNum);
		assertEquals(expPgNum, nextPgNum);
	}

	/**
	 * Test search query and verify search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchAnnotationPaging() throws Exception {

		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations(VALUE_TESTSET, SearchProfiles.MINIMAL);
		assertNotNull("AnnotationPage must not be null", annPg);
		//there might be old annotations of failing tests in the database
		assertTrue(TOTAL_IN_COLLECTION <= annPg.getTotalInCollection());
		assertEquals(annPg.getCurrentPage(), 0);
		assertEquals(TOTAL_IN_PAGE, annPg.getTotalInPage());
		assertEquals(TOTAL_IN_PAGE, annPg.getItems().getResultSize());
		assertNextPageNumber(annPg, 1);

		// second page
		String npUri = annPg.getNextPageUri();
		String nextPageJson = annSearchApi.getApiConnection().getHttpConnection().getURLContent(npUri);
		AnnotationPageParser annoPageParser = new AnnotationPageParser();
		AnnotationPage secondAnnoPg = annoPageParser.parseAnnotationPage(nextPageJson);
		String currentPageUri = secondAnnoPg.getCurrentPageUri();
		log.debug("currentPageUri" + currentPageUri);
		String nextCurrentPageUri = secondAnnoPg.getNextPageUri();
		log.debug("nextCurrentPageUri" + nextCurrentPageUri);
		assertNotNull(secondAnnoPg);
		assertEquals(secondAnnoPg.getCurrentPage(), 1);
		assertNextPageNumber(secondAnnoPg, 2);
		assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getTotalInPage());
		assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getItems().getResultSize());
		
		// last page
		int lastPageNum = (int)Math.ceil((TOTAL_IN_COLLECTION - 1) / TOTAL_IN_PAGE);
		AnnotationPage lastPage = annSearchApi.searchAnnotations(VALUE_TESTSET, Integer.toString(lastPageNum), Integer.toString(TOTAL_IN_PAGE), null, null);
		assertEquals(lastPage.getCurrentPage(), lastPageNum);
		
	}

}
