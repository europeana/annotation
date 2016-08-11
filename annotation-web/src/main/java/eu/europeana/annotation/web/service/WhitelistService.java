package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

public interface WhitelistService {

	/**
	 * This method returns all Whitelist entries.
	 * 
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
	public int deleteWhitelistEntry(String url);

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
	 * This method removes all whitelist entries and returns the number of
	 * deleted entries.
	 */
	public int deleteWholeWhitelist();

}
