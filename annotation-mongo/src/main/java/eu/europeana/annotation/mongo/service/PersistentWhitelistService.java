package eu.europeana.annotation.mongo.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentWhitelistService  extends AbstractNoSqlService<PersistentWhitelistEntry, String> {

	//find() methods 
	public PersistentWhitelistEntry find(PersistentWhitelistEntry whitelist) throws AnnotationMongoException;
	public List<PersistentWhitelistEntry> findAll(PersistentWhitelistEntry whitelist) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentWhitelistEntry queryWhitelist) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param whitelist
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentWhitelistEntry create(PersistentWhitelistEntry whitelist) throws AnnotationMongoException;
	
	public abstract WhitelistEntry store(WhitelistEntry object) throws WhitelistValidationException;
	public PersistentWhitelistEntry findByUrl(String url);
	public List<? extends PersistentWhitelistEntry> getAll();
	public WhitelistEntry update(WhitelistEntry object);
	public void removeAll();
	public void removeByUrl(String url);
	public Set<String> getWhitelistDomains();
	public String getDomainName(String url) throws URISyntaxException;
	
}
