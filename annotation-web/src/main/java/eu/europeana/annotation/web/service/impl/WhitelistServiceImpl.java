package eu.europeana.annotation.web.service.impl;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.utils.parse.WhiteListParser;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.service.WhitelistService;

public class WhitelistServiceImpl extends BaseAnnotationServiceImpl implements WhitelistService {

	@Resource
	PersistentWhitelistService mongoWhitelistPersistence;

	public PersistentWhitelistService getMongoWhitelistPersistence() {
		return mongoWhitelistPersistence;
	}

	public void setMongoWhitelistPersistance(PersistentWhitelistService mongoWhitelistPersistence) {
		this.mongoWhitelistPersistence = mongoWhitelistPersistence;
	}


	/**
     * Whitelist methods
     * @throws ParamValidationI18NException 
     */
	@Override
	public WhitelistEntry storeWhitelistEntry(WhitelistEntry newWhitelistEntry) throws WhitelistValidationException {

		validateWhitelistEntry(newWhitelistEntry);
		// store in mongo database
		return getMongoWhitelistPersistence().store(newWhitelistEntry);
	}

	@Override
	public WhitelistEntry updateWhitelistEntry(WhitelistEntry whitelist) {
		return getMongoWhitelistPersistence().update(whitelist);
	}

	@Override
	public int deleteWhitelistEntry(String url) {
		int res = getMongoWhitelistPersistence().removeByUrl(url);
		if (res == 0) 
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NO_ENTRIES_FOUND_TO_DELETE);
		return res;
	}

	@Override
	public WhitelistEntry getWhitelistEntryByUrl(String url) {
		return getMongoWhitelistPersistence().findByUrl(url);
	}

	@Override
	public WhitelistEntry getWhitelistEntryByName(String name) {
		return getMongoWhitelistPersistence().findByName(name);
	}

	public int deleteWholeWhitelist() {
		int numDeletedWhitelistEntries = getMongoWhitelistPersistence().removeAll();
		if (numDeletedWhitelistEntries == 0) 
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NO_ENTRIES_FOUND_TO_DELETE);
        return numDeletedWhitelistEntries;
	}

	
	private void validateMandatoryFields(WhitelistEntry whitelistEntry) {

		if (whitelistEntry.getName() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_NAME);

		if (whitelistEntry.getHttpUrl() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_URI);

		if (whitelistEntry.getStatus() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_STATUS);

		if (!whitelistEntry.getHttpUrl().startsWith(WhitelistValidationException.HTTP))
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_HTTP_URI);
	}


	private void enrichWhitelistEntry(WhitelistEntry whitelistEntry) {

		DateFormat df = new SimpleDateFormat(WhitelistValidationException.DATE_FORMAT);
		Date dateobj = new Date();
		String currentDateStr = df.format(dateobj);
		try {
			Date currentDate = df.parse(currentDateStr);
			whitelistEntry.setCreationDate(currentDate);
			whitelistEntry.setLastUpdate(currentDate);
			whitelistEntry.setEnableFrom(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	public List<? extends WhitelistEntry> loadWhitelistFromResources() throws WhitelistValidationException{
		List<? extends WhitelistEntry> res = new ArrayList<WhitelistEntry>();
		
		/**
		 * load property with the path to the default whitelist JSON file
		 */
		String whitelistPath = getConfiguration().getDefaultWhitelistResourcePath();
		
		/**
		 * load whitelist from resources in JSON format
		 */
		List<WhitelistEntry> defaultWhitelist = new ArrayList<WhitelistEntry>();
		URL whiteListFile = getClass().getResource(whitelistPath);
		
		defaultWhitelist = WhiteListParser.toWhitelist(
				whiteListFile.getFile());
		
		/**
		 *  store whitelist objects in database
		 */
		Iterator<WhitelistEntry> itrDefault = defaultWhitelist.iterator();
		while (itrDefault.hasNext()) {
			WhitelistEntry whitelistEntry = itrDefault.next();
			storeWhitelistEntry(whitelistEntry);
		}
		
		/**
		 *  retrieve whitelist objects
		 */
		res = getWhitelist();

		return res;
	}

	private void validateWhitelistEntry(WhitelistEntry whitelistEntry) {
		validateMandatoryFields(whitelistEntry);
		enrichWhitelistEntry(whitelistEntry);
		if (getWhitelistEntryByUrl(whitelistEntry.getHttpUrl()) != null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_HTTP_URL_EXISTS 
					+ whitelistEntry.getHttpUrl());
		if (getWhitelistEntryByName(whitelistEntry.getName()) != null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NAME_EXISTS
					+ whitelistEntry.getHttpUrl());
	}
	
	public List<? extends WhitelistEntry> getWhitelist() {
		
		/**
		 *  retrieve all whitelist objects
		 */
		return getMongoWhitelistPersistence().getAll();
	}

	@Override
	protected boolean validateResource(String value) {
	    // TODO Auto-generated method stub
	    return false;
	}

	
}
