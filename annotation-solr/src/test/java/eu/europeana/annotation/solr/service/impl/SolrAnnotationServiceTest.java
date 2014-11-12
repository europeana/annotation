/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
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

import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Before;
//import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    
	
	@Test
	public void testCleanUpAllSolrAnnotations() throws MalformedURLException, IOException, AnnotationServiceException {
		
		cleanAllSolr();
	    
		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.search("*:*");
		assertTrue(solrAnnotations.size() == 0);				
	}

	@Before
	public void cleanAllSolr() throws AnnotationServiceException {
		/**
		 * clean up all SOLR Annotations
		 */
		((SolrAnnotationServiceImpl) solrAnnotationService).cleanUpAll();
	}
	
	@Test
	public void testSolrAnnotationIdGeneration() throws MalformedURLException, IOException, AnnotationServiceException {
		
		SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnotationIdString(TEST_ANNOTATION_ID_STR);
		assertTrue(solrAnnotation.getAnnotationIdString().equals(TEST_ANNOTATION_ID_STR));
	}
	
	@Test
	public void testStoreSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> beans = solrAnnotationService.search(TEST_VALUE);
	    
		assertTrue(beans.size() > 0);
		
		/**
		 * Get the results 
		 */
        if (beans.size() > 0) {
			for (SolrAnnotation bean : beans) {
	            Logger.getLogger(getClass().getName()).info(TEST_VALUE + ": " + bean.getLabel());
	            assertEquals(bean.getLabel(), TEST_VALUE);
			}
		}
	}

	private SolrAnnotationImpl storeTestObject() throws AnnotationServiceException{
		return storeTestObject(1);
	}
	
	private SolrAnnotationImpl storeTestObjectByLabel(String label) throws AnnotationServiceException{
		return storeTestObject(1, label);
	}
	
	private SolrAnnotationImpl storeTestObject(int anoNr) throws AnnotationServiceException {
		SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnotationIdString("/testCollection/TestObject/"+anoNr);
		solrAnnotation.setLanguage(TEST_LANGUAGE);
		solrAnnotation.setLabel(TEST_VALUE);

		/**
		 * save the SolrAnnotation object in SOLR
		 */
		solrAnnotationService.store(solrAnnotation);
		return solrAnnotation;
	}
	
	private SolrAnnotationImpl storeTestObject(int anoNr, String label) throws AnnotationServiceException {
		SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
		solrAnnotation.setAnnotationIdString("/testCollection/TestObject/"+anoNr);
		solrAnnotation.setLanguage(TEST_LANGUAGE);
		solrAnnotation.setLabel(label);

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
//		SolrAnnotationImpl solrAnnotation = new SolrAnnotationImpl();
//		solrAnnotation.setAnnotationIdString(
//				"/" + Integer.toString(generateUniqueId()) + "/" + Integer.toString(generateUniqueId()));
//		solrAnnotation.setLanguage(TEST_LANGUAGE);
//		solrAnnotation.setLabel(TEST_VALUE_2);

		/**
		 * save the SolrAnnotation object in SOLR
		 */
//		solrAnnotationService.store(solrAnnotation);
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.searchByLabel(TEST_VALUE);			   
//		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.searchByLabel(TEST_VALUE_2);			   
		assertTrue(solrAnnotations.size() > 0);				

		/**
		 * Check the results 
		 */
		SolrAnnotation resSolrAnnotation = solrAnnotations.get(0);
		assertTrue(resSolrAnnotation.getLabel().equals(TEST_VALUE));
//		assertTrue(resSolrAnnotation.getLabel().equals(TEST_VALUE_2));
	}
	
	@Test
	public void testSearchSolrAnnotationByQueryObject() throws MalformedURLException, IOException, AnnotationServiceException {
		
		SolrAnnotationImpl querySolrAnnotation = storeTestObject();
		System.out.println("searchbyobject after store: " + querySolrAnnotation);
		//		SolrAnnotationImpl querySolrAnnotation = new SolrAnnotationImpl();
//		querySolrAnnotation.setAnnotationIdString(
//				"/" + Integer.toString(generateUniqueId()) + "/" + Integer.toString(generateUniqueId()));
//		querySolrAnnotation.setLanguage(TEST_LANGUAGE);
//		querySolrAnnotation.setLabel(TEST_VALUE);
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.search(querySolrAnnotation);			   
		assertTrue(solrAnnotations.size() > 0);				
		System.out.println("found annotations size: " + solrAnnotations.size());

		/**
		 * Check the results 
		 */
		SolrAnnotation resSolrAnnotation = solrAnnotations.get(0);
		System.out.println("searchbyobject res solrAnnotation object: " + resSolrAnnotation);
		assertTrue(resSolrAnnotation.getLanguage().equals(querySolrAnnotation.getLanguage()));
		assertTrue(resSolrAnnotation.getLabel().equals(querySolrAnnotation.getLabel()));
	}
	
	@Test
	public void testUpdateSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		SolrAnnotationImpl querySolrAnnotation = storeTestObject();

		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.search(TEST_VALUE);    
		assertTrue(solrAnnotations.size() > 0);
//		printList(solrAnnotations, "list before update for test value: " + TEST_VALUE);
		printList(solrAnnotationService.getAll(), "list before update for test value: " + TEST_VALUE);
				
		SolrAnnotation solrAnnotation = solrAnnotations.get(0);
		System.out.println("SolrAnnotation object to update: " + solrAnnotation);
		solrAnnotation.setLanguage(TEST_LANGUAGE_2);
				
		/**
		 * delete the SolrAnnotation object from SOLR
		 */
		solrAnnotationService.update(solrAnnotation);
		
		solrAnnotations = solrAnnotationService.search(TEST_VALUE);
		printList(solrAnnotationService.getAll(), "list after update for test value: " + TEST_VALUE);
//		printList(solrAnnotations, "list after update for test value: " + TEST_VALUE);

		/**
		 * query from SOLR
		 */
//		List<? extends SolrAnnotation> beans = solrAnnotationService.searchById(solrAnnotation.getResourceId());
//		List<? extends SolrAnnotation> beans = solrAnnotationService.searchById(solrAnnotation.getAnnotationIdString());
		List<? extends SolrAnnotation> beans = solrAnnotationService.searchById(QueryParser.escape(
				solrAnnotation.getAnnotationIdString()));
//		List<? extends SolrAnnotation> beans = solrAnnotationService.search(TEST_VALUE);
	    
		printList(solrAnnotationService.getAll(), "list after update after search by annotationIdString ...");
//		printList(beans, "list after update after search by annotationIdString ...");

		assertTrue(beans.size() > 0);
		System.out.println("solrAnnotations size: " + beans.size());

		/**
		 * Check the results 
		 */
		SolrAnnotation updatedSolrAnnotation = beans.get(0);
		System.out.println("updatedSolrAnnotation: " + updatedSolrAnnotation);
		assertTrue(updatedSolrAnnotation.getLanguage().equals(TEST_LANGUAGE_2));
	}
	
	@Test
	public void testSearchAllSolrAnnotations() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		storeTestObjectByLabel(TEST_VALUE_2);

		/**
		 * search for all SOLR Annotations
		 */
		List<? extends SolrAnnotation> solrAnnotations = ((SolrAnnotationServiceImpl) solrAnnotationService).searchAll();
	    
		assertTrue(solrAnnotations.size() > 0);				
	}

	@Test
	public void testDeleteSolrAnnotation() throws MalformedURLException, IOException, AnnotationServiceException {
		
		storeTestObject();
		
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrAnnotation> solrAnnotations = solrAnnotationService.search(TEST_VALUE);
	    
		assertTrue(solrAnnotations.size() > 0);
		
		System.out.println("test delete annotations size: " + solrAnnotations.size());
		printList(solrAnnotations, "list to delete...");
		
		/**
		 * delete the SolrAnnotation object from SOLR
		 */
		solrAnnotationService.delete(solrAnnotations.get(0));
		
		printList(solrAnnotationService.getAll(), "list after delete method ...");

		/**
		 * query from SOLR
		 */
		solrAnnotations = solrAnnotationService.search(TEST_VALUE);
	    
		assertFalse(solrAnnotations.size() > 0);		
	}
	
	public void printList(List<? extends SolrAnnotation> beans, String msg) {
		
		System.out.println(msg);
		
		/**
		 * Get the results 
		 */
        if (beans.size() > 0) {
			for (SolrAnnotation bean : beans) {
				System.out.println(bean);
			}
		}
	}
		
}
