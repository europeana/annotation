package eu.europeana.annotation.dereferencation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;
/*
 * TODO: for these tests to work, please copy the given deref2json.xsl file to the test/resources folder
 */
public class MetisDereferenciationClientTest {

    Logger log = LogManager.getLogger(getClass());
  
    private MetisDereferenciationClient dereferenciationClient;
    private static final String METIS_DEREFERENCE_SERVICE_URI = "http://metis-dereference-rest-acceptance.eanadev.org/dereference?uri=";
    private static final String URI_WKD_VERMEER = "http://www.wikidata.org/entity/Q41264";
    private static final String URI_VIAF_VERMEER = "http://viaf.org/viaf/51961439";
//    private static final String URI_WKD_US = "http://www.wikidata.org/entity/Q30";
//    private static final String URI_GEO_FRANCE = "http://sws.geonames.org/3017382/";
    private static final String URI_WKT_PARK = "http://www.wikidata.org/entity/Q22698";
    private static final String URI_GETTY_COLD = "http://vocab.getty.edu/aat/300068991";
    private static final String URI_WKD_DA_VINCI = "http://www.wikidata.org/entity/Q762";

    @Test
    public void testDereferenceOne() throws IOException {
	Map<String, String> dereferenced = getDereferenciationClient().dereferenceOne(METIS_DEREFERENCE_SERVICE_URI, URI_WKD_VERMEER,
		"en,de");
	assertNotNull(dereferenced);
	assertTrue(dereferenced.size() == 1);
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_VERMEER)));
	log.debug(dereferenced.get(URI_WKD_VERMEER));
    }

    @Test
    public void testDereferenceDaVinci() throws IOException {
	Map<String, String> dereferenced = getDereferenciationClient().dereferenceOne(METIS_DEREFERENCE_SERVICE_URI, URI_WKD_DA_VINCI,
		"en,de");
	assertNotNull(dereferenced);
	assertTrue(dereferenced.size() == 1);
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_DA_VINCI)));
	log.debug(dereferenced.get(URI_WKD_DA_VINCI));
    }
    @Test
    public void testDereferenceMany() throws IOException {
	List<String> uris = Arrays.asList(new String[] { URI_WKD_VERMEER, URI_WKT_PARK, URI_GETTY_COLD, URI_VIAF_VERMEER, URI_WKD_DA_VINCI });

	Map<String, String> dereferenced = getDereferenciationClient().dereferenceMany(METIS_DEREFERENCE_SERVICE_URI,
		uris, "en,de");

	assertNotNull(dereferenced);
	assertEquals(uris.size(), dereferenced.size());
	assertTrue(dereferenced.containsKey(URI_WKD_VERMEER));
	assertTrue(dereferenced.containsKey(URI_WKT_PARK));
	assertTrue(dereferenced.containsKey(URI_GETTY_COLD));
	assertTrue(dereferenced.containsKey(URI_VIAF_VERMEER));
	assertTrue(dereferenced.containsKey(URI_WKD_DA_VINCI));
	
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_VERMEER)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKT_PARK)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_GETTY_COLD)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_VIAF_VERMEER)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_DA_VINCI)));
	
	log.debug(dereferenced.get(URI_WKD_VERMEER));
	log.debug(dereferenced.get(URI_WKT_PARK));
	log.debug(dereferenced.get(URI_GETTY_COLD));
	log.debug(dereferenced.get(URI_VIAF_VERMEER));
	log.debug(dereferenced.get(URI_WKD_DA_VINCI));
	
    }

    public MetisDereferenciationClient getDereferenciationClient() {
	if (dereferenciationClient == null) {
	    dereferenciationClient = new MetisDereferenciationClient();
	}
	return dereferenciationClient;
    }

}
