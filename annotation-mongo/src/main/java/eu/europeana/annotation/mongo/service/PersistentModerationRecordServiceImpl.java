package eu.europeana.annotation.mongo.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.WriteResult;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.exception.ProviderAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.mongo.dao.PersistentModerationRecordDaoImpl;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.PersistentModerationRecordImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

@Service
public class PersistentModerationRecordServiceImpl extends AbstractNoSqlServiceImpl<PersistentModerationRecordImpl, String>
		implements PersistentModerationRecordService {

  @Autowired
  public PersistentModerationRecordServiceImpl(PersistentModerationRecordDaoImpl writeLockDaoImpl) {
    this.setDao(writeLockDaoImpl);
  }
  
	@Override
	public PersistentModerationRecordImpl find(PersistentModerationRecordImpl moderationRecord) {
		Query<PersistentModerationRecordImpl> query = createQuery(moderationRecord);
		query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, moderationRecord.getIdentifier());
		return getDao().findOne(query);
	}

	public PersistentModerationRecordImpl find(long annoIdentifier) {
		Query<PersistentModerationRecordImpl> query = getDao().createQuery();
		query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, annoIdentifier);
		return getDao().findOne(query);
	}

	@Override
	public List<PersistentModerationRecordImpl> findAll(PersistentModerationRecordImpl moderationRecord)
			throws AnnotationMongoException {

		Query<PersistentModerationRecordImpl> query = createQuery(moderationRecord);
		return getDao().find(query).asList();

	}

	@Override
	public PersistentModerationRecordImpl findByID(String id) {
		return getDao().findOne("_id", new ObjectId(id));
	}

	@Override
	public List<? extends ModerationRecord> getFilteredModerationRecordList(String status, String startOn,
			String limit) {
		Query<PersistentModerationRecordImpl> query = getDao().createQuery();
		try {
			if (StringUtils.isNotEmpty(startOn))
				query.offset(Integer.parseInt(startOn));
			if (StringUtils.isNotEmpty(limit))
				query.limit(Integer.parseInt(limit));
		} catch (Exception e) {
			throw new ProviderAttributeInstantiationException(
					"Unexpected exception occured when searching annotation moderation records. "
							+ ProviderAttributeInstantiationException.BASE_MESSAGE,
					"startOn: " + startOn + ", limit: " + limit + ". ", e);
		}

		QueryResults<? extends PersistentModerationRecord> results = getDao().find(query);
		return results.asList();
	}

	protected Query<PersistentModerationRecordImpl> createQuery(PersistentModerationRecordImpl moderationRecord) {
		Query<PersistentModerationRecordImpl> query = getDao().createQuery();
		return query;
	}

	@Override
	public void remove(String id) {
		try {
			PersistentModerationRecordImpl moderationRecord = findByID(id);
			getDao().delete(moderationRecord);
			// make one of the following to work
			// getDao().deleteById(id);
			// super.remove(id);
		} catch (Exception e) {
			throw new AnnotationMongoRuntimeException(e);
		}
	}

	@Override
	public void remove(PersistentModerationRecordImpl queryModerationRecord) throws ModerationMongoException {
//		Query<PersistentModerationRecord> createQuery = createQuery(queryModerationRecord);
//		WriteResult res = getDao().deleteByQuery(createQuery);
//		validateDeleteResult(res);
		remove(queryModerationRecord.getIdentifier());
	}

	protected void validateDeleteResult(WriteResult res) throws AnnotationMongoException {
		int affected = res.getN();
		if (affected != 1)
			throw new AnnotationMongoException("Delete operation Failed!" + res);
	}

	@Override
	public PersistentModerationRecordImpl create(PersistentModerationRecordImpl moderationRecord)
			throws AnnotationMongoException {

		return store(moderationRecord);
	}

	@Override
	public ModerationRecord store(ModerationRecord object) {
		ModerationRecord res = null;
		if (object instanceof PersistentModerationRecord)
			res = this.store((PersistentModerationRecord) object);
		else {
			PersistentModerationRecordImpl persistentObject = copyIntoPersistentModerationRecord(object);
			return this.store(persistentObject);
		}
		return res;
	}

	public PersistentModerationRecordImpl copyIntoPersistentModerationRecord(ModerationRecord moderationRecord) {

		PersistentModerationRecordImpl persistentModerationRecord = new PersistentModerationRecordImpl();
		persistentModerationRecord.setIdentifier(moderationRecord.getIdentifier());
		persistentModerationRecord.setEndorseList(moderationRecord.getEndorseList());
		persistentModerationRecord.setReportList(moderationRecord.getReportList());
		persistentModerationRecord.setSummary(moderationRecord.getSummary());
		persistentModerationRecord.setCreated(moderationRecord.getCreated());
		persistentModerationRecord.setLastUpdated(moderationRecord.getLastUpdated());
		return persistentModerationRecord;
	}

	@Override
	public ModerationRecord update(ModerationRecord object) {

		ModerationRecord res = null;

		PersistentModerationRecordImpl persistentModerationRecord = (PersistentModerationRecordImpl) object;

		if (persistentModerationRecord != null && persistentModerationRecord.getId() != null) {
			remove(persistentModerationRecord.getId().toString());
			persistentModerationRecord.setId(null);
			res = store(persistentModerationRecord);
		} else {
			throw new ModerationRecordValidationException(ModerationRecordValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	public Summary getModerationSummaryByAnnotationId(long annotationIdentifier) {
		ModerationRecord moderationRecord = find(annotationIdentifier);
		if (moderationRecord == null)
			return null;

		return moderationRecord.getSummary();
	}

	@Override
	public void remove(long annoIdentifier) throws ModerationMongoException {

		try {
			Query<PersistentModerationRecordImpl> query = createQuery(null);
			query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, annoIdentifier);
			Key<PersistentModerationRecordImpl> key = getDao().find(query).getKey();
			if(key != null && key.getId() != null )
				remove(key.getId().toString());
		} catch (Throwable th) {
			throw new ModerationMongoException("Cannot delete Moderation Record", th); 
		}
	}
}
