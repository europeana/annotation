package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.annotation.client.WhitelistJsonApiImpl;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;


//TODO: enabled when whitelistEntry API is enabled
//@Ignore 
public class WhitelistJsonApiTest {

	private static String TEST_HTTP_URI = "http://test.data.europeana.eu";
	private static String TEST_NAME = "test.data.europeana";
	
    private WhitelistJsonApiImpl whitelistJsonApi;
    
    @Before
    public void initObjects() {
    	whitelistJsonApi = new WhitelistJsonApiImpl();
    }

	public static String whitelistEntryJson = 
		"{" +
		"\"name\": \"" + TEST_NAME + "\"," +
		"\"httpUrl\": \"" + TEST_HTTP_URI + "\"," +
		"\"status\": \"enabled\"" +
		"}";

	@Test
	public void crudWhitelistEntry() {
		
		/**
		 * Create a whitelistEntry object within the test and do not rely on the objects stored in the database.
		 */
		WhitelistEntry whitelistEntry = whitelistJsonApi.storeWhitelistEntry(whitelistEntryJson);
		assertNotNull(whitelistEntry);
		
		WhitelistEntry resultWhitelistEntry = whitelistJsonApi.getWhitelistEntryByUrl(TEST_HTTP_URI);
		assertNotNull(resultWhitelistEntry);
		
		int numDeletedWhitelistEntries = whitelistJsonApi.deleteWhitelistEntry(TEST_HTTP_URI);
		assertTrue(numDeletedWhitelistEntries == 1);
		
	}
	
}
