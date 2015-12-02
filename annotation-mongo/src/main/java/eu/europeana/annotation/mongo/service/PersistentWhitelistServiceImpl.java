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
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidWhitelistException;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelist;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
public class PersistentWhitelistServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentWhitelist, String> implements
		PersistentWhitelistService {

	@Override
	public PersistentWhitelist find(PersistentWhitelist whitelist) {
		Query<PersistentWhitelist> query = createQuery(whitelist);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentWhitelist> findAll(PersistentWhitelist whitelist)
			throws AnnotationMongoException {
		
		Query<PersistentWhitelist> query = createQuery(whitelist);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentWhitelist findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentWhitelist> createQuery(PersistentWhitelist whitelist) {
		Query<PersistentWhitelist> query = getDao().createQuery();
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentWhitelist whitelist = findByID(id);
			getDao().delete(whitelist);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}

	public void removeByUrlWithoutCache(String url) {		
		Query<PersistentWhitelist> query = getDao().createQuery();
		query.filter(PersistentWhitelist.FIELD_HTTP_URL, url);
		getDao().deleteByQuery(query);
	}
	
	@Override
	public void removeByUrl(String url) {
		/**
		 * store current cache state in temporary list
		 */
		List<? extends PersistentWhitelist> tmpList = getAll();
		
		/**
		 * actually remove entries
		 */
		removeAll();
		
		/**
		 * mark entry to remove from temporary list
		 */
		Whitelist entry = null;
		Iterator<? extends PersistentWhitelist> itr = tmpList.iterator();
		while (itr.hasNext()) {
			Whitelist whitelistObj = itr.next();
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
		Iterator<? extends PersistentWhitelist> itrStore = tmpList.iterator();
		while (itrStore.hasNext()) {
			Whitelist whitelistObj = itrStore.next();
			store(whitelistObj);
		}
		
		/**
		 * refresh cache
		 */
		getAll(); 
	}
	
	@CacheEvict("whitelist")	
	@Override
	public void removeAll() {
		Iterator<PersistentWhitelist> itr = findAll().iterator();
		while (itr.hasNext()) {
			Whitelist whitelistObj = itr.next();
			removeByUrlWithoutCache(whitelistObj.getHttpUrl());
		}
	}
	
	@Override
	public void remove(PersistentWhitelist queryWhitelist) throws AnnotationMongoException {
		Query<PersistentWhitelist> createQuery = createQuery(queryWhitelist);
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
	public PersistentWhitelist create(PersistentWhitelist whitelist)
			throws AnnotationMongoException {

		return store(whitelist);
	}

	void validateWhitelist(PersistentWhitelist whitelist) throws InvalidWhitelistException {
	}

	@Override
	public Whitelist store(Whitelist object) {
		Whitelist res = null;
		if(object instanceof PersistentWhitelist)
			res = this.store((PersistentWhitelist) object);
		else{
			PersistentWhitelist persistentObject = copyIntoPersistentWhitelist(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentWhitelist copyIntoPersistentWhitelist(Whitelist whitelist) {

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
	public Whitelist update(Whitelist object) {

		Whitelist res = null;

		PersistentWhitelist persistentWhitelist = (PersistentWhitelist) object;

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
	public PersistentWhitelist findByUrl(String url) {
		Query<PersistentWhitelist> query = getDao().createQuery();
		query.filter(PersistentWhitelist.FIELD_HTTP_URL, url);
		QueryResults<? extends PersistentWhitelist> results = getDao()
				.find(query);
		List<? extends PersistentWhitelist> whitelistList = results.asList();
		return whitelistList.get(whitelistList.size() - 1);
	}

	public String getDomainName(String url) throws URISyntaxException {
	    URI uri = new URI(url);
	    String domain = uri.getHost();
	    return domain.startsWith("www.") ? domain.substring(4) : domain;
	}
	
	@Override
	public Set<String> getWhitelistDomains() {
		Set<String> domains = new HashSet<String>();
		/**
		 *  retrieve whitelist objects
		 */
		Iterator<? extends PersistentWhitelist> itr = getAll().iterator();
		while (itr.hasNext()) {
			Whitelist whitelistObj = itr.next();
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
	public List<? extends PersistentWhitelist> getAll() {
		return (List<? extends PersistentWhitelist>) findAll();
	}
}
