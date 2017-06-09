package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentApiWriteLockService extends AbstractNoSqlService<PersistentApiWriteLock, String> {

	public PersistentApiWriteLock lock(String action) throws ApiWriteLockException;
	
	public void unlock(PersistentApiWriteLock pij) throws ApiWriteLockException;
	
	public PersistentApiWriteLock getLastActiveLock(String name) throws ApiWriteLockException;
	
	public PersistentApiWriteLock getLockById(String id) throws ApiWriteLockException;

	public PersistentApiWriteLock getLastActiveLock() throws ApiWriteLockException;
	
}
