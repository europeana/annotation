package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.utils.QueryUtils;

/**
 * Annotation search API test class
 * 
 * @author Sergiu Gordea @ait
 * @author Sven Schlarb @ait
 */
public class AnnotationSearchApiTest extends BaseAnnotationTest {

	static final String VALUE_ALL = "*:*";	
	static final int TOTAL_IN_PAGE = 10;
	static final int TOTAL_IN_COLLECTION = 21;
	public static final String TAG_STANDARD_TESTSET = "/tag/standard_testset.json";

	private Annotation[] annotations;

	private void createAnnotationDataSet() throws Exception {
	    String defaultRequestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TESTSET);
		annotations = createMultipleTestAnnotations(defaultRequestBody, TOTAL_IN_COLLECTION);
		assertEquals(TOTAL_IN_COLLECTION, annotations.length);
	}

	private void removeAnnotationDataSet() throws Exception {
	  for(Annotation anno: annotations) {
	    removeAnnotationManually(anno.getIdentifier());
	  }
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
	  createAnnotationDataSet();
	  // first page
	  String valueTestSet = "generator_uri:" + "\"" + AnnotationTestsConfiguration.getInstance().getPropAnnotationClientApiEndpoint() + "/" + "annotations" +"\"";
	  AnnotationPage annPg = searchAnnotationsAddQueryField(valueTestSet, null, null, null, null, SearchProfiles.MINIMAL, null);
	  assertNotNull(annPg, "AnnotationPage must not be null");
	  //there might be old annotations of failing tests in the database
	  assertTrue(TOTAL_IN_COLLECTION <= annPg.getTotalInCollection());
	  assertEquals(annPg.getCurrentPage(), 0);
	  assertEquals(TOTAL_IN_PAGE, annPg.getTotalInPage());
	  assertEquals(TOTAL_IN_PAGE, annPg.getItems().getResultSize());
	  assertNextPageNumber(annPg, 1);

	  // second page
	  String npUri = annPg.getNextPageUri();
	  //adapt the base url which is configured in the annotation.properties to use the one from the annotation-client.properties 
	  String annoNpUriNew = StringUtils.replace(npUri, StringUtils.substringBefore(npUri,"/search?"), AnnotationTestsConfiguration.getInstance().getServiceUri());
	  //here we need to decode the value returned 
	  annoNpUriNew = URLDecoder.decode(annoNpUriNew, StandardCharsets.UTF_8.toString());
	  String nextPageJson = mockMvc.perform(get(annoNpUriNew)).andReturn().getResponse().getContentAsString();
	  AnnotationPage secondAnnoPg = AnnotationTestUtils.getAnnotationPage(nextPageJson);
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
	  AnnotationPage lastPage = searchAnnotationsAddQueryField(valueTestSet, Integer.toString(lastPageNum), Integer.toString(TOTAL_IN_PAGE), null, null, SearchProfiles.STANDARD, null);
	  assertEquals(lastPage.getCurrentPage(), lastPageNum);
	  
	  removeAnnotationDataSet();
	}

	
	 /* Test search query and verify search result
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchAnyAnnotation() throws Exception {
	    createAnnotationDataSet();
		// first page
		//to search for active annotations, the first parameter can be provided as, e.g. "q=*:*" or just "*:*"
//		AnnotationPage annPg = annSearchApi.searchAnnotations("*:*&fq=modified:[2019-11-24T16:10:49.624Z TO 2019-11-27T16:10:49.624Z]", SearchProfiles.STANDARD, null);
		AnnotationPage annPg = searchAnnotationsAddQueryField(VALUE_ALL, null, null, null, null, SearchProfiles.STANDARD, null);
		//to search for disabled(deleted) annotations, the parameter "disabled" must be provided
		//AnnotationPage annPg = annSearchApi.searchAnnotations("disabled=true&lastUpdate=25-11-2019", SearchProfiles.STANDARD);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there might be old annotations of failing tests in the database
		assertEquals(annPg.getCurrentPage(), 0);
		
		List<? extends Annotation> annos = annPg.getAnnotations(); 
		
		assertTrue(0 < annos.size());
		
		removeAnnotationDataSet();
	}
	
	
	@Test
	public void testSearchAnyAnnotationMinimalProfile() throws Exception {
	    createAnnotationDataSet();
		// first page
		AnnotationPage annPg = searchAnnotationsAddQueryField("*", null, null, null, null, SearchProfiles.MINIMAL, null);
		assertNotNull(annPg, "AnnotationPage must not be null");
		//there might be old annotations of failing tests in the database
		assertEquals(annPg.getCurrentPage(), 0);
		removeAnnotationDataSet();
	}
}
