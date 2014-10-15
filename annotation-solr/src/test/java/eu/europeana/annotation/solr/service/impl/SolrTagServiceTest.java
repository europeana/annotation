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

import eu.europeana.annotation.solr.exceptions.TagServiceException;
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

	private static final String TEST_CREATOR  = "Musterman";
	private static final String TEST_LANGUAGE = "EN";
	private static final String TEST_VALUE    = "OK";

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
    
	@Test
	public void testSolrBean() throws MalformedURLException, IOException, TagServiceException{
		
		SolrTagImpl solrTag = new SolrTagImpl();
		solrTag.setId(Integer.toString(generateUniqueId()));
		solrTag.setCreator(TEST_CREATOR);
		solrTag.setLanguage(TEST_LANGUAGE);
		solrTag.setLabel(TEST_VALUE);

	
		
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
	
}
