package eu.europeana.annotation.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.exception.response.ModerationNotFoundException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.api.commons.web.exception.HttpException;

public interface AnnotationService {

	public String getComponentName();
	
	/**
	 * This method retrieves all not disabled annotations.
	 * @param resourceId
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationList (String resourceId);
	
	
	/**
	 * This method retrieves all not disabled annotations for given target.
	 * @param target
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationListByTarget (String target);
	
	/**
	 * This method retrieves all not disabled annotations for given resourceId.
	 * @param resourceId
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationListByResourceId (String resourceId);
	
	
	/**
	 * This method creates Europeana Annotation object from a JsonLd string.
	 * @param motivationType 
	 * @param annotationJsonLdStr
	 * @return Annotation object
	 * @throws JsonParseException 
	 * @throws HttpException 
	 */
	public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr) throws JsonParseException, HttpException;

	/**
	 * This method stores Annotation object in database and in Solr.
	 * @param annotation
	 * @return Annotation object
	 */
	public Annotation storeAnnotation(Annotation annotation);

	/**
	 * This method stores Annotation object in database and in Solr if 'indexing' is true.
	 * @param annotation
	 * @param indexing
	 * @return Annotation object
	 */
	public Annotation storeAnnotation(Annotation annotation, boolean indexing);

	/**
	 * update (stored) <code>persistentAnnotation</code> with values from <code>webAnnotation</code>
	 * @param persistentAnnotation
	 * @param webAnnotation
	 * @return
	 */
	public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation, Annotation webAnnotation);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param annoId
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(AnnotationId annoId);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param annotation The annotation object
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(Annotation annotation);
	
	/**
	 * This method returns annotation object for given annotationId that
	 * comprises provider and identifier.
	 * @param annoId - id of the annotation to be retrieved
	 * @param userId - id of the user sending the request (URI)
	 * @return annotation object
	 */
	public Annotation getAnnotationById(AnnotationId annoId, String userId) throws AnnotationNotFoundException, UserAuthorizationException;
		
	/**
	 * Search for annotations by the given text query, row start position and rows limit. 	 
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws AnnotationServiceException 
	 */
	//TODO: change parameters to integers
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit) 
			throws AnnotationServiceException;
	
	
	/**
	 * Search for annotation status logs by the given text query, row start position and rows limit.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws StatusLogServiceException 
	 */
	public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit) throws StatusLogServiceException;

	/**
	 * This method is used for query faceting.
	 * @param qf
	 * @param queries
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public Map<String, Integer> searchAnnotations(String [] qf, List<String> queries) throws AnnotationServiceException;
	
	/**
	 * Check whether annotation for given provider and identifier already exist in database.
	 */
	public boolean existsInDb(AnnotationId annoId); 
	
	/**
	 * Check whether moderation record for given provider and identifier already exist in database.
	 * @param annoId
	 * @return
	 */
	public boolean existsModerationInDb(AnnotationId annoId);
	
//	/**
//	 * Check whether given provider already exists in database.
//	 */
//	public boolean existsProviderInDb(Provider provider); 
	
	/**
	 * This method updates annotation status.
	 * @param annotation
	 * @return
	 */
	public Annotation updateAnnotationStatus(Annotation annotation);
	
	/**
	 * @param annotation
	 * @return
	 */
	public void logAnnotationStatusUpdate(String user, Annotation annotation);
	
	
	/**
	 * This method is checking the visibility of the annotation stored in the database.
	 * @param annotation The stored annotation object
	 * @param user The name of the current user
	 * @return annotation object if check was successful, exception otherwise
	 * @throws AnnotationStateException
	 */
	public void checkVisibility(Annotation annotation, String user) throws AnnotationStateException;

	/**
	 * this method validates the correctness of the provided annotation id (provider and identifier) 
	 * @param annoId
	 * @throws ParamValidationException 
	 */
	public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException;

//	void indexAnnotation(AnnotationId annoId);

	public void validateWebAnnotation(Annotation webAnnotation) throws ParamValidationException, RequestBodyValidationException, PropertyValidationException;

	void validateWebAnnotations(List<? extends Annotation> webAnnotations, BatchReportable batchReportable) throws ParamValidationException;
	
	public void reportNonExisting(List<? extends Annotation> annotations, BatchReportable batchReportable,
			List<String> annotationHttpUrls);

	/**
	 * This method stores moderation record in database
	 * @param newModerationRecord
	 * @return
	 */
	public ModerationRecord storeModerationRecord(ModerationRecord newModerationRecord);
	
	/**
	 * @param annoId
	 * @return
	 * @throws ModerationNotFoundException
	 * @throws ModerationMongoException
	 */
	public ModerationRecord findModerationRecordById(AnnotationId annoId) 
			throws ModerationNotFoundException, ModerationMongoException;

	public List<? extends Annotation> getExisting(List<String> annotationHttpUrls);

	public void updateExistingAnnotations(BatchReportable batchReportable, List<? extends Annotation> existingAnnos, HashMap<String, ? extends Annotation> updateAnnos, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap) throws AnnotationValidationException, BulkOperationException;

	public void insertNewAnnotations(BatchUploadStatus uploadStatus, List<? extends Annotation> annotations, AnnotationDefaults annoDefaults, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap) throws AnnotationValidationException, BulkOperationException;

	/**
	 * This method extends annotation according to the search profile
	 * @param annotation
	 * @param searchProfile
	 * @param language e.g. "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru" 
	 * @return annotation extended with profile data
	 * @throws IOException
	 * @throws HttpException 
	 * @throws JsonParseException 
	 */
	public void dereferenceSemanticTags(Annotation annotation, SearchProfiles searchProfile, String language) throws HttpException, IOException;
	
	/*
	 * This methods returns annotation ids which where deleted after a given date 
	 * @param startDate
	 * @param startTimestamp
	 * @return deleted annotation ids
	 */
	public List<AnnotationDeletion> getDeletedAnnotationSet(MotivationTypes motivationType, String startDate, String startTimestamp);
	
	
}
