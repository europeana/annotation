package eu.europeana.annotation.mongo.service;

import java.util.List;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentModerationRecordService  extends AbstractNoSqlService<PersistentModerationRecord, String> {

	//find() methods 
	public PersistentModerationRecord find(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	public PersistentModerationRecord find(long annoIdentifier) throws ModerationMongoException;
	public List<PersistentModerationRecord> findAll(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentModerationRecord queryModerationRecord) throws ModerationMongoException;
	
	//store() methods
	public PersistentModerationRecord create(PersistentModerationRecord moderationRecord) throws AnnotationMongoException;
	
	public abstract ModerationRecord store(ModerationRecord object) throws ModerationRecordValidationException;
	public List<? extends ModerationRecord> getFilteredModerationRecordList(
			String status, String startOn, String limit);
	
	public ModerationRecord update(ModerationRecord object);
	public Summary getModerationSummaryByAnnotationId(long annotationIdentifier);
	
	public void remove(long annoIdentifier) throws ModerationMongoException;
	
}
