package eu.europeana.annotation.mongo.service;

import java.util.Date;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import eu.europeana.annotation.mongo.dao.PersistentApiWriteLockDaoImpl;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.PersistentApiWriteLockImpl;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
@Service
public class PersistentApiWriteLockServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentApiWriteLockImpl, String> implements	
		PersistentApiWriteLockService {

  @Autowired
  public PersistentApiWriteLockServiceImpl(PersistentApiWriteLockDaoImpl writeLockDaoImpl) {
    this.setDao(writeLockDaoImpl);
  }
  
	@Override
	public PersistentApiWriteLockImpl lock(String action) throws ApiWriteLockException {
		
		try {
			PersistentApiWriteLockImpl pij = new PersistentApiWriteLockImpl(action);
			return super.store(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to set lock.", e);
		}
	}
	
	@Override 
	public PersistentApiWriteLockImpl getLastActiveLock(String name) throws ApiWriteLockException {
		
		try {
			Query<PersistentApiWriteLockImpl> query = getDao().createQuery();
			
			query.criteria("name").contains(name);
			query.criteria("ended").doesNotExist();
			query.order("-started");
			PersistentApiWriteLockImpl pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get last lock.", e);
		}
	}
	
	@Override 
	public PersistentApiWriteLockImpl getLastActiveLock() throws ApiWriteLockException {
		
		try {
			Query<PersistentApiWriteLockImpl> query = getDao().createQuery();
			query.criteria("ended").doesNotExist();
			query.order("-started");
			PersistentApiWriteLockImpl pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get last lock.", e);
		}
	}

	@Override
	public PersistentApiWriteLockImpl getLockById(String id) throws ApiWriteLockException {
		try {
			Query<PersistentApiWriteLockImpl> query = getDao().createQuery();
			query.criteria("_id").equal(new ObjectId(id));
			PersistentApiWriteLockImpl pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to get lock by id.", e);
		}
	}

	@Override
	public void unlock(PersistentApiWriteLockImpl pij) throws ApiWriteLockException {
		try {
			pij.setEnded(new Date());
			super.store(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to unlock api.", e);
		}
	}
	
	protected void deleteLock(PersistentApiWriteLockImpl pij) throws ApiWriteLockException {
		try {
			getDao().delete(pij);
		} catch(Exception e) {
			throw new ApiWriteLockException("Unable to delete lock.", e);
		}
	}

}
