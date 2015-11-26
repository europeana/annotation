package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelist;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentWhitelistService  extends AbstractNoSqlService<PersistentWhitelist, String> {

	//find() methods 
	public PersistentWhitelist find(PersistentWhitelist whitelist) throws AnnotationMongoException;
	public List<PersistentWhitelist> findAll(PersistentWhitelist whitelist) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentWhitelist queryWhitelist) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param whitelist
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentWhitelist create(PersistentWhitelist whitelist) throws AnnotationMongoException;
	
	public abstract Whitelist store(Whitelist object) throws WhitelistValidationException;
	public PersistentWhitelist findByUrl(String url);
//	public List<? extends PersistentWhitelist> findAll();
	public Whitelist update(Whitelist object);
	public void removeAll();
	
}
