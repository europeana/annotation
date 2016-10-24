package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.impl.TagResource;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.QueryUtils;
import eu.europeana.annotation.utils.parse.AnnotationPageParser;

/**
 * TODO: rewrite
 * 
 * @author Sergiu Gordea @ait
 * @author Sven Schlarb @ait
 */
public class AnnotationSearchApiTest extends BaseWebAnnotationProtocolTest {
	
	// http://localhost:8080/annotation/search?wskey=apidemo&query=*%3A*
	protected Logger log = Logger.getLogger(getClass());

	public static String VALUE     = "Vlad";
	public static String VALUE_ALL = "*:*";
	public static String START_ON  = "0";
	public static String LIMIT     = "10";
	public static String FIELD     = "multilingual";
	public static String LANGUAGE  = "ro";	
	
	public static final int TOTAL_IN_PAGE = 10;
	public static final int TOTAL_IN_COLLECTION = 21;
		

	@Test
	public void searchAnnotation() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE_ALL);
		
		assertNotNull("AnnotationPage must not be null", annotationPage);
		assertEquals("First page must be 0", annotationPage.getCurrentPage(), 0);
	}
	
	private void assertCollectionAndPageSize(AnnotationPage annotationPage) {
		assertNotNull("AnnotationPage must not be null", annotationPage);
		assertEquals("First page must be 0", annotationPage.getCurrentPage(), 0);
		assertEquals(TOTAL_IN_COLLECTION, annotationPage.getTotalInCollection());
		assertEquals(TOTAL_IN_PAGE, annotationPage.getTotalInPage());
		assertEquals(TOTAL_IN_PAGE, annotationPage.getItems().getResultSize());
	}	

	
	private void assertNextPageUri(AnnotationPage annotationPage, Integer expectedPageNum) throws MalformedURLException {
		// http://localhost:8080/annotation/search?wskey=apidemo&query=*%3A*&page=1&pageSize=10
		String nextPageUri = annotationPage.getNextPageUri();
		Integer nextPageNum = QueryUtils.getQueryParamNumValue(nextPageUri, WebAnnotationFields.PAGE);
		assertEquals(expectedPageNum, nextPageNum);
		Integer pageSizeNum = QueryUtils.getQueryParamNumValue(nextPageUri, WebAnnotationFields.PARAM_PAGE_SIZE);
		assertEquals(new Integer(TOTAL_IN_PAGE), pageSizeNum);
	}
	

	@Test
	public void testSearchAnnotationPaging() throws Exception {
		
		// create
		Annotation[] annotations = createMultipleTestAnnotations(TOTAL_IN_COLLECTION);
		assertEquals(TOTAL_IN_COLLECTION, annotations.length);
		
		// search
		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		AnnotationPageParser annoPageParser = new AnnotationPageParser();
		
		AnnotationPage annotationPage = annotationSearchApi.searchAnnotations(VALUE_ALL);
		
		assertCollectionAndPageSize(annotationPage);
		assertNextPageUri(annotationPage, 1);
		
		String npUri = annotationPage.getNextPageUri();
		String nextPageJson = annotationSearchApi.getApiConnection().getHttpConnection().getURLContent(npUri);
		
		log.debug(nextPageJson);

		AnnotationPage nextAnnoPage = annoPageParser.parseAnnotationPage(nextPageJson);
		assertNotNull(nextAnnoPage);
		
		Integer[] annotationIds = getNumericAnnotationIds(annotations);
		assertEquals(TOTAL_IN_COLLECTION, annotationIds.length);
		this.deleteAnnotations(annotationIds);
		
	}

//	@Test
	public void searchAnnotationByLimit() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, null, null);
		
		assertNotNull(annotationPage);
		//assertTrue(annotationList.size() > 0);
	}

//	@Test
	public void searchAnnotationByLanguage() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, FIELD, LANGUAGE);
		
		assertNotNull(annotationPage);
		//assertTrue(annotationList.size() > 0);
	}
	
	
}
