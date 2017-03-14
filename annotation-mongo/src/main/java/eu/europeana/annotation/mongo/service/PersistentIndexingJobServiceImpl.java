package eu.europeana.annotation.mongo.service;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;

import eu.europeana.annotation.mongo.exception.IndexingJobServiceException;
import eu.europeana.annotation.mongo.model.PersistentIndexingJobImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentIndexingJob;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
@Component
public class PersistentIndexingJobServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentIndexingJob, String> implements	
		PersistentIndexingJobService {

	@Override
	public PersistentIndexingJob lock(String action) throws IndexingJobServiceException {
		
		try {
			PersistentIndexingJob pij = new PersistentIndexingJobImpl(action);
			return super.store(pij);
		} catch(Exception e) {
			throw new IndexingJobServiceException("Unable to lock indexing job.");
		}
	}
	
	@Override 
	public PersistentIndexingJob getLastRunningJob() throws IndexingJobServiceException {
		
		try {
			Query<PersistentIndexingJob> query = getDao().createQuery().limit(1);
			query.criteria("ended").doesNotExist();
			query.order("-started");
			PersistentIndexingJob pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new IndexingJobServiceException("Unable to get last indexing job.");
		}
		
		
	}

	@Override
	public PersistentIndexingJob getJobById(String id) throws IndexingJobServiceException {
		try {
			Query<PersistentIndexingJob> query = getDao().createQuery().limit(1);
			query.criteria("_id").equal(new ObjectId(id));
			PersistentIndexingJob pij = getDao().findOne(query);
			return pij;
		} catch(Exception e) {
			throw new IndexingJobServiceException("Unable to get job by id.");
		}
	}

	@Override
	public void unlock(PersistentIndexingJob pij) throws IndexingJobServiceException {
		try {
			pij.setEnded(new Date());
			super.store(pij);
		} catch(Exception e) {
			throw new IndexingJobServiceException("Unable to unlock indexing job.");
		}
	}
	
	protected void deleteJob(PersistentIndexingJob pij) throws IndexingJobServiceException {
		try {
			getDao().delete(pij);
		} catch(Exception e) {
			throw new IndexingJobServiceException("Unable to delete job.");
		}
	}

}
