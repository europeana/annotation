package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.batch.BulkOperationMode;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;

public interface PersistentAnnotationService extends AbstractNoSqlService<PersistentAnnotation, String>{

	public abstract Annotation store(Annotation object) throws AnnotationValidationException;

	public List<? extends Annotation> getAnnotationListByTarget(String target);

	public List<? extends Annotation> getAnnotationListByResourceId(String resourceId);

	public List<? extends Annotation> getAnnotationList (List<Long> annotationIdentifiers);
	
	public PersistentAnnotation find(long annoIdentifier);
	
	/**
	 * 
	 * @param annoId
	 * @throws AnnotationMongoRuntimeException - less or more than 1 object is found for the given arguments
	 */
	public void remove(long annoIdentifier) throws AnnotationMongoException;
	
	/**
	 * This method performs update for the passed annotation object
	 * @param annotation
	 */
	public PersistentAnnotation update(PersistentAnnotation annotation) throws AnnotationValidationException;

	
	/**
	 * This method notices the time of the last SOLR indexing for particular annotation
	 * @param anno the annotation object that was reindexed in solr
	 * @throws AnnotationMongoException 
	 */
	public Annotation updateIndexingTime(Annotation anno, Date lastIndexingDate) throws AnnotationMongoException;

	/**
	 * This method changes annotation status.
	 * @param newAnnotation
	 * @return
	 */
	public Annotation updateStatus(Annotation newAnnotation);
	
	public abstract long generateAnnotationIdentifier();
//	public abstract AnnotationId generateAnnotationId(String provider);
	
	public abstract List<Long> generateAnnotationIdentifierSequence(Integer seqLength);
//	public abstract List<AnnotationId> generateAnnotationIdSequence(String provider, Integer seqLength);

	public abstract Annotation findByTagId(String tagId);
	
	/**
	 * This method filters annotations by start and end timestamps.
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return list of object ids
	 */
	public List<String> filterByLastUpdateTimestamp(String startTimestamp, String endTimestamp);
	
	public List<String> getDisabled(MotivationTypes motivation, Date startTimestamp, Date stopTimestamp, int page, int limit);

	public abstract List<String> filterByLastUpdateGreaterThanLastIndexTimestamp();

	/**
	 * Store list of annotations (default mode: insert), i.e. all writes must be inserts.
	 * @param annos List of annotations
	 * @throws AnnotationValidationException
	 * @throws AnnotationMongoException
	 */
	public void create(List<? extends Annotation> annos) throws AnnotationValidationException, BulkOperationException;
	
	/**
	 * Store list of annotations (default mode: insert), i.e. all writes must be inserts.
	 * @param existingAnnos List of existing annotations
	 * @throws AnnotationValidationException
	 * @throws AnnotationMongoException
	 */
	public void update(List<? extends Annotation> existingAnnos) throws AnnotationValidationException, BulkOperationException;

	/**
	 * Store list of annotations (insert/update). Bulk writes must be either inserts or updates for all annotations in the list.
	 * @param annos List of annotations
	 * @param update Update mode: true if existing annotations should be updated
	 * @throws AnnotationValidationException
	 * @throws AnnotationMongoException
	 */
	public void store(List<? extends Annotation> annos, BulkOperationMode bulkOpMode) throws AnnotationValidationException, BulkOperationException;

	public void createBackupCopy(List<? extends Annotation> existingAnnos);

	@Deprecated
	List<? extends Annotation> filterDisabled(String queryParams);

}

