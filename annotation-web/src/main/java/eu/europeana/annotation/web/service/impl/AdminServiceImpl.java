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

import org.apache.log4j.Logger;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class AdminServiceImpl implements AdminService {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	PersistentWhitelistService mongoWhitelistPersistence;

	@Resource
	AuthenticationService authenticationService;

	Logger logger = Logger.getLogger(getClass());


	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public String getComponentName() {
		return configuration.getComponentName();
	}

	protected AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public PersistentWhitelistService getMongoWhitelistPersistence() {
		return mongoWhitelistPersistence;
	}

	public void setMongoWhitelistPersistance(PersistentWhitelistService mongoWhitelistPersistence) {
		this.mongoWhitelistPersistence = mongoWhitelistPersistence;
	}


	/**
     * Whitelist methods
     * @throws ParamValidationException 
     */
	@Override
	public Whitelist storeWhitelist(Whitelist newWhitelist) throws ParamValidationException {

		// store in mongo database
		return getMongoWhitelistPersistence().store(newWhitelist);
	}

	@Override
	public Whitelist updateWhitelist(Whitelist whitelist) {
		return getMongoWhitelistPersistence().update(whitelist);
	}

	@Override
	public void deleteWhitelistEntry(String url) {
		getMongoWhitelistPersistence().removeByUrl(url);
	}

	@Override
	public Whitelist getWhitelistByUrl(String url) {
		return getMongoWhitelistPersistence().findByUrl(url);
	}

	public void deleteAllWhitelistEntries() {
		getMongoWhitelistPersistence().removeAll();
	}

	
	private void validateMandatoryFields(Whitelist whitelistEntry) {

		if (whitelistEntry.getName() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_NAME);

		if (whitelistEntry.getHttpUrl() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_URI);

		if (whitelistEntry.getStatus() == null)
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_NULL_STATUS);

		if (!whitelistEntry.getHttpUrl().startsWith(WhitelistValidationException.HTTP))
			throw new WhitelistValidationException(WhitelistValidationException.ERROR_NOT_HTTP_URI);
	}


	private void enrichWhitelistEntry(Whitelist whitelistEntry) {

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


	public List<? extends Whitelist> loadWhitelistFromResources() throws ParamValidationException{
		List<? extends Whitelist> res = new ArrayList<Whitelist>();
		
		/**
		 * load property with the path to the default whitelist JSON file
		 */
		String whitelistPath = getConfiguration().getDefaultWhitelistResourcePath();
		
		/**
		 * load whitelist from resources in JSON format
		 */
		List<Whitelist> defaultWhitelist = new ArrayList<Whitelist>();
		URL whiteListFile = getClass().getResource(whitelistPath);
		
		defaultWhitelist = JsonUtils.toWhitelistObjectList(
				whiteListFile.getPath().substring(1));
		
		/**
		 *  store whitelist objects in database
		 */
		Iterator<Whitelist> itrDefault = defaultWhitelist.iterator();
		while (itrDefault.hasNext()) {
			Whitelist whitelistEntry = itrDefault.next();
			validateMandatoryFields(whitelistEntry);
			enrichWhitelistEntry(whitelistEntry);
			if (getWhitelistByUrl(whitelistEntry.getHttpUrl()) != null)
				throw new WhitelistValidationException(WhitelistValidationException.ERROR_HTTP_URL_EXISTS 
						+ whitelistEntry.getHttpUrl());
			storeWhitelist(whitelistEntry);
		}
		
		/**
		 *  retrieve whitelist objects
		 */
		res = getAllWhitelistEntries();

		return res;
	}
	
	public List<? extends Whitelist> getAllWhitelistEntries() {
		
		/**
		 *  retrieve all whitelist objects
		 */
		return getMongoWhitelistPersistence().getAll();
	}
	
}
