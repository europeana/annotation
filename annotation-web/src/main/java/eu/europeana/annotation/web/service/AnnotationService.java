package eu.europeana.annotation.web.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.stanbol.commons.exception.JsonParseException;
import eu.europeana.annotation.definitions.exception.AnnotationDereferenciationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.exception.response.ModerationNotFoundException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.api.commons.web.exception.HttpException;

public interface AnnotationService {

	/**
	 * This method retrieves all not disabled annotations.
	 * @param identifiers
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationList (List<Long> identifiers);
	
	
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
	 * @throws AnnotationServiceException 
	 * @throws HttpException 
	 */
	public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation, Annotation webAnnotation) throws AnnotationServiceException, HttpException;
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param annoIdentifier
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(long annoIdentifier);
	
	/**
	 * This method enables the annotation by setting the 'disabled' field to null in the database 
	 * and creating the solr annotation.
	 * @param annoIdentifier
	 * @return enabled Annotation
	 */
	public Annotation enableAnnotation(long annoIdentifier);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param annotation The annotation object
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(Annotation annotation);
	
	/**
	 * This method returns annotation object for the given identifier.
	 * @param annoIdentifier - id of the annotation to be retrieved
	 * @param userId - id of the user sending the request (URI)
	 * @param enabled - a flag for telling which annotation to get (enabled, when the flag is true, or disabled)
	 * @return annotation object
	 */
	public Annotation getAnnotationById(long annoIdentifier, String userId, boolean enabled) throws AnnotationNotFoundException, UserAuthorizationException;
		
	
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
	 * Check whether annotation with the given identifier already exists in database.
	 */
	public boolean existsInDb(long annoIdentifier); 
	
	/**
	 * Check whether moderation record with the given identifier already exists in database.
	 * @param annoIdentifier
	 * @return
	 * @throws ModerationMongoException
	 */
	public boolean existsModerationInDb(long annoIdentifier) throws ModerationMongoException;
		
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

	public void validateWebAnnotation(Annotation webAnnotation) throws ParamValidationI18NException, RequestBodyValidationException, PropertyValidationException;

	void validateWebAnnotations(List<? extends Annotation> webAnnotations, BatchReportable batchReportable) throws ParamValidationI18NException;
	
	public void reportNonExisting(List<? extends Annotation> annotations, BatchReportable batchReportable,
			List<Long> missingIdentifiers);

	/**
	 * This method stores moderation record in database
	 * @param newModerationRecord
	 * @return
	 */
	public ModerationRecord storeModerationRecord(ModerationRecord newModerationRecord);
	
	/**
	 * @param annoIdentifier
	 * @return
	 * @throws ModerationNotFoundException
	 * @throws ModerationMongoException
	 */
	public ModerationRecord findModerationRecordById(long annoIdentifier) 
			throws ModerationNotFoundException, ModerationMongoException;

	public List<? extends Annotation> getExisting(List<Long> annotationIdentifiers);

	public void updateExistingAnnotations(BatchReportable batchReportable, List<? extends Annotation> existingAnnos, List<? extends Annotation> updateAnnos, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap) throws AnnotationValidationException, BulkOperationException, IOException, InterruptedException;

	public void insertNewAnnotations(BatchUploadStatus uploadStatus, List<? extends Annotation> annotations, AnnotationDefaults annoDefaults, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap) throws AnnotationValidationException, BulkOperationException, IOException, InterruptedException;

	/**
	 * This method extends the body for semantic tags for dereference profile
	 * @param annotation
	 * @param searchProfile
	 * @param language e.g. "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru" 
	 * @return annotation extended with profile data
	 * @throws AnnotationDereferenciationException
	 * @throws HttpException 
	 * @throws JsonParseException 
	 */
	public void dereferenceSemanticTags(Annotation annotation, SearchProfiles searchProfile, String language) throws HttpException, AnnotationDereferenciationException;
	
	/**
	 * This method extends the body for semantic tags for dereference profile
	 * @param annotations a list of annotation
	 * @param searchProfile the serialization profile
	 * @param language e.g. "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru" 
	 * @throws AnnotationDereferenciationException
	 * @throws HttpException 
	 * @throws JsonParseException 
	 */
	public void dereferenceSemanticTags(List<? extends Annotation> annotations, SearchProfiles searchProfile, String languages) throws AnnotationDereferenciationException, HttpException;

	/**
	 * This method checks for the duplicate annotations in order to ensure the annotation uniqueness.
	 * @param annotation
	 * @param noSelfCheck
	 * @return
	 * @throws AnnotationServiceException
	 */
	public List<String> checkDuplicateAnnotations(Annotation annotation, boolean noSelfCheck) throws AnnotationServiceException;
	
	/**
	 * Returns the deleted annotations in the given date range.
	 * @param motivationType	
	 * @param startDate	
	 * @param stopDate
	 * @param page
	 * @param limit
	 * @return
	 */
	public List<String> getDeletedAnnotationSet(MotivationTypes motivationType, Date startDate, Date stopDate, int page, int limit);

    public List<? extends Annotation> getAllAnnotations();

}
