package eu.europeana.annotation.mongo.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import com.mongodb.WriteResult;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.dao.PersistentWhitelistDaoImpl;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidWhitelistException;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

@Service
@EnableCaching
public class PersistentWhitelistServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentWhitelistImpl, String> implements
		PersistentWhitelistService {

  @Autowired
  public PersistentWhitelistServiceImpl(PersistentWhitelistDaoImpl writeLockDaoImpl) {
    this.setDao(writeLockDaoImpl);
  }
  
	@Override
	public PersistentWhitelistImpl find(PersistentWhitelistImpl whitelist) {
		Query<PersistentWhitelistImpl> query = createQuery(whitelist);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentWhitelistImpl> findAll(PersistentWhitelistImpl whitelist)
			throws AnnotationMongoException {
		
		Query<PersistentWhitelistImpl> query = createQuery(whitelist);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentWhitelistImpl findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentWhitelistImpl> createQuery(PersistentWhitelistImpl whitelist) {
		Query<PersistentWhitelistImpl> query = getDao().createQuery();
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentWhitelistImpl whitelist = findByID(id);
			getDao().delete(whitelist);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}

	public int removeByUrlWithoutCache(String url) {		
		Query<PersistentWhitelistImpl> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_HTTP_URL, url);
		WriteResult writeResult = getDao().deleteByQuery(query);
		return writeResult.getN();
	}
	
	@Override
	public int removeByUrl(String url) {
		/**
		 * store current cache state in temporary list
		 */
		List<? extends PersistentWhitelistImpl> tmpList = getAll();
		int initWhitelistSize = tmpList.size();
		
		/**
		 * actually remove entries
		 */
		removeAll();
		
		/**
		 * mark entry to remove from temporary list
		 */
		WhitelistEntry entry = null;
		Iterator<? extends PersistentWhitelistImpl> itr = tmpList.iterator();
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
		Iterator<? extends PersistentWhitelistImpl> itrStore = tmpList.iterator();
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
		Iterator<PersistentWhitelistImpl> itr = findAll().iterator();
		while (itr.hasNext()) {
			WhitelistEntry whitelistObj = itr.next();
			int numDeletedWhitelistEntries = removeByUrlWithoutCache(whitelistObj.getHttpUrl());
			totalNumDeletedWhitelistEntries = totalNumDeletedWhitelistEntries + numDeletedWhitelistEntries;
		}
		return totalNumDeletedWhitelistEntries;
	}
	
	@Override
	public void remove(PersistentWhitelistImpl queryWhitelist) throws AnnotationMongoException {
		Query<PersistentWhitelistImpl> createQuery = createQuery(queryWhitelist);
		WriteResult res = getDao().deleteByQuery(createQuery);
		validateDeleteResult(res);
	}

	protected void validateDeleteResult(WriteResult res)
			throws AnnotationMongoException {
		int affected = res.getN();
		if(affected != 1 )
			throw new AnnotationMongoException("Delete operation Failed!" + res);
	}
	
	
	@Override
	public PersistentWhitelistImpl create(PersistentWhitelistImpl whitelist)
			throws AnnotationMongoException {

		return store(whitelist);
	}

	void validateWhitelist(PersistentWhitelistImpl whitelist) throws InvalidWhitelistException {
	}

	@Override
	public WhitelistEntry store(WhitelistEntry object) {
		WhitelistEntry res = null;
		if(object instanceof PersistentWhitelistImpl)
			res = this.store((PersistentWhitelistImpl) object);
		else{
			PersistentWhitelistImpl persistentObject = copyIntoPersistentWhitelist(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentWhitelistImpl copyIntoPersistentWhitelist(WhitelistEntry whitelist) {

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

		PersistentWhitelistImpl persistentWhitelist = (PersistentWhitelistImpl) object;

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
	public PersistentWhitelistImpl findByUrl(String url) {
		Query<PersistentWhitelistImpl> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_HTTP_URL, url);
		QueryResults<? extends PersistentWhitelistImpl> results = getDao()
				.find(query);
		List<? extends PersistentWhitelistImpl> whitelistList = results.asList();
		if (whitelistList.size() == 0)
			return null;
		return whitelistList.get(whitelistList.size() - 1);
	}

	@Override
	public PersistentWhitelistImpl findByName(String name) {
		Query<PersistentWhitelistImpl> query = getDao().createQuery();
		query.filter(PersistentWhitelistEntry.FIELD_NAME, name);
		QueryResults<? extends PersistentWhitelistImpl> results = getDao()
				.find(query);
		List<? extends PersistentWhitelistImpl> whitelistList = results.asList();
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
		Iterator<? extends PersistentWhitelistImpl> itr = getAll().iterator();
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
	public List<? extends PersistentWhitelistImpl> getAll() {
		return (List<? extends PersistentWhitelistImpl>) findAll();
	}
}
