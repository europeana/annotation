package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.springframework.stereotype.Component;

import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.exception.ProviderAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.PersistentModerationRecordImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

@Component
public class PersistentModerationRecordServiceImpl extends AbstractNoSqlServiceImpl<PersistentModerationRecord, String>
		implements PersistentModerationRecordService {

	@Override
	public PersistentModerationRecord find(PersistentModerationRecord moderationRecord) {
		Query<PersistentModerationRecord> query = createQuery(moderationRecord);

		query.filter(PersistentModerationRecord.FIELD_BASEURL, moderationRecord.getAnnotationId().getBaseUrl());
		query.filter(PersistentModerationRecord.FIELD_PROVIDER, moderationRecord.getAnnotationId().getProvider());
		query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, moderationRecord.getAnnotationId().getIdentifier());

		return getDao().findOne(query);
	}

	public PersistentModerationRecord find(AnnotationId annoId) {

		Query<PersistentModerationRecord> query = getDao().createQuery();
		query.filter(PersistentModerationRecord.FIELD_BASEURL, annoId.getBaseUrl());
		query.filter(PersistentModerationRecord.FIELD_PROVIDER, annoId.getProvider());
		query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, annoId.getIdentifier());

		return getDao().findOne(query);
	}

	@Override
	public List<PersistentModerationRecord> findAll(PersistentModerationRecord moderationRecord)
			throws AnnotationMongoException {

		Query<PersistentModerationRecord> query = createQuery(moderationRecord);
		return getDao().find(query).asList();

	}

	@Override
	public PersistentModerationRecord findByID(String id) {
		return getDao().findOne("_id", new ObjectId(id));
	}

	@Override
	public List<? extends ModerationRecord> getFilteredModerationRecordList(String status, String startOn,
			String limit) {
		Query<PersistentModerationRecord> query = getDao().createQuery();
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

	protected Query<PersistentModerationRecord> createQuery(PersistentModerationRecord moderationRecord) {
		Query<PersistentModerationRecord> query = getDao().createQuery();
		return query;
	}

	@Override
	public void remove(String id) {
		try {
			PersistentModerationRecord moderationRecord = findByID(id);
			getDao().delete(moderationRecord);
			// make one of the following to work
			// getDao().deleteById(id);
			// super.remove(id);
		} catch (Exception e) {
			throw new AnnotationMongoRuntimeException(e);
		}
	}

	@Override
	public void remove(PersistentModerationRecord queryModerationRecord) throws ModerationMongoException {
//		Query<PersistentModerationRecord> createQuery = createQuery(queryModerationRecord);
//		WriteResult res = getDao().deleteByQuery(createQuery);
//		validateDeleteResult(res);
		remove(queryModerationRecord.getAnnotationId());
	}

	@SuppressWarnings("deprecation")
	protected void validateDeleteResult(WriteResult res) throws AnnotationMongoException {
		int affected = res.getN();
		if (affected != 1)
			throw new AnnotationMongoException("Delete operation Failed!" + res);
	}

	@Override
	public PersistentModerationRecord create(PersistentModerationRecord moderationRecord)
			throws AnnotationMongoException {

		return store(moderationRecord);
	}

	@Override
	public ModerationRecord store(ModerationRecord object) {
		ModerationRecord res = null;
		if (object instanceof PersistentModerationRecord)
			res = this.store((PersistentModerationRecord) object);
		else {
			PersistentModerationRecord persistentObject = copyIntoPersistentModerationRecord(object);
			return this.store(persistentObject);
		}
		return res;
	}

	public PersistentModerationRecord copyIntoPersistentModerationRecord(ModerationRecord moderationRecord) {

		PersistentModerationRecordImpl persistentModerationRecord = new PersistentModerationRecordImpl();
		persistentModerationRecord.setAnnotationId(moderationRecord.getAnnotationId());
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

		PersistentModerationRecord persistentModerationRecord = (PersistentModerationRecord) object;

		if (persistentModerationRecord != null && persistentModerationRecord.getId() != null) {
			remove(persistentModerationRecord.getId().toString());
			persistentModerationRecord.setId(null);
			res = store(persistentModerationRecord);
		} else {
			throw new ModerationRecordValidationException(ModerationRecordValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	public Summary getModerationSummaryByAnnotationId(AnnotationId annotationId) {
		ModerationRecord moderationRecord = find(annotationId);
		if (moderationRecord == null)
			return null;

		return moderationRecord.getSummary();
	}

	@Override
	public void remove(AnnotationId annoId) throws ModerationMongoException {

		try {
			Query<PersistentModerationRecord> query = createQuery(null);

			query.filter(PersistentModerationRecord.FIELD_BASEURL, annoId.getBaseUrl());
			query.filter(PersistentModerationRecord.FIELD_PROVIDER, annoId.getProvider());
			query.filter(PersistentModerationRecord.FIELD_IDENTIFIER, annoId.getIdentifier());

			Key<PersistentModerationRecord> key = getDao().find(query).getKey();
			
			if(key != null && key.getId() != null )
				remove(key.getId().toString());
		} catch (Throwable th) {
			throw new ModerationMongoException("Cannot delete Moderation Record", th); 
		}
	}
}
