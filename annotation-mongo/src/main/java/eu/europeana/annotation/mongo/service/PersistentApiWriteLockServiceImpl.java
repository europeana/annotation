package eu.europeana.annotation.mongo.service;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;

import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.PersistentApiWriteLockImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
@Component
public class PersistentApiWriteLockServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentApiWriteLock, String> implements	
		PersistentApiWriteLockService {

	@Override
	public PersistentApiWriteLock lock(String action) throws ApiWriteLockException {
		
		try {
			PersistentApiWriteLock pij = new PersistentApiWriteLockImpl(action);
			return super.store(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to set lock.");
		}
	}
	
	@Override 
	public PersistentApiWriteLock getLastActiveLock(String name) throws ApiWriteLockException {
		
		try {
			Query<PersistentApiWriteLock> query = getDao().createQuery().limit(1);
			query.criteria("name").contains(name);
			query.criteria("ended").doesNotExist();
			query.order("-started");
			PersistentApiWriteLock pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get last lock.");
		}
	}
	
	@Override 
	public PersistentApiWriteLock getLastActiveLock() throws ApiWriteLockException {
		
		try {
			Query<PersistentApiWriteLock> query = getDao().createQuery().limit(1);
			query.criteria("ended").doesNotExist();
			query.order("-started");
			PersistentApiWriteLock pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get last lock.");
		}
	}

	@Override
	public PersistentApiWriteLock getLockById(String id) throws ApiWriteLockException {
		try {
			Query<PersistentApiWriteLock> query = getDao().createQuery().limit(1);
			query.criteria("_id").equal(new ObjectId(id));
			PersistentApiWriteLock pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get lock by id.");
		}
	}

	@Override
	public void unlock(PersistentApiWriteLock pij) throws ApiWriteLockException {
		try {
			pij.setEnded(new Date());
			super.store(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to unlock api.");
		}
	}
	
	protected void deleteLock(PersistentApiWriteLock pij) throws ApiWriteLockException {
		try {
			getDao().delete(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to delete lock.");
		}
	}

}
