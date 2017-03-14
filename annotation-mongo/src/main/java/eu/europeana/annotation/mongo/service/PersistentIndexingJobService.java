package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.IndexingJobServiceException;
import eu.europeana.annotation.mongo.model.internal.PersistentIndexingJob;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentIndexingJobService extends AbstractNoSqlService<PersistentIndexingJob, String> {

	public PersistentIndexingJob lock(String action) throws IndexingJobServiceException;
	
	public void unlock(PersistentIndexingJob pij) throws IndexingJobServiceException;
	
	public PersistentIndexingJob getLastRunningJob() throws IndexingJobServiceException;
	
	public PersistentIndexingJob getJobById(String id) throws IndexingJobServiceException;
	
}
