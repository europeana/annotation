/*
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.skos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.utils.concept.SkosUtils;

/**
 * This class implements different SKOS testing scenarios.
 */
public class AnnotationSkosTest {

	public final String ALT_LABEL_KEY       = "http://www.w3.org/2004/02/skos/core#altLabel1_altLabel";
	public final String ALT_LABEL_VALUE     = "\"canal bends\"";
	public final String TEST_RDF_FILE_PATH  = "D:/git/annotation/annotation-web/src/test/resources/test.rdf";
	
	public final String TEST_BROADER_VALUE  = "http://data.europeana.eu/concept/music/genre/Art_music";
	public final String TEST_PREV_LABEL_KEY = "http://www.w3.org/2004/02/skos/core#prefLabel0_prefLabel";
	public final String TEST_PREV_LABEL_VALUE = "\"Music theatre\"@en";
	public final String TEST_RDF_COLLECTION_FILE_PATH  = "D:/git/annotation/annotation-web/src/test/resources/Music-Genres.rdf";

	public final String TEST_PREV_LABEL_I18N_KEY_19 = "http://www.w3.org/2004/02/skos/core#prefLabel19_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_19 = "\"Musikal\"@sv";
	public final String TEST_PREV_LABEL_I18N_KEY_30 = "http://www.w3.org/2004/02/skos/core#prefLabel30_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_30 = "\"Muzikál\"@cs";
	public final String TEST_PREV_LABEL_I18N_KEY_28 = "http://www.w3.org/2004/02/skos/core#prefLabel28_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_28 = "\"Teater musikal\"@id";
	public final String TEST_PREV_LABEL_I18N_KEY_22 = "http://www.w3.org/2004/02/skos/core#prefLabel22_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_22 = "\"Müzikal tiyatro\"@tr";
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel31_prefLabel=\"Mjuzikl\"@hr,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel1_prefLabel=\"Teatru muzical\"@ro,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel9_prefLabel=\"Comédie musicale\"@fr,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel25_prefLabel=\"Music theatre\"@en,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel27_prefLabel=\"Μιούζικαλ\"@el,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel20_prefLabel=\"Musical\"@pl,"
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel15_prefLabel=\"Musical\"@de," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel21_prefLabel=\"Musikaali\"@fi," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel11_prefLabel=\"Мюзикл\"@ru," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel6_prefLabel=\"Teatre musical\"@ca," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel33_prefLabel=\"뮤지컬\"@ko," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel16_prefLabel=\"Musical\"@hu," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel12_prefLabel=\"Мюзикл\"@uk," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel18_prefLabel=\"Musikal\"@no," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel32_prefLabel=\"Muusikal\"@et," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel29_prefLabel=\"Muzikál\"@sk," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel0_prefLabel=\"Muzikal\"@sl," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel3_prefLabel=\"Teatro musical\"@pt," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel7_prefLabel=\"Nhạc kịch\"@vi," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel5_prefLabel=\"تئاتر موزیکال\"@fa," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel4_prefLabel=\"Teatro musical\"@es," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel17_prefLabel=\"Musical\"@it," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel24_prefLabel=\"Музикален театър\"@bg," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel10_prefLabel=\"Muzikinis teatras\"@lt," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel8_prefLabel=\"ละครเพลง\"@th," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel13_prefLabel=\"Musical\"@da," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel14_prefLabel=\"Musical\"@nl," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel26_prefLabel=\"ミュージカル\"@ja," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel23_prefLabel=\"Мјузикл\"@sr," 
//			+ " http://www.w3.org/2004/02/skos/core#prefLabel2_prefLabel=\"مسرح موسيقي\"@ar";
	public final String TEST_RDF_COLLECTION_I18N_FILE_PATH  = "D:/git/annotation/annotation-web/src/test/resources/Music-Genres-i18n.rdf";

	
	private SkosUtils skosUtils = null;

    
	@Before
    public void setUp() throws Exception {
		setSkosUtils(new SkosUtils());
    }

    @After
    public void tearDown() throws Exception {
    }

        	    
	public SkosUtils getSkosUtils() {
		return skosUtils;
	}

	public void setSkosUtils(SkosUtils skosUtils) {
		this.skosUtils = skosUtils;
	}
	

	/**
     * This test performs parsing of the SKOS RDF in XML format to Europeana Annotation Concept object using Jena library.
     */
//    @Test
    public void testParseSkosRdfXmlToConcept() {
    	
    	Concept concept = getSkosUtils().parseSkosRdfXmlToConcept(TEST_RDF_FILE_PATH);    	
    	assertNotNull(concept);
    	assertNotNull(concept.getAltLabel());
    	System.out.println(concept.toString());
    	String value = concept.getAltLabel().get(ALT_LABEL_KEY);
    	assertNotNull(value);
    	assertEquals(value, ALT_LABEL_VALUE);
    }


    /**
     * This test performs parsing of the SKOS RDF in XML format to Europeana Annotation Concept collection using Jena library.
     */
//    @Test
    public void testParseSkosRdfXmlToConceptCollection() {
    	
    	List<Concept> concepts = getSkosUtils().parseSkosRdfXmlToConceptCollection(TEST_RDF_COLLECTION_FILE_PATH);    	
    	assertTrue(concepts.size() == 430);
    	Concept concept = concepts.get(0);
    	assertNotNull(concept);
    	System.out.println(concept.toString());
    	String broaderValue = concept.getBroader().get(0);
    	assertEquals(broaderValue, TEST_BROADER_VALUE);
    	String prevLabelValue = concept.getPrefLabel().get(TEST_PREV_LABEL_KEY);
    	assertNotNull(prevLabelValue);
    	assertEquals(prevLabelValue, TEST_PREV_LABEL_VALUE);
    }

    
    /**
     * This test performs parsing of the SKOS RDF in XML format to Europeana Annotation Concept collection with
     * internationalization using Jena library.
     */
    @Test
    public void testParseSkosRdfXmlToConceptCollectionWithInternationalization() {
    	
    	List<Concept> concepts = getSkosUtils().parseSkosRdfXmlToConceptCollection(TEST_RDF_COLLECTION_I18N_FILE_PATH);    	
    	assertTrue(concepts.size() == 432);
    	Concept concept = concepts.get(0);
    	assertNotNull(concept);
    	System.out.println(concept.toString());
    	String broaderValue = concept.getBroader().get(0);
    	assertEquals(broaderValue, TEST_BROADER_VALUE);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_19), TEST_PREV_LABEL_I18N_VALUE_19);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_30), TEST_PREV_LABEL_I18N_VALUE_30);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_28), TEST_PREV_LABEL_I18N_VALUE_28);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_22), TEST_PREV_LABEL_I18N_VALUE_22);
    }

    
}
