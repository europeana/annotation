package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentModerationRecordService  extends AbstractNoSqlService<PersistentModerationRecord, String> {

	//find() methods 
	public PersistentModerationRecord find(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	public PersistentModerationRecord find(AnnotationId annoId) throws ModerationMongoException;
	public List<PersistentModerationRecord> findAll(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentModerationRecord queryModerationRecord) throws AnnotationMongoException;
	
	//store() methods
	public PersistentModerationRecord create(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	
	public abstract ModerationRecord store(ModerationRecord object) throws ModerationRecordValidationException;
	public List<? extends ModerationRecord> getFilteredModerationRecordList(
			String status, String startOn, String limit);
	
	public ModerationRecord update(ModerationRecord object);
	
}
