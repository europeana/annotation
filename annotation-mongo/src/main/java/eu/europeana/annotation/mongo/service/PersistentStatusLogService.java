package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.StatusLogValidationException;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentStatusLogService  extends AbstractNoSqlService<PersistentStatusLog, String> {

	//find() methods 
	public PersistentStatusLog find(PersistentStatusLog statusLog) throws AnnotationMongoException;
	public List<PersistentStatusLog> findAll(PersistentStatusLog statusLog) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentStatusLog queryStatusLog) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param statusLog
	 * @param agent - the person or software tool that performed the modifications of the statusLog
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentStatusLog update(PersistentStatusLog statusLog, String agent) throws AnnotationMongoException;
	public PersistentStatusLog create(PersistentStatusLog statusLog) throws AnnotationMongoException;
	
	public abstract StatusLog store(StatusLog object) throws StatusLogValidationException;
	public PersistentStatusLog findByStatus(String status);
	public List<? extends StatusLog> getFilteredStatusLogList(
			String status, String startOn, String limit);
	
	public StatusLog update(StatusLog object);
	
}
