package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

public interface AdminService {

	public String getComponentName();
	
	/**
	 * This method finds annotation object in database by annotation ID 
	 * and reindexes it in Solr.
	 * @param annoId
	 * @return success of reindexing operation
	 */
	public boolean reindexAnnotationById(AnnotationId annoId);

	/**
	 * This method performs Solr reindexing for all annotation objects stored in database between 
	 * start and end date or timestamp. 
	 * @param startDate
	 * @param endDate
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return status of reindexing
	 */
	public String reindexAnnotationSet(String startDate, String endDate, String startTimestamp, String endTimestamp);
	
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
	 * This method removes all whitelist entries and returns the number of deleted entries.
	 */
	public int deleteWholeWhitelist();

	
}
