package eu.europeana.solr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//import org.easymock.EasyMock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.impl.SolrAnnotationServiceImpl;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * Unit test for the SolrAnnotationImpl service
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-solr-test.xml"})
@Disabled("needs configuration file")
public class SolrAnnotationServiceTest {

	private static final String TEST_RESEARCH_ID = "1234"; 
	private static final String TEST_ANNOTATION_ID = "5678"; 
	private static final String TEST_ANNOTATION_ID_STR = "/" + TEST_RESEARCH_ID + "/" + TEST_ANNOTATION_ID;
//	private static final String TEST_LANGUAGE   = "EN";
//	private static final String TEST_LANGUAGE_2 = "DE";
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
	   
    @Autowired
    @Qualifier(AnnotationConfiguration.BEAN_SOLR_ANNO_SERVICE) 
	SolrAnnotationService solrAnnotationService;
    
	/**
	 * enable this method only in development environment when required
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws AnnotationServiceException
	 */

	@Test
	public void testSolrAnnoIdGeneration() throws MalformedURLException, IOException, AnnotationServiceException {
		
		SolrAnnotation solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnoUri(TEST_ANNOTATION_ID_STR);
		assertTrue(solrAnnotation.getAnnoUri().equals(TEST_ANNOTATION_ID_STR));
	}
	
	@Test
	public void testStoreSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
	    storeTestObject(1, null);
		
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
	            solrAnnotationService.delete(((SolrAnnotation)bean).getAnnoUri());
			}
		}      
	}

	private SolrAnnotation storeTestObject(long identifier, String label) throws AnnotationServiceException {
      if(label==null) {
        label=TEST_VALUE;
      }
	  SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
      Date now = new Date();
      solrAnnotation.setGenerated(now);
      solrAnnotation.setCreated(now);
      solrAnnotation.setModified(now);
      solrAnnotation.setMotivation(MotivationTypes.TAGGING.getOaType());
      solrAnnotation.setTargetRecordIds(Collections.singletonList("/123/xyz"));
      solrAnnotation.setTargetUris(Collections.singletonList("http://data.europeana.eu/item/123/xyz"));
      solrAnnotation.setBodyValue(label);
      solrAnnotation.setBodyMultilingualValue(Collections.singletonMap(SolrAnnotationConstants.BODY_VALUE_PREFIX+"it", label));
      solrAnnotation.setAnnoUri("http://data.europeana.eu/annotation/"+identifier);
      solrAnnotation.setAnnoId(identifier);
      solrAnnotation.setCreatorUri("http://data.europeana.eu/user/44036c47-a40e-421b-9019-10146c970acb");
      solrAnnotation.setGeneratorUri("https://api.europeana.eu/apikey/annotations");
      solrAnnotation.setScenario(AnnotationScenarioTypes.SIMPLE_TAG);

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
		storeTestObject(1, null);
		
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
		
		solrAnnotationService.delete(resSolrAnnotation.getAnnoUri());
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
		
		storeTestObject(1, null);

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
		
		solrAnnotationService.delete(solrAnnotation.getAnnoUri());
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
		
	    SolrAnnotation storedAnno1 = storeTestObject(1, null);
	    SolrAnnotation storedAnno2 = storeTestObject(2, TEST_VALUE_2);

		/**
		 * search for all SOLR Annotations
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = ((SolrAnnotationServiceImpl) solrAnnotationService).searchAll();
	    
		assertTrue(solrAnnotations.getResultSize() > 0);
		
		solrAnnotationService.delete(storedAnno1.getAnnoUri());
		solrAnnotationService.delete(storedAnno2.getAnnoUri());
	}

	@Test
	public void testDeleteSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject(1, null);
		
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
		solrAnnotationService.delete(solrAnnotations.getResults().get(0).getIdentifierAsNumber());
		
//		printList(solrAnnotationService.getAll(), "list after delete method ...");

		/**
		 * query from SOLR
		 */
		solrAnnotations = solrAnnotationService.search(TEST_VALUE);
	    
		assertTrue(solrAnnotations.getResultSize() == 0);		
	}
	
	@Test
	public void testSearchSolrAnnotationByLimit() throws MalformedURLException, IOException, AnnotationServiceException {
		
		/**
		 * Create solr annotation with new label
		 */
		SolrAnnotation storedAnno1 = storeTestObject(1, TEST_VALUE_2);
		SolrAnnotation storedAnno2 = storeTestObject(2, TEST_VALUE);
		Logger.getLogger(getClass().getName()).info("search by label with limit test: " +  storedAnno1);

		/**
		 * query from SOLR
		 */
		ResultSet<? extends AnnotationView> solrAnnotations = solrAnnotationService.search(TEST_VALUE, "0", "1");			   
		assertTrue(solrAnnotations.getResultSize() == 1);				

		/**
		 * Check the results 
		 */
		SolrAnnotation resSolrAnnotation = (SolrAnnotation) solrAnnotations.getResults().get(0);
		assertTrue(resSolrAnnotation.getBodyValue().equals(TEST_VALUE));
		
		solrAnnotationService.delete(storedAnno1.getAnnoUri());
		solrAnnotationService.delete(storedAnno2.getAnnoUri());
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
