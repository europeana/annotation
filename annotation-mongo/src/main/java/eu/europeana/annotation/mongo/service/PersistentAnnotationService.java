package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentAnnotationService extends AbstractNoSqlService<PersistentAnnotation, String>{

//	public abstract ImageAnnotation store(ImageAnnotation object) throws AnnotationValidationException;

	public abstract Annotation store(Annotation object) throws AnnotationValidationException;

//	public abstract ObjectTag store(ObjectTag object) throws AnnotationValidationException;

	public List<? extends Annotation> getAnnotationList(String europeanaId);

//	public List<? extends Annotation> getAnnotationListByProvider(String europeanaId, String provider);

	public List<? extends Annotation> getAnnotationListByTarget(String target);

	public List<? extends Annotation> getAnnotationListByResourceId(String resourceId);

	/**
	 * This method retrieves annotations applying filters.
	 * @param europeanaId
	 * @param provider
	 * @param startOn
	 * @param limit
	 * @param isDisabled
	 * @return the list of annotations
	 */
	public List<? extends Annotation> getFilteredAnnotationList (
			String europeanaId, String provider, String startOn, String limit, boolean isDisabled);

	public List<? extends Annotation> getAnnotationList (List<String> annotationIds);
	
//	public List<? extends Annotation> getAnnotationListByUrl (List<String> annotationIds);
		
	
	//public PersistentAnnotation find(String provider, String identifier);
	
	//public PersistentAnnotation find(String europeanaId, String provider, String identifier);
	
	public PersistentAnnotation find(AnnotationId annoId);
	
	
//	/**
//	 * @param baseUrl
//	 * @param resourceId
//	 * @param annotationNr
//	 * @throws AnnotationMongoRuntimeException - less or more than 1 object is found for the given arguments
//	 */
//	public void remove(String baseUrl, String provider, String identifier);
//	public void remove(String resourceId, String provider, Long annotationNr);
	
	/**
	 * 
	 * @param annoId
	 * @throws AnnotationMongoRuntimeException - less or more than 1 object is found for the given arguments
	 */
	public void remove(AnnotationId annoId) throws AnnotationMongoException;
	
	/**
	 * This method performs update for the passed annotation object
	 * @param annotation
	 */
	public PersistentAnnotation update(PersistentAnnotation annotation) throws AnnotationValidationException;

	/**
	 * This method notices the time of the last SOLR indexing for particular annotation
	 * @param annoId
	 * @throws AnnotationMongoException 
	 */
	public Annotation updateIndexingTime(AnnotationId annoId, Date lastIndexingDate) throws AnnotationMongoException;

	/**
	 * This method changes annotation status.
	 * @param newAnnotation
	 * @return
	 */
	public Annotation updateStatus(Annotation newAnnotation);
	
	public abstract AnnotationId generateAnnotationId(String provider);
//	public abstract AnnotationId generateAnnotationId(String resourceId);

	public abstract Annotation findByTagId(String tagId);
	
	/**
	 * This method filters annotations by start and end timestamps.
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return list of object ids
	 */
	public List<String> filterByLastUpdateTimestamp(String startTimestamp, String endTimestamp);

}

