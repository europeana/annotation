package eu.europeana.annotation.dereferencation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;

public class MetisDereferenciationClientTest {

    private MetisDereferenciationClient dereferenciationClient;
    private static final String METIS_DEREFERENCE_SERVICE_URI = "http://metis-dereference-rest-production.eanadev.org/dereference?uri=";
    private static final String URI_WKD_VERMEER = "http://www.wikidata.org/entity/Q41264";
//    private static final String URI_WKD_US = "http://www.wikidata.org/entity/Q30";
//    private static final String URI_GEO_FRANCE = "http://sws.geonames.org/3017382/";
    private static final String URI_WKT_PARK = "http://www.wikidata.org/entity/Q22698";
    private static final String URI_GETTY_COLD = "http://vocab.getty.edu/aat/300068991";

    @Test
    public void testDereferenceOne() throws IOException {
	Map<String, String> dereferenced = getDereferenciationClient().dereferenceOne(METIS_DEREFERENCE_SERVICE_URI, URI_WKD_VERMEER,
		"en,de");
	assertNotNull(dereferenced);
	assertTrue(dereferenced.size() == 1);
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_VERMEER)));
	System.out.println(dereferenced.get(URI_WKD_VERMEER));
    }

    @Test
    public void testDereferenceMany() throws IOException {
	List<String> uris = Arrays.asList(new String[] { URI_WKD_VERMEER, URI_WKT_PARK, URI_GETTY_COLD });

	Map<String, String> dereferenced = getDereferenciationClient().dereferenceMany(METIS_DEREFERENCE_SERVICE_URI,
		uris, "en,de");

	assertNotNull(dereferenced);
	assertTrue(dereferenced.size() == 3);
	assertTrue(dereferenced.containsKey(URI_WKD_VERMEER));
	assertTrue(dereferenced.containsKey(URI_WKT_PARK));
	assertTrue(dereferenced.containsKey(URI_GETTY_COLD));

	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_VERMEER)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKT_PARK)));
	assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_GETTY_COLD)));
	System.out.println(dereferenced.get(URI_WKD_VERMEER));
	System.out.println(dereferenced.get(URI_WKT_PARK));
	System.out.println(dereferenced.get(URI_GETTY_COLD));
    }

    public MetisDereferenciationClient getDereferenciationClient() {
	if (dereferenciationClient == null) {
	    dereferenciationClient = new MetisDereferenciationClient();
	}
	return dereferenciationClient;
    }

}
