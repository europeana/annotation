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




import org.junit.Before;
//import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
//import eu.europeana.corelib.logging.Logger;
//import com.google.code.morphia.Datastore;
//import com.google.code.morphia.Key;
//import com.google.code.morphia.query.Query;
//
//import eu.europeana.corelib.definitions.jibx.AgentType;
//import eu.europeana.corelib.definitions.jibx.AltLabel;
//import eu.europeana.corelib.definitions.jibx.Begin;
//import eu.europeana.corelib.definitions.jibx.End;
//import eu.europeana.corelib.definitions.jibx.LiteralType.Lang;
//import eu.europeana.corelib.definitions.jibx.Note;
//import eu.europeana.corelib.definitions.jibx.PrefLabel;
//import eu.europeana.corelib.definitions.solr.beans.BriefBean;
//import eu.europeana.corelib.solr.bean.impl.BriefBeanImpl;
import eu.europeana.annotation.solr.service.SolrTagService;

/**
 * Unit test for the SolrBeanImpl field input creator
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-solr-context.xml", "/annotation-solr-test.xml" })
public class SolrTagServiceTest {

	private static final String TEST_CREATOR    = "Musterman";
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
	SolrTagService solrTagService;
    
	
	@Before
	public void cleanAllSolr() throws TagServiceException {
		/**
		 * clean up all SOLR Tags
		 */
		((SolrTagServiceImpl) solrTagService).cleanUpAll();
	}
	
	@Test
	public void testCleanUpAllSolrTags() throws MalformedURLException, IOException, TagServiceException {
		
		/**
		 * clean up all SOLR Tags
		 */
		((SolrTagServiceImpl) solrTagService).cleanUpAll();
	    
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.search(TEST_VALUE);
		assertTrue(solrTags.size() == 0);				
	}
	
	private SolrTagImpl storeTestObject() throws TagServiceException{
		return storeTestObject(1);
	}
	
	private SolrTagImpl storeTestObjectByLabel(String label) throws TagServiceException{
		return storeTestObject(1, label);
	}
	
	private SolrTagImpl storeTestObject(int anoNr) throws TagServiceException {
		SolrTagImpl solrTag = new SolrTagImpl();
		solrTag.setId(Integer.toString(anoNr));
		solrTag.setCreator(TEST_CREATOR);
		solrTag.setLanguage(TEST_LANGUAGE);
		solrTag.setLabel(TEST_VALUE);

		/**
		 * save the SolrTag object in SOLR
		 */
		solrTagService.store(solrTag);
		return solrTag;
	}
	
	private SolrTagImpl storeTestObject(int anoNr, String label) throws TagServiceException {
		SolrTagImpl solrTag = new SolrTagImpl();
		solrTag.setId(Integer.toString(generateUniqueId()));
		solrTag.setCreator(TEST_CREATOR);
		solrTag.setLanguage(TEST_LANGUAGE);
		solrTag.setLabel(label);

		/**
		 * save the SolrTag object in SOLR
		 */
		solrTagService.store(solrTag);
		return solrTag;
	}
	
	
	@Test
	public void testStoreSolrTag() throws MalformedURLException, IOException, TagServiceException {
		
		SolrTagImpl solrTag = storeTestObject();

		/**
		 * save the SolrTag object in SOLR
		 */
		solrTagService.store(solrTag);
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> beans = solrTagService.search(TEST_VALUE);
	    
		assertTrue(beans.size() > 0);
		
		/**
		 * Get the results 
		 */
        if (beans.size() > 0) {
			for (SolrTag bean : beans) {
	            Logger.getLogger(getClass().getName()).info(TEST_CREATOR + ": " + bean.getCreator());
	            Logger.getLogger(getClass().getName()).info(TEST_CREATOR + ": " + bean.getCreator());
	            assertEquals(bean.getCreator(), TEST_CREATOR);
			}
		}
	}
	
	@Test
	public void testSearchSolrTagByLabel() throws MalformedURLException, IOException, TagServiceException {
		
		/**
		 * Create solr tag with new label
		 */
		SolrTagImpl solrTag = storeTestObjectByLabel(TEST_VALUE_2);
		System.out.println("search by label test: " +  solrTag);

		/**
		 * save the SolrTag object in SOLR
		 */
//		solrTagService.store(solrTag);
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.searchByLabel(TEST_VALUE_2);			   
		assertTrue(solrTags.size() > 0);				

		/**
		 * Check the results 
		 */
		SolrTag resSolrTag = solrTags.get(0);
		assertTrue(resSolrTag.getLabel().equals(TEST_VALUE_2));
	}
	
	@Test
	public void testSearchSolrTagByQueryObject() throws MalformedURLException, IOException, TagServiceException {
		
		SolrTagImpl querySolrTag = storeTestObject();
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.search(querySolrTag);			   
		assertTrue(solrTags.size() > 0);				

		/**
		 * Check the results 
		 */
		SolrTag resSolrTag = solrTags.get(0);
		System.out.println("searchbyobject res solrtag object: " + resSolrTag);
		assertTrue(resSolrTag.getCreator().equals(querySolrTag.getCreator()));
		assertTrue(resSolrTag.getLanguage().equals(querySolrTag.getLanguage()));
		assertTrue(resSolrTag.getLabel().equals(querySolrTag.getLabel()));
	}
	
	@Test
	public void testUpdateSolrTag() throws MalformedURLException, IOException, TagServiceException {
		
		storeTestObject();
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.search(TEST_VALUE);
	    
		assertTrue(solrTags.size() > 0);
				
		SolrTag solrTag = solrTags.get(0);
		solrTag.setLanguage(TEST_LANGUAGE_2);
		
		/**
		 * delete the SolrTag object from SOLR
		 */
		solrTagService.update(solrTag);
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> beans = solrTagService.search(TEST_VALUE);
	    
		assertTrue(beans.size() > 0);
		System.out.println("solrTags size: " + beans.size());

		/**
		 * Check the results 
		 */
		SolrTag updatedSolrTag = beans.get(0);
		assertTrue(updatedSolrTag.getLanguage().equals(TEST_LANGUAGE_2));
	}
	
	@Test
	public void testSearchAllSolrTags() throws MalformedURLException, IOException, TagServiceException {
		
		storeTestObject(generateUniqueId());
		storeTestObject(generateUniqueId());
		
		/**
		 * search for all SOLR Tags
		 */
		List<? extends SolrTag> solrTags = ((SolrTagServiceImpl) solrTagService).searchAll();
	    
		assertTrue(solrTags.size() > 0);				
	}

	@Test
	public void testDeleteSolrTag() throws MalformedURLException, IOException, TagServiceException {
		
		storeTestObject(generateUniqueId());

		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.search(TEST_VALUE);
	    
		assertTrue(solrTags.size() > 0);
				
		/**
		 * delete the SolrTag object from SOLR
		 */
		solrTagService.delete(solrTags.get(0));
		
		/**
		 * query from SOLR
		 */
		solrTags = solrTagService.search(TEST_VALUE);
	    
		assertFalse(solrTags.size() > 0);		
	}
		
}
