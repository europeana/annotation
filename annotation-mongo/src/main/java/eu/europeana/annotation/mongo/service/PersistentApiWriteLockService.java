package eu.europeana.annotation.mongo.service;

import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.PersistentApiWriteLockImpl;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentApiWriteLockService extends AbstractNoSqlService<PersistentApiWriteLockImpl, String> {

	public PersistentApiWriteLockImpl lock(String action) throws ApiWriteLockException;
	
	public void unlock(PersistentApiWriteLockImpl pij) throws ApiWriteLockException;
	
	public PersistentApiWriteLockImpl getLastActiveLock(String name) throws ApiWriteLockException;
	
	public PersistentApiWriteLockImpl getLockById(String id) throws ApiWriteLockException;

	public PersistentApiWriteLockImpl getLastActiveLock() throws ApiWriteLockException;
	
}
