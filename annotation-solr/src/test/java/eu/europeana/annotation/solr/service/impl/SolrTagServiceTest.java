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
import static org.junit.Assert.assertNotNull;
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

import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
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
		Logger.getLogger(getClass().getName()).info("search by label test: " +  solrTag);

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
		Logger.getLogger(getClass().getName()).info("searchbyobject res solrtag object: " + resSolrTag);
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
		Logger.getLogger(getClass().getName()).info("solrTags size: " + beans.size());

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
		
	@Test
	public void testSearchSolrTagByTagId() throws MalformedURLException, IOException, TagServiceException {
		
		/**
		 * Create solr tag with new label and check the tag_id
		 */
		SolrTagImpl solrTag = storeTestObjectByLabel(TEST_VALUE_2);
		Logger.getLogger(getClass().getName()).info("search by label test: " +  solrTag);
		assertNotNull(solrTag.getId());
		
		/**
		 * query from SOLR
		 */
		List<? extends SolrTag> solrTags = solrTagService.search(solrTag.getId());			   
		assertTrue(solrTags.size() > 0);				
	}
	
	@Test
	public void testSearchSolrTagByMultilingualMapLabel() throws MalformedURLException, IOException, TagServiceException {
		
		/**
		 * Create solr Tag with new label
		 */
		SolrTagImpl solrTag = storeTestObject();

		/**
		 * add multilingual labels
		 */
		solrTag.addLabelInMapping("EN", "leaf");
		solrTag.addLabelInMapping("DE", "blatt");
		
		Logger.getLogger(getClass().getName()).info("Solr Tag before update. The multilingual map: " + 
				solrTag.getMultilingual().toString());

		solrTagService.update(solrTag);
		
		/**
		 * query from SOLR by map key
		 */
		List<? extends SolrTag> solrTagsByMapKey = solrTagService.searchByMapKey("EN_multilingual", "leaf");			   
		assertTrue(solrTagsByMapKey.size() > 0);				
		printListWithMap(solrTagsByMapKey, "list with map after query...", true);
	}
	
	public void printList(List<? extends SolrTag> beans, String msg) {
		
		printListWithMap(beans, msg, false);
	}
		
	public void printListWithMap(List<? extends SolrTag> beans, String msg, boolean withMap) {
		
		Logger.getLogger(getClass().getName()).info(msg);
		
		/**
		 * Get the results 
		 */
        if (beans.size() > 0) {
			for (SolrTag bean : beans) {
				Logger.getLogger(getClass().getName()).info(bean.toString());
				if (withMap 
						&& ((SolrTagImpl) bean).getMultilingual() != null
						&& ((SolrTagImpl) bean).getMultilingual().size() > 0) {
					Logger.getLogger(getClass().getName()).info("multilingual map: " + 
							((SolrTagImpl) bean).getMultilingual().toString());
				}
			}
		}
	}
}
