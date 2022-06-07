package eu.europeana.annotation.mongo.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentWhitelistService  extends AbstractNoSqlService<PersistentWhitelistImpl, String> {

	//find() methods 
	public PersistentWhitelistImpl find(PersistentWhitelistImpl whitelist) throws AnnotationMongoException;
	public List<PersistentWhitelistImpl> findAll(PersistentWhitelistImpl whitelist) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentWhitelistImpl queryWhitelist) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param whitelist
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentWhitelistImpl create(PersistentWhitelistImpl whitelist) throws AnnotationMongoException;
	
	public abstract WhitelistEntry store(WhitelistEntry object) throws WhitelistValidationException;
	public PersistentWhitelistImpl findByUrl(String url);
	public PersistentWhitelistImpl findByName(String name);
	public List<? extends PersistentWhitelistImpl> getAll();
	public WhitelistEntry update(WhitelistEntry object);
	public int removeAll();
	public int removeByUrl(String url);
	public Set<String> getWhitelistDomains();
	public String getDomainName(String url) throws URISyntaxException;
	
}
