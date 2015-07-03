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

    private SkosUtils skosUtils = null;

    
	@Before
    public void setUp() throws Exception {
		setSkosUtils(new SkosUtils());
    }

    @After
    public void tearDown() throws Exception {
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

    	    
	public SkosUtils getSkosUtils() {
		return skosUtils;
	}

	public void setSkosUtils(SkosUtils skosUtils) {
		this.skosUtils = skosUtils;
	}
}
