package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

public interface AdminService {

	public String getComponentName();
	
	/**
	 * This method returns all Whitelist entries.
	 * @return Whitelist entries
	 * @throws ParamValidationException 
	 */
	public List<? extends Whitelist> loadWhitelistFromResources() throws ParamValidationException;
	
	/**
	 * @param newWhitelist
	 * @return
	 * @throws ParamValidationException 
	 */
	public Whitelist storeWhitelist(Whitelist newWhitelist) throws ParamValidationException;
		
	/**
	 * @param whitelist
	 * @return
	 */
	public Whitelist updateWhitelist(Whitelist whitelist);
	
	/**
	 * @param url
	 */
	public void deleteWhitelistEntry(String url);
	
	/**
	 * @param url
	 * @return
	 */
	public Whitelist getWhitelistByUrl(String url);
	
	/**
	 * @return
	 */
	public List<? extends Whitelist> getAllWhitelistEntries();
	
	/**
	 * This method removes all whitelist entries.
	 */
	public void deleteAllWhitelistEntries();

	
}
