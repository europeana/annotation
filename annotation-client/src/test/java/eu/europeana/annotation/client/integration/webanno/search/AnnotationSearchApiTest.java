package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	static final String VALUE_TESTSET = "generator_uri: \"http://data.europeana.eu/apikey/annotations\"";

	static final int TOTAL_IN_PAGE = 10;
	static final int TOTAL_IN_COLLECTION = 21;

	private Annotation[] annotations;

	/**
	 * Create annotations data set before each test execution
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void createAnnotationDataSet() throws JsonParseException, IOException {
		annotations = createMultipleTestAnnotations(TOTAL_IN_COLLECTION);
		assertEquals(TOTAL_IN_COLLECTION, annotations.length);
	}

	/**
	 * Delete annotations data set after each test execution
	 */
	@AfterEach
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
		AnnotationPage annPg = annSearchApi.searchAnnotations(VALUE_TESTSET, SearchProfiles.MINIMAL, null);
		assertNotNull(annPg, "AnnotationPage must not be null");
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

	
	 /* Test search query and verify search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchAnyAnnotation() throws Exception {

		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();

		// first page
		//to search for active annotations, the first parameter can be provided as, e.g. "q=*:*" or just "*:*"
//		AnnotationPage annPg = annSearchApi.searchAnnotations("*:*&fq=modified:[2019-11-24T16:10:49.624Z TO 2019-11-27T16:10:49.624Z]", SearchProfiles.STANDARD, null);
		AnnotationPage annPg = annSearchApi.searchAnnotations(VALUE_ALL, SearchProfiles.STANDARD, null);
		//to search for disabled(deleted) annotations, the parameter "disabled" must be provided
		//AnnotationPage annPg = annSearchApi.searchAnnotations("disabled=true&lastUpdate=25-11-2019", SearchProfiles.STANDARD);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there might be old annotations of failing tests in the database
		assertEquals(annPg.getCurrentPage(), 0);
		
		List<? extends Annotation> annos = annPg.getAnnotations(); 
		
		assertTrue(0 < annos.size());
	}
	
	
	@Test
	public void testSearchAnyAnnotationMinimalProfile() throws Exception {

		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// first page
		AnnotationPage annPg = annSearchApi.searchAnnotations("*", SearchProfiles.MINIMAL, null);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there might be old annotations of failing tests in the database
		assertEquals(annPg.getCurrentPage(), 0);
		
//		List<? extends Annotation> annos = annPg.getAnnotations(); 
//		assertTrue(0 < annos.size());
		
		
	}
}
