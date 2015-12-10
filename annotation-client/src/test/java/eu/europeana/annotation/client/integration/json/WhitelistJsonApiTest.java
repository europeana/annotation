package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

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
		
		/**
		 * Retrieve created whitelistEntry object by the given HTTP URL
		 */
		WhitelistEntry resultWhitelistEntry = whitelistJsonApi.getWhitelistEntryByUrl(TEST_HTTP_URI);
		assertNotNull(resultWhitelistEntry);
		
		/**
		 * Delete created whitelistEntry object by the given HTTP URL
		 */
		ResponseEntity<String> res = whitelistJsonApi.deleteWhitelistEntry(TEST_HTTP_URI);
		String jsonRes = res.getBody();
		int numDeletedWhitelistEntries = 0;
		try {
			JSONObject mainObject = new JSONObject(jsonRes);
			String numEntriesStr = mainObject.getString("error");
			int len = numEntriesStr.length();
			if (len > 0) {
				String numStr = numEntriesStr.substring(len - 1);
				numDeletedWhitelistEntries = Integer.valueOf(numStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		assertTrue(numDeletedWhitelistEntries == 1);		
	}

	/**
	 * Since this test method impacts all whitelist entries in the database for regular testing it should
	 * be commented out.
	 */
//	@Test
	public void crudWhitelist() {
		
		/**
		 * Load a whitelistEntry objects within the test and do not rely on the objects stored in the database.
		 */
		List<? extends WhitelistEntry> whitelistEntries = whitelistJsonApi.loadWhitelistFromResources();
		assertNotNull(whitelistEntries);
		int loadedSize = whitelistEntries.size();
		
		/**
		 * Retrieve created whitelistEntry objects
		 */
		List<? extends WhitelistEntry> retrievedWhitelistEntries = whitelistJsonApi.getWhitelist();
		assertNotNull(retrievedWhitelistEntries);
		int retrievedSize = retrievedWhitelistEntries.size();
		assertTrue(loadedSize == retrievedSize);

		
		/**
		 * Delete created whitelistEntry objects
		 */
		ResponseEntity<String> res = whitelistJsonApi.deleteWholeWhitelist();
		String jsonRes = res.getBody();
		int numDeletedWhitelistEntries = 0;
		try {
			JSONObject mainObject = new JSONObject(jsonRes);
			String numEntriesStr = mainObject.getString("error");
			int len = numEntriesStr.length();
			if (len > 0) {
				String numStr = numEntriesStr.substring(len - 1);
				numDeletedWhitelistEntries = Integer.valueOf(numStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		assertTrue(numDeletedWhitelistEntries == loadedSize);		
	}
	
	
}
