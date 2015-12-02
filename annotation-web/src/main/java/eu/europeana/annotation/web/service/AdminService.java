package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

public interface AdminService {

	public String getComponentName();
	
	/**
	 * This method returns all Whitelist entries.
	 * @return Whitelist entries
	 * @throws ParamValidationException 
	 */
	public List<? extends WhitelistEntry> loadWhitelistFromResources() throws ParamValidationException;
	
	/**
	 * @param newWhitelist
	 * @return
	 * @throws ParamValidationException 
	 */
	public WhitelistEntry storeWhitelistEntry(WhitelistEntry newWhitelist) throws ParamValidationException;
		
	/**
	 * @param whitelist
	 * @return
	 */
	public WhitelistEntry updateWhitelistEntry(WhitelistEntry whitelist);
	
	/**
	 * @param url
	 */
	public void deleteWhitelistEntry(String url);
	
	/**
	 * @param url
	 * @return
	 */
	public WhitelistEntry getWhitelistEntryByUrl(String url);
	
	/**
	 * @return
	 */
	public List<? extends WhitelistEntry> getWhitelist();
	
	/**
	 * This method removes all whitelist entries.
	 */
	public void deleteWholeWhitelist();

	
}
