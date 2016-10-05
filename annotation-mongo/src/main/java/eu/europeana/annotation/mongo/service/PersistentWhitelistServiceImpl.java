package eu.europeana.annotation.mongo.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidWhitelistException;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
public class PersistentWhitelistServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentWhitelistEntry, String> implements
		PersistentWhitelistService {

	@Override
	public PersistentWhitelistEntry find(PersistentWhitelistEntry whitelist) {
		Query<PersistentWhitelistEntry> query = createQuery(whitelist);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentWhitelistEntry> findAll(PersistentWhitelistEntry whitelist)
			throws AnnotationMongoException {
		
		Query<PersistentWhitelistEntry> query = createQuery(whitelist);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentWhitelistEntry findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentWhitelistEntry> createQuery(PersistentWhitelistEntry whitelist) {
		Query<PersistentWhitelistEntry> query = getDao().createQuery();
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentWhitelistEntry whitelist = findByID(id);
			getDao().delete(whitelist);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}

	public int removeByUrlWithoutCache(String url) {		
		Query<PersistentWhitelistEntry> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_HTTP_URL, url);
		WriteResult writeResult = getDao().deleteByQuery(query);
		return writeResult.getN();
	}
	
	@Override
	public int removeByUrl(String url) {
		/**
		 * store current cache state in temporary list
		 */
		List<? extends PersistentWhitelistEntry> tmpList = getAll();
		int initWhitelistSize = tmpList.size();
		
		/**
		 * actually remove entries
		 */
		removeAll();
		
		/**
		 * mark entry to remove from temporary list
		 */
		WhitelistEntry entry = null;
		Iterator<? extends PersistentWhitelistEntry> itr = tmpList.iterator();
		while (itr.hasNext()) {
			WhitelistEntry whitelistObj = itr.next();
			if (whitelistObj.getHttpUrl().equals(url)) {
				entry = whitelistObj;
			    break;
			}
		}
		
		/**
		 * remove whitelist entry from temporary list
		 */
		tmpList.remove(entry);
		
		/**
		 * store temporary list
		 */
		Iterator<? extends PersistentWhitelistEntry> itrStore = tmpList.iterator();
		while (itrStore.hasNext()) {
			WhitelistEntry whitelistObj = itrStore.next();
			store(whitelistObj);
		}
		
		/**
		 * refresh cache
		 */
		return initWhitelistSize - getAll().size(); 
	}
	
	@CacheEvict("whitelist")	
	@Override
	public int removeAll() {
		int totalNumDeletedWhitelistEntries = 0;
		Iterator<PersistentWhitelistEntry> itr = findAll().iterator();
		while (itr.hasNext()) {
			WhitelistEntry whitelistObj = itr.next();
			int numDeletedWhitelistEntries = removeByUrlWithoutCache(whitelistObj.getHttpUrl());
			totalNumDeletedWhitelistEntries = totalNumDeletedWhitelistEntries + numDeletedWhitelistEntries;
		}
		return totalNumDeletedWhitelistEntries;
	}
	
	@Override
	public void remove(PersistentWhitelistEntry queryWhitelist) throws AnnotationMongoException {
		Query<PersistentWhitelistEntry> createQuery = createQuery(queryWhitelist);
		WriteResult res = getDao().deleteByQuery(createQuery);
		validateDeleteResult(res);
	}

	@SuppressWarnings("deprecation")
	protected void validateDeleteResult(WriteResult res)
			throws AnnotationMongoException {
		int affected = res.getN();
		if(affected != 1 )
			throw new AnnotationMongoException("Delete operation Failed!" + res.getError(), res.getLastError().getException());
	}
	
	
	@Override
	public PersistentWhitelistEntry create(PersistentWhitelistEntry whitelist)
			throws AnnotationMongoException {

		return store(whitelist);
	}

	void validateWhitelist(PersistentWhitelistEntry whitelist) throws InvalidWhitelistException {
	}

	@Override
	public WhitelistEntry store(WhitelistEntry object) {
		WhitelistEntry res = null;
		if(object instanceof PersistentWhitelistEntry)
			res = this.store((PersistentWhitelistEntry) object);
		else{
			PersistentWhitelistEntry persistentObject = copyIntoPersistentWhitelist(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentWhitelistEntry copyIntoPersistentWhitelist(WhitelistEntry whitelist) {

		PersistentWhitelistImpl persistentWhitelist = new PersistentWhitelistImpl();
		persistentWhitelist.setHttpUrl(whitelist.getHttpUrl());
		persistentWhitelist.setName(whitelist.getName());
		persistentWhitelist.setStatus(whitelist.getStatus());
		persistentWhitelist.setCreationDate(whitelist.getCreationDate());
		persistentWhitelist.setLastUpdate(whitelist.getLastUpdate());
		persistentWhitelist.setEnableFrom(whitelist.getEnableFrom());
		persistentWhitelist.setDisableTo(whitelist.getDisableTo());
		return persistentWhitelist;
	}				
	
	@Override
	public WhitelistEntry update(WhitelistEntry object) {

		WhitelistEntry res = null;

		PersistentWhitelistEntry persistentWhitelist = (PersistentWhitelistEntry) object;

		if (persistentWhitelist != null 
				&& persistentWhitelist.getId() != null 
				) {
			remove(persistentWhitelist.getId().toString());
			persistentWhitelist.setId(null);
			res = store(persistentWhitelist);
		} else {
			throw new WhitelistValidationException(
					WhitelistValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	@Override
	public PersistentWhitelistEntry findByUrl(String url) {
		Query<PersistentWhitelistEntry> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_HTTP_URL, url);
		QueryResults<? extends PersistentWhitelistEntry> results = getDao()
				.find(query);
		List<? extends PersistentWhitelistEntry> whitelistList = results.asList();
		if (whitelistList.size() == 0)
			return null;
		return whitelistList.get(whitelistList.size() - 1);
	}

	@Override
	public PersistentWhitelistEntry findByName(String name) {
		Query<PersistentWhitelistEntry> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_NAME, name);
		QueryResults<? extends PersistentWhitelistEntry> results = getDao()
				.find(query);
		List<? extends PersistentWhitelistEntry> whitelistList = results.asList();
		if (whitelistList.size() == 0)
			return null;
		return whitelistList.get(whitelistList.size() - 1);
	}

	public String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    return uri.getHost();
	}
	
	@Override
	public Set<String> getWhitelistDomains() {
		Set<String> domains = new HashSet<String>();
		/**
		 *  retrieve whitelist objects
		 */
		Iterator<? extends PersistentWhitelistEntry> itr = getAll().iterator();
		while (itr.hasNext()) {
			WhitelistEntry whitelistObj = itr.next();
			String domainName;
			try {
				domainName = getDomainName(whitelistObj.getHttpUrl());
				domains.add(domainName);
			} catch (URISyntaxException e) {
				throw new AnnotationValidationException("URL is not valid. " +
					WebAnnotationFields.WHITELIST +"/"+ WebAnnotationFields.ADD + ". URL: " + whitelistObj.getHttpUrl());
			}
		}
		return domains;
	}

	@CachePut("whitelist")
	@Override
	public List<? extends PersistentWhitelistEntry> getAll() {
		return (List<? extends PersistentWhitelistEntry>) findAll();
	}
}
