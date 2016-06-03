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
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.utils.concept.SkosUtils;

/**
 * @deprecated skos vocabularies will be managed though entity-collection
 * This class implements different SKOS testing scenarios.
 */
@Ignore
public class AnnotationSkosTest {

	public final String ALT_LABEL_KEY       = "http://www.w3.org/2004/02/skos/core#altLabel1_altLabel";
	public final String ALT_LABEL_VALUE     = "\"canal bends\"";
	public final String TEST_RDF_FILE_PATH  = "D:/git/annotation/annotation-web/src/test/resources/test.rdf";
	
	public final String TEST_BROADER_VALUE  = "http://data.europeana.eu/concept/music/genre/Art_music";
	public final String TEST_PREV_LABEL_KEY = "http://www.w3.org/2004/02/skos/core#prefLabel0_prefLabel";
	public final String TEST_PREV_LABEL_VALUE = "\"Music theatre\"@en";
	public final String TEST_RDF_COLLECTION_FILE_PATH  = "D:/git/annotation/annotation-web/src/test/resources/Music-Genres.rdf";

	public final String TEST_PREV_LABEL_I18N_KEY_0 = "http://www.w3.org/2004/02/skos/core#prefLabel0_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_0 = "\"Muzikal\"@sl";
	public final String TEST_PREV_LABEL_I18N_KEY_1 = "http://www.w3.org/2004/02/skos/core#prefLabel1_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_1 = "\"Teatru muzical\"@ro";
	public final String TEST_PREV_LABEL_I18N_KEY_2 = "http://www.w3.org/2004/02/skos/core#prefLabel2_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_2 = "\"مسرح موسيقي\"@ar";
	public final String TEST_PREV_LABEL_I18N_KEY_3 = "http://www.w3.org/2004/02/skos/core#prefLabel3_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_3 = "\"Teatro musical\"@pt";
	public final String TEST_PREV_LABEL_I18N_KEY_4 = "http://www.w3.org/2004/02/skos/core#prefLabel4_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_4 = "\"Teatro musical\"@es";
	public final String TEST_PREV_LABEL_I18N_KEY_5 = "http://www.w3.org/2004/02/skos/core#prefLabel5_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_5 = "\"تئاتر موزیکال\"@fa";
	public final String TEST_PREV_LABEL_I18N_KEY_6 = "http://www.w3.org/2004/02/skos/core#prefLabel6_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_6 = "\"Teatre musical\"@ca";
	public final String TEST_PREV_LABEL_I18N_KEY_7 = "http://www.w3.org/2004/02/skos/core#prefLabel7_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_7 = "\"Nhạc kịch\"@vi";
	public final String TEST_PREV_LABEL_I18N_KEY_8 = "http://www.w3.org/2004/02/skos/core#prefLabel8_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_8 = "\"ละครเพลง\"@th";
	public final String TEST_PREV_LABEL_I18N_KEY_9 = "http://www.w3.org/2004/02/skos/core#prefLabel9_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_9 = "\"Comédie musicale\"@fr";
	public final String TEST_PREV_LABEL_I18N_KEY_10 = "http://www.w3.org/2004/02/skos/core#prefLabel10_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_10 = "\"Muzikinis teatras\"@lt";
	public final String TEST_PREV_LABEL_I18N_KEY_11 = "http://www.w3.org/2004/02/skos/core#prefLabel11_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_11 = "\"Мюзикл\"@ru";
	public final String TEST_PREV_LABEL_I18N_KEY_12 = "http://www.w3.org/2004/02/skos/core#prefLabel12_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_12 = "\"Мюзикл\"@uk";
	public final String TEST_PREV_LABEL_I18N_KEY_13 = "http://www.w3.org/2004/02/skos/core#prefLabel13_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_13 = "\"Musical\"@da";
	public final String TEST_PREV_LABEL_I18N_KEY_14 = "http://www.w3.org/2004/02/skos/core#prefLabel14_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_14 = "\"Musical\"@nl";
	public final String TEST_PREV_LABEL_I18N_KEY_15 = "http://www.w3.org/2004/02/skos/core#prefLabel15_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_15 = "\"Musical\"@de";
	public final String TEST_PREV_LABEL_I18N_KEY_16 = "http://www.w3.org/2004/02/skos/core#prefLabel16_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_16 = "\"Musical\"@hu";
	public final String TEST_PREV_LABEL_I18N_KEY_17 = "http://www.w3.org/2004/02/skos/core#prefLabel17_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_17 = "\"Musical\"@it";
	public final String TEST_PREV_LABEL_I18N_KEY_18 = "http://www.w3.org/2004/02/skos/core#prefLabel18_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_18 = "\"Musikal\"@no";
	public final String TEST_PREV_LABEL_I18N_KEY_19 = "http://www.w3.org/2004/02/skos/core#prefLabel19_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_19 = "\"Musikal\"@sv";
	public final String TEST_PREV_LABEL_I18N_KEY_20 = "http://www.w3.org/2004/02/skos/core#prefLabel20_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_20 = "\"Musical\"@pl";
	public final String TEST_PREV_LABEL_I18N_KEY_21 = "http://www.w3.org/2004/02/skos/core#prefLabel21_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_21 = "\"Musikaali\"@fi";
	public final String TEST_PREV_LABEL_I18N_KEY_22 = "http://www.w3.org/2004/02/skos/core#prefLabel22_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_22 = "\"Müzikal tiyatro\"@tr";
	public final String TEST_PREV_LABEL_I18N_KEY_23 = "http://www.w3.org/2004/02/skos/core#prefLabel23_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_23 = "\"Мјузикл\"@sr";
	public final String TEST_PREV_LABEL_I18N_KEY_24 = "http://www.w3.org/2004/02/skos/core#prefLabel24_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_24 = "\"Музикален театър\"@bg";
	public final String TEST_PREV_LABEL_I18N_KEY_25 = "http://www.w3.org/2004/02/skos/core#prefLabel25_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_25 = "\"Music theatre\"@en";
	public final String TEST_PREV_LABEL_I18N_KEY_26 = "http://www.w3.org/2004/02/skos/core#prefLabel26_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_26 = "\"ミュージカル\"@ja";
	public final String TEST_PREV_LABEL_I18N_KEY_27 = "http://www.w3.org/2004/02/skos/core#prefLabel27_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_27 = "\"Μιούζικαλ\"@el";
	public final String TEST_PREV_LABEL_I18N_KEY_28 = "http://www.w3.org/2004/02/skos/core#prefLabel28_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_28 = "\"Teater musikal\"@id";
	public final String TEST_PREV_LABEL_I18N_KEY_29 = "http://www.w3.org/2004/02/skos/core#prefLabel29_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_29 = "\"Muzikál\"@sk";
	public final String TEST_PREV_LABEL_I18N_KEY_30 = "http://www.w3.org/2004/02/skos/core#prefLabel30_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_30 = "\"Muzikál\"@cs";
	public final String TEST_PREV_LABEL_I18N_KEY_31 = "http://www.w3.org/2004/02/skos/core#prefLabel31_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_31 = "\"Mjuzikl\"@hr";
	public final String TEST_PREV_LABEL_I18N_KEY_32 = "http://www.w3.org/2004/02/skos/core#prefLabel32_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_32 = "\"Muusikal\"@et";
	public final String TEST_PREV_LABEL_I18N_KEY_33 = "http://www.w3.org/2004/02/skos/core#prefLabel33_prefLabel";
	public final String TEST_PREV_LABEL_I18N_VALUE_33 = "\"뮤지컬\"@ko";

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
    @Test
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
    @Test
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
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_0), TEST_PREV_LABEL_I18N_VALUE_0);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_1), TEST_PREV_LABEL_I18N_VALUE_1);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_2), TEST_PREV_LABEL_I18N_VALUE_2);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_3), TEST_PREV_LABEL_I18N_VALUE_3);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_4), TEST_PREV_LABEL_I18N_VALUE_4);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_5), TEST_PREV_LABEL_I18N_VALUE_5);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_6), TEST_PREV_LABEL_I18N_VALUE_6);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_7), TEST_PREV_LABEL_I18N_VALUE_7);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_8), TEST_PREV_LABEL_I18N_VALUE_8);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_9), TEST_PREV_LABEL_I18N_VALUE_9);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_10), TEST_PREV_LABEL_I18N_VALUE_10);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_11), TEST_PREV_LABEL_I18N_VALUE_11);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_12), TEST_PREV_LABEL_I18N_VALUE_12);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_13), TEST_PREV_LABEL_I18N_VALUE_13);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_14), TEST_PREV_LABEL_I18N_VALUE_14);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_15), TEST_PREV_LABEL_I18N_VALUE_15);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_16), TEST_PREV_LABEL_I18N_VALUE_16);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_17), TEST_PREV_LABEL_I18N_VALUE_17);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_18), TEST_PREV_LABEL_I18N_VALUE_18);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_19), TEST_PREV_LABEL_I18N_VALUE_19);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_20), TEST_PREV_LABEL_I18N_VALUE_20);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_21), TEST_PREV_LABEL_I18N_VALUE_21);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_22), TEST_PREV_LABEL_I18N_VALUE_22);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_23), TEST_PREV_LABEL_I18N_VALUE_23);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_24), TEST_PREV_LABEL_I18N_VALUE_24);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_25), TEST_PREV_LABEL_I18N_VALUE_25);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_26), TEST_PREV_LABEL_I18N_VALUE_26);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_27), TEST_PREV_LABEL_I18N_VALUE_27);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_28), TEST_PREV_LABEL_I18N_VALUE_28);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_29), TEST_PREV_LABEL_I18N_VALUE_29);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_30), TEST_PREV_LABEL_I18N_VALUE_30);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_31), TEST_PREV_LABEL_I18N_VALUE_31);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_32), TEST_PREV_LABEL_I18N_VALUE_32);
    	assertEquals(concept.getPrefLabel().get(TEST_PREV_LABEL_I18N_KEY_33), TEST_PREV_LABEL_I18N_VALUE_33);
    }

    
}
