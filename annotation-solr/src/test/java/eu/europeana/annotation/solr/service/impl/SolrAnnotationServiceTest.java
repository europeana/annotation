package eu.europeana.annotation.solr.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.Resource;
//import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;

/**
 * Unit test for the SolrAnnotationImpl service
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-solr-context.xml", "/annotation-solr-test.xml" })
public class SolrAnnotationServiceTest {

	private static final String TEST_RESEARCH_ID = "1234"; 
	private static final String TEST_ANNOTATION_ID = "5678"; 
	private static final String TEST_ANNOTATION_ID_STR = "/" + TEST_RESEARCH_ID + "/" + TEST_ANNOTATION_ID;
	private static final String TEST_LANGUAGE   = "EN";
	private static final String TEST_LANGUAGE_2 = "DE";
	private static final String TEST_VALUE      = "OK";
	private static final String TEST_VALUE_2    = "SUPER";

    public static int generateUniqueId() {      
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;        
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

    @Resource
	SolrAnnotationService solrAnnotationService;
    
	/**
	 * enable this method only in development environment when required
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws AnnotationServiceException
	 */
	//@Test
	public void testCleanUpAllSolrAnnotations() throws MalformedURLException, IOException, AnnotationServiceException {
		
		cleanAllSolr();
	    
		/**
		 * query from SOLR
		 */
		 ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search("*:*");
		assertTrue(solrAnnotations.getResultSize() == 0);				
	}

	public void cleanAllSolr() throws AnnotationServiceException {
//		String baseUrl = ((HttpSolrServer) ((SolrAnnotationServiceImpl) solrAnnotationService).solrServer).getBaseURL();
//		//call this only on development machines
//		if(baseUrl.startsWith("http://127.0.1.1") || baseUrl.startsWith("http://localhost")){
			((SolrAnnotationServiceImpl) solrAnnotationService).cleanUpAll();
//		}
	}
	
	@Test
	public void testSolrAnnotationIdGeneration() throws MalformedURLException, IOException, AnnotationServiceException {
		
		SolrAnnotation solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnoUri(TEST_ANNOTATION_ID_STR);
		assertTrue(solrAnnotation.getAnnoUri().equals(TEST_ANNOTATION_ID_STR));
	}
	
	@Test
	public void testStoreSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> beans = solrAnnotationService.search(TEST_VALUE);
	    
		assertTrue(beans.getResultSize() > 0);
		
		/**
		 * Get the results 
		 */
        if (beans.getResultSize() > 0) {
			for (AnnotationView bean : beans.getResults()) {
	         //   Logger.getLogger(getClass().getName()).info(TEST_VALUE + ": " + bean.getLabel());
	            assertEquals(((SolrAnnotation)bean).getBodyValue(), TEST_VALUE);
			}
		}
	}

	private SolrAnnotationImpl storeTestObject() throws AnnotationServiceException{
		return storeTestObject(1);
	}
	
	private SolrAnnotation storeTestObjectByLabel(String label) throws AnnotationServiceException{
		return storeTestObject(1, label);
	}
	
	private SolrAnnotationImpl storeTestObject(int anoNr) throws AnnotationServiceException {
		SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnoUri("/testCollection/TestObject/"+anoNr);
//		solrAnnotation.setLanguage(TEST_LANGUAGE);
		solrAnnotation.setBodyValue(TEST_VALUE);

		/**
		 * save the SolrAnnotation object in SOLR
		 */
		solrAnnotationService.store(solrAnnotation);
		return solrAnnotation;
	}
	
	private SolrAnnotation storeTestObject(int anoNr, String label) throws AnnotationServiceException {
		SolrAnnotation solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnoUri("/testCollection/TestObject/"+anoNr);
//		solrAnnotation.setLanguage(TEST_LANGUAGE);
		solrAnnotation.setBodyValue(label);

		/**
		 * save the SolrAnnotation object in SOLR
		 */
		solrAnnotationService.store(solrAnnotation);
		return solrAnnotation;
	}
	
	@Test
	public void testSearchSolrAnnotationByLabel() throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create solr Annotation with new label
		 */
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.searchByLabel(TEST_VALUE);			   
		assertTrue(solrAnnotations.getResultSize() > 0);				

		/**
		 * Check the results 
		 */
		SolrAnnotation resSolrAnnotation = (SolrAnnotation) solrAnnotations.getResults().get(0);
		assertTrue(resSolrAnnotation.getBodyValue().equals(TEST_VALUE));
	}
	
//	@Test
//	public void testSearchSolrAnnotationByQueryObject() throws MalformedURLException, IOException, AnnotationServiceException {
//		
//		SolrAnnotation querySolrAnnotation = storeTestObject();
//		Logger.getLogger(getClass().getName()).info("searchbyobject after store: " + querySolrAnnotation);
//		
//		/**
//		 * query from SOLR
//		 */
//		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search(querySolrAnnotation);			   
//		assertTrue(solrAnnotations.getResultSize() > 0);				
//		Logger.getLogger(getClass().getName()).info("found annotations size: " + solrAnnotations.getResultSize());
//
//		/**
//		 * Check the results 
//		 */
//		SolrAnnotation resSolrAnnotation = (SolrAnnotation) solrAnnotations.getResults().get(0);
//		Logger.getLogger(getClass().getName()).info("searchbyobject res solrAnnotation object: " + resSolrAnnotation);
////		assertTrue(resSolrAnnotation.getLanguage().equals(querySolrAnnotation.getLanguage()));
//		assertTrue(resSolrAnnotation.getBodyValue().equals(querySolrAnnotation.getBodyValue()));
//	}
	
	@Test
	public void testUpdateSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();

//		printList(solrAnnotationService.getAll()
//				, "list at the beginning of the testUpdateSolrAnnotation for test value: " + TEST_VALUE);

		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search(TEST_VALUE);    
		assertTrue(solrAnnotations.getResultSize() > 0);
//		printList(solrAnnotationService.getAll(), "list before update for test value: " + TEST_VALUE);
				
		SolrAnnotation solrAnnotation = (SolrAnnotation) solrAnnotations.getResults().get(0);
		Logger.getLogger(getClass().getName()).info("SolrAnnotation object to update: " + solrAnnotation);
//		solrAnnotation.setLanguage(TEST_LANGUAGE_2);
				
		/**
		 * delete the SolrAnnotation object from SOLR
		 */
		solrAnnotationService.update(solrAnnotation);
		
		solrAnnotations = solrAnnotationService.search(TEST_VALUE);
		//printList(solrAnnotationService.getAll(), "list after update for test value: " + TEST_VALUE);

		/**
		 * query from SOLR
		 */
//		ResultSet<? extends AnnotationView> beans = solrAnnotationService.searchById(QueryParser.escape(
//				solrAnnotation.getAnnotationIdUrl()), SearchProfiles.STANDARD);
//	    
//		printList(solrAnnotationService.getAll(), "list after update after search by annotationIdString ...");

//		assertTrue(beans.size() > 0);
//		Logger.getLogger(getClass().getName()).info("solrAnnotations size: " + beans.size());

		/**
		 * Check the results 
		 */
//		SolrAnnotation updatedSolrAnnotation = beans.get(0);
//		Logger.getLogger(getClass().getName()).info("updatedSolrAnnotation: " + updatedSolrAnnotation);
//		assertTrue(updatedSolrAnnotation.getLanguage().equals(TEST_LANGUAGE_2));
	}
	
	@Test
	public void testSearchAllSolrAnnotations() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		storeTestObjectByLabel(TEST_VALUE_2);

		/**
		 * search for all SOLR Annotations
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = ((SolrAnnotationServiceImpl) solrAnnotationService).searchAll();
	    
		assertTrue(solrAnnotations.getResultSize() > 0);				
	}

	@Test
	public void testDeleteSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search(TEST_VALUE);
	    
		assertTrue(solrAnnotations.getResultSize() > 0);
		
		Logger.getLogger(getClass().getName()).info("test delete annotations size: " + solrAnnotations.getResultSize());
//		printList(solrAnnotations, "list to delete...");
		
		/**
		 * delete the SolrAnnotation object from SOLR
		 */
		solrAnnotationService.delete(solrAnnotations.getResults().get(0).getIdAsString());
		
//		printList(solrAnnotationService.getAll(), "list after delete method ...");

		/**
		 * query from SOLR
		 */
		solrAnnotations = solrAnnotationService.search(TEST_VALUE);
	    
		assertFalse(solrAnnotations.getResultSize() > 0);		
	}
	
	public void testSearchSolrAnnotationByLimit() throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create solr annotation with new label
		 */
		SolrAnnotation solrAnnotation = storeTestObjectByLabel(TEST_VALUE_2);
		storeTestObjectByLabel(TEST_VALUE);
		storeTestObjectByLabel(TEST_VALUE);
		Logger.getLogger(getClass().getName()).info("search by label with limit test: " +  solrAnnotation);

		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search(TEST_VALUE, "0", "2");			   
		assertTrue(solrAnnotations.getResultSize() == 2);				

		/**
		 * Check the results 
		 */
		SolrAnnotation resSolrAnnotation = (SolrAnnotation) solrAnnotations.getResults().get(0);
		assertTrue(resSolrAnnotation.getBodyValue().equals(TEST_VALUE));
	}
	
//	@Test
//	public void testSearchSolrAnnotationByMultilingualMapLabel() throws MalformedURLException, IOException, AnnotationServiceException {
//		
//		/**
//		 * Create solr Annotation with new label
//		 */
//		SolrAnnotationImpl solrAnnotation = storeTestObject();
//
//		/**
//		 * add multilingual labels
//		 */
//		solrAnnotation.addMultilingualValue("EN", "leaf");
//		solrAnnotation.addMultilingualValue("DE", "blatt");
//		
//		Logger.getLogger(getClass().getName()).info("Solr annotation before update. The multilingual map: " + 
//				solrAnnotation.getBodyMultilingualValue().toString());
//
//		solrAnnotationService.update(solrAnnotation);
//		
//		/**
//		 * query from SOLR by map key
//		 */
//		ResultSet<? extends AnnotationView> solrAnnotationsByMapKey = solrAnnotationService.searchByMapKey("EN_multilingual", "leaf");			   
//		assertTrue(solrAnnotationsByMapKey.getResultSize() > 0);				
//		//printListWithMap(solrAnnotationsByMapKey, "list with map after query...", true);
//	}
	
//	@Test
//	public void testQueryFacetSearch() throws AnnotationServiceException {
//		List<String> queries = new ArrayList<String>();
//		queries.add(SolrAnnotationFields.LABEL.getSolrAnnotationField() 
//				+ SolrAnnotationConst.DELIMETER
//				+ SolrAnnotationConst.STAR);
//		List<String> qfList = new ArrayList<String>();
//		qfList.add(SolrAnnotationFields.LANGUAGE.getSolrAnnotationField());
//		qfList.add(SolrAnnotationFields.BODY_TYPE.getSolrAnnotationField());
//		String[] qf = qfList.toArray(new String[qfList.size()]);
//		Assert.assertNotNull(solrAnnotationService.queryFacetSearch(
//				SolrAnnotationConst.ALL_SOLR_ENTRIES, qf, queries));
//	}

	public void printList(List<? extends SolrAnnotation> beans, String msg) {
		
		printListWithMap(beans, msg, false);
	}
		
	public void printListWithMap(List<? extends SolrAnnotation> beans, String msg, boolean withMap) {
		
		Logger.getLogger(getClass().getName()).info(msg);
		
		/**
		 * Get the results 
		 */
        if (beans.size() > 0) {
			for (SolrAnnotation bean : beans) {
				Logger.getLogger(getClass().getName()).info(bean.toString());
				if (withMap 
						&& ((SolrAnnotationImpl) bean).getBodyMultilingualValue() != null
						&& ((SolrAnnotationImpl) bean).getBodyMultilingualValue().size() > 0) {
					Logger.getLogger(getClass().getName()).info("multilingual map: " + 
							((SolrAnnotationImpl) bean).getBodyMultilingualValue().toString());
				}
			}
		}
	}
		
}
