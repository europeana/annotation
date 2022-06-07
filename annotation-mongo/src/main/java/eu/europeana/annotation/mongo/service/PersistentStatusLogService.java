package eu.europeana.annotation.mongo.service;

import java.util.List;
import eu.europeana.annotation.definitions.exception.StatusLogValidationException;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.PersistentStatusLogImpl;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentStatusLogService  extends AbstractNoSqlService<PersistentStatusLogImpl, String> {

	//find() methods 
	public PersistentStatusLogImpl find(PersistentStatusLogImpl statusLog) throws AnnotationMongoException;
	public List<PersistentStatusLogImpl> findAll(PersistentStatusLogImpl statusLog) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentStatusLogImpl queryStatusLog) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param statusLog
	 * @param agent - the person or software tool that performed the modifications of the statusLog
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentStatusLogImpl update(PersistentStatusLogImpl statusLog, String agent) throws AnnotationMongoException;
	public PersistentStatusLogImpl create(PersistentStatusLogImpl statusLog) throws AnnotationMongoException;
	
	public abstract StatusLog store(StatusLog object) throws StatusLogValidationException;
	public PersistentStatusLogImpl findByStatus(String status);
	public List<? extends StatusLog> getFilteredStatusLogList(
			String status, String startOn, String limit);
	
	public StatusLog update(StatusLog object);
	
}
