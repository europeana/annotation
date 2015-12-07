package eu.europeana.annotation.client;

import java.util.List;

import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;

public interface WhitelistJsonApi {

	/**
	 * This method returns all Whitelist entries.
	 * @return Whitelist entries
	 */
	public List<? extends WhitelistEntry> loadWhitelistFromResources();
	
	/**
	 * @param newWhitelist
	 * @return
	 */
	public WhitelistEntry storeWhitelistEntry(String newWhitelist);
		
	/**
	 * @param whitelist
	 * @return
	 */
	public WhitelistEntry updateWhitelistEntry(WhitelistEntry whitelist);
	
	/**
	 * @param url
	 */
	public ResponseEntity<String> deleteWhitelistEntry(String url);
	
	/**
	 * @param url
	 * @return
	 */
	public WhitelistEntry getWhitelistEntryByUrl(String url);
	
	/**
	 * @param name
	 * @return
	 */
	public WhitelistEntry getWhitelistEntryByName(String name);
	
	/**
	 * @return
	 */
	public List<? extends WhitelistEntry> getWhitelist();
	
	/**
	 * This method removes all whitelist entries and returns the number of deleted entries.
	 */
	public ResponseEntity<String> deleteWholeWhitelist();

}
