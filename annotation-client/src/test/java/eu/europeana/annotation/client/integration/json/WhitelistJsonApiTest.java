package eu.europeana.annotation.client.integration.json;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.WhitelistJsonApiImpl;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;


//TODO: enabled when whitelistEntry API is enabled
//@Ignore 
public class WhitelistJsonApiTest extends BaseWebAnnotationTest {

	private static String TEST_HTTP_URI = "http://test.data.europeana.eu";
	
    private static WhitelistJsonApiImpl whitelistJsonApi;
    protected Logger log = LogManager.getLogger(getClass());
    
    @BeforeAll
    public  static void initObjects() {
    	whitelistJsonApi = new WhitelistJsonApiImpl();
    }

//	@Test
	public void crudWhitelistEntry() throws JsonParseException, IOException {
		
		//ensure that old failing tests doesn't break the current run
		removeIfExists(TEST_HTTP_URI);
		
		String whitelistEntryJson = getJsonStringInput(WHITELIST_ENTRY);
		
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

	private void removeIfExists(String whiteListEntryUrl) {
		try{
			WhitelistEntry resultWhitelistEntry = whitelistJsonApi.getWhitelistEntryByUrl(whiteListEntryUrl);
			if(resultWhitelistEntry != null)
				whitelistJsonApi.deleteWhitelistEntry(whiteListEntryUrl);
		}catch(Exception ex){
			log.warn("posible problem to clean failing test results", ex);
		}
		
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
