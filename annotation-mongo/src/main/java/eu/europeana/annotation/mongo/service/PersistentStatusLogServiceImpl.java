package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryImpl;
import org.mongodb.morphia.query.QueryResults;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.exception.ProviderAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.StatusLogValidationException;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidStatusLogException;
import eu.europeana.annotation.mongo.model.PersistentStatusLogImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

@Component
public class PersistentStatusLogServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentStatusLog, String> implements
		PersistentStatusLogService {

	@Override
	public PersistentStatusLog find(PersistentStatusLog statusLog) {
		Query<PersistentStatusLog> query = createQuery(statusLog);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentStatusLog> findAll(PersistentStatusLog statusLog)
			throws AnnotationMongoException {
		
		Query<PersistentStatusLog> query = createQuery(statusLog);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentStatusLog findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	@Override
	public List<? extends StatusLog> getFilteredStatusLogList(
			String status, String startOn, String limit
			) {
		Query<PersistentStatusLog> query = getDao().createQuery();
		if (StringUtils.isNotEmpty(status))
			query.filter(PersistentStatusLog.FIELD_STATUS, status);
		try {
			if (StringUtils.isNotEmpty(startOn))
				query.offset(Integer.parseInt(startOn));
			if (StringUtils.isNotEmpty(limit))
				query.limit(Integer.parseInt(limit));
		} catch (Exception e) {
			throw new ProviderAttributeInstantiationException(
					"Unexpected exception occured when searching annotation status logs. "
							+ ProviderAttributeInstantiationException.BASE_MESSAGE,
					"startOn: " + startOn + ", limit: " + limit + ". ", e);
		}

		QueryResults<? extends PersistentStatusLog> results = getDao()
				.find(query);
		return results.asList();
	}
	
	protected Query<PersistentStatusLog> createQuery(PersistentStatusLog statusLog) {
		Query<PersistentStatusLog> query = getDao().createQuery();
//		if(statusLog.getStatusLogType() != null)
//			query.filter(PersistentStatusLog.FIELD_CONCEPT_TYPE, statusLog.getStatusLogType());
		
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentStatusLog statusLog = findByID(id);
			getDao().delete(statusLog);
			//make one of the following to work
			//getDao().deleteById(id);
			//super.remove(id);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}
	
	@Override
	public void remove(PersistentStatusLog queryStatusLog) throws AnnotationMongoException {
		Query<PersistentStatusLog> createQuery = createQuery(queryStatusLog);
		WriteResult res = getDao().deleteByQuery(createQuery);
		validateDeleteResult(res);
	}

	@SuppressWarnings("deprecation")
	protected void validateDeleteResult(WriteResult res)
			throws AnnotationMongoException {
		int affected = res.getN();
		if(affected != 1 )
			throw new AnnotationMongoException("Delete operation Failed!" + res);
	}

	@Override
	public PersistentStatusLog update(PersistentStatusLog statusLog, String agent) throws InvalidStatusLogException {
		if (statusLog.getId() == null)
			throw new InvalidStatusLogException(InvalidStatusLogException.MESSAGE_NULL_ATTRIBUTE + "id");
		
//		statusLog.setLastUpdateTimestamp(System.currentTimeMillis());
		
//		validateStatusLog(statusLog);
		return store(statusLog);
	}

//	public boolean isSemanticStatusLog(PersistentStatusLog statusLog){
//		return StatusLogTypes.isSemanticStatusLog(statusLog.getStatusLogType());
//	}
//	
//	public boolean isSimpleStatusLog(PersistentStatusLog statusLog){
//		return StatusLogTypes.isSimpleStatusLog(statusLog.getStatusLogType());
//	}
	
	
	@Override
	public PersistentStatusLog create(PersistentStatusLog statusLog)
			throws AnnotationMongoException {
		
//		if (statusLog.getLastUpdatedBy() == null)
//			statusLog.setLastUpdatedBy(statusLog.getCreator());
//		
//		if (statusLog.getCreationTimestamp() == null)
//			statusLog.setCreationTimestamp(System.currentTimeMillis());
//		if (statusLog.getLastUpdateTimestamp() == null)
//			statusLog.setLastUpdateTimestamp(statusLog.getCreationTimestamp());
//		
//		if (statusLog.getStatusLogType() == null)
//			statusLog.setStatusLogTypeEnum(StatusLogTypes.SIMPLE_TAG);
//
//		validateStatusLog(statusLog);

		return store(statusLog);
	}

//	private void validateStatusLog(PersistentStatusLog statusLog) throws InvalidStatusLogException {
//	}

	@Override
	public StatusLog store(StatusLog object) {
		StatusLog res = null;
		if(object instanceof PersistentStatusLog)
			res = this.store((PersistentStatusLog) object);
		else{
			PersistentStatusLog persistentObject = copyIntoPersistentStatusLog(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentStatusLog copyIntoPersistentStatusLog(StatusLog statusLog) {

		PersistentStatusLogImpl persistentStatusLog = new PersistentStatusLogImpl();
		persistentStatusLog.setUser(statusLog.getUser());
		persistentStatusLog.setStatus(statusLog.getStatus());
		persistentStatusLog.setDate(statusLog.getDate());
		persistentStatusLog.setAnnotationId(statusLog.getAnnotationId());
		return persistentStatusLog;
	}				
	
	@Override
	public StatusLog update(StatusLog object) {

		StatusLog res = null;

		PersistentStatusLog persistentStatusLog = (PersistentStatusLog) object;

		if (persistentStatusLog != null 
				&& persistentStatusLog.getId() != null 
				) {
			remove(persistentStatusLog.getId().toString());
			persistentStatusLog.setId(null);
			res = store(persistentStatusLog);
		} else {
			throw new StatusLogValidationException(
					StatusLogValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	@Override
	public PersistentStatusLog findByStatus(String status) {
		Query<PersistentStatusLog> query = getDao().createQuery();
		query.filter(PersistentStatusLog.FIELD_STATUS, status);

//		return getDao().findOne(query);
		QueryResults<? extends PersistentStatusLog> results = getDao()
				.find(query);
		List<? extends PersistentStatusLog> statusLogList = results.asList();
		return statusLogList.get(statusLogList.size() - 1);
	}
	
}
