package eu.europeana.annotation.web.service.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationDereferenciationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.annotation.web.service.AnnotationDefaults;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.web.exception.HttpException;

public class AnnotationServiceImpl extends BaseAnnotationServiceImpl implements AnnotationService {

    @Resource
    PersistentWhitelistService mongoWhitelistPersistence;

    @Resource
    PersistentStatusLogService mongoStatusLogPersistence;

    @Resource
    private AnnotationConfiguration configuration;

    @Resource
    I18nService i18nService;

    private MetisDereferenciationClient dereferenciationClient = new MetisDereferenciationClient();

    AnnotationBuilder annotationBuilder;

    final AnnotationIdHelper annotationIdHelper = new AnnotationIdHelper();

    public AnnotationConfiguration getConfiguration() {
	return configuration;
    }

    public AnnotationIdHelper getAnnotationIdHelper() {
	return annotationIdHelper;
    }

    public AnnotationBuilder getAnnotationHelper() {
	if (annotationBuilder == null)
	    annotationBuilder = new AnnotationBuilder();
	return annotationBuilder;
    }

    public PersistentWhitelistService getMongoWhitelistPersistence() {
	return mongoWhitelistPersistence;
    }

    public void setMongoWhitelistPersistance(PersistentWhitelistService mongoWhitelistPersistence) {
	this.mongoWhitelistPersistence = mongoWhitelistPersistence;
    }

    public PersistentStatusLogService getMongoStatusLogPersistence() {
	return mongoStatusLogPersistence;
    }

    public void setMongoStatusLogPersistance(PersistentStatusLogService mongoStatusLogPersistence) {
	this.mongoStatusLogPersistence = mongoStatusLogPersistence;
    }

    @Override
    public List<? extends Annotation> getAnnotationList(String resourceId) {
	return getMongoPersistence().getAnnotationList(resourceId);
    }

    public MetisDereferenciationClient getDereferenciationClient() {
	return dereferenciationClient;
    }

    public void setDereferenciationClient(MetisDereferenciationClient dereferenciationClient) {
	this.dereferenciationClient = dereferenciationClient;
    }

    @Override
    public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit)
	    throws StatusLogServiceException {
	return getMongoStatusLogPersistence().getFilteredStatusLogList(query, startOn, limit);
    }

    @Override
    public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr)
	    throws JsonParseException, HttpException {

	/**
	 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser ->
	 * JsonLd object
	 */
	try {
	    AnnotationLdParser europeanaParser = new AnnotationLdParser();
	    return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
	} catch (AnnotationAttributeInstantiationException e) {
	    throw new RequestBodyValidationException(annotationJsonLdStr, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
     * europeana.annotation.definitions.model.Annotation)
     */
    @Override
    public Annotation storeAnnotation(Annotation newAnnotation) {
	return storeAnnotation(newAnnotation, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
     * europeana.annotation.definitions.model.Annotation, boolean)
     */
    @Override
    public Annotation storeAnnotation(Annotation newAnnotation, boolean indexing) {

	// must have annotaionId with resourceId and provider.
	validateAnnotationId(newAnnotation);

	// store in mongo database
	Annotation res = getMongoPersistence().store(newAnnotation);

	if (indexing && getConfiguration().isIndexingEnabled()) {
	    // add solr indexing here
	    try {
		getSolrService().store(res);
	    } catch (Exception e) {
		getLogger().info("The annotation was stored correctly into the Mongo, but it was not indexed yet. ", e);
	    }
	    // save the time of the last SOLR indexing
	    updateLastIndexingTime(res, newAnnotation.getLastUpdate());
	}

	return res;
    }

    public ModerationRecord storeModerationRecord(ModerationRecord newModerationRecord) {

	// must have annotaionId with resourceId and provider.
	validateAnnotationIdForModerationRecord(newModerationRecord);

	// store in mongo database
	ModerationRecord res = getMongoModerationRecordPersistence().store(newModerationRecord);

	// lastindexe
	Date lastindexing = res.getLastUpdated();

	if (getConfiguration().isIndexingEnabled()) {
	    try {
		Annotation annotation = getMongoPersistance().find(res.getAnnotationId());
		reindexAnnotation(annotation, lastindexing);
	    } catch (Exception e) {
		getLogger().warn(
			"The moderation record was stored correctly into the Mongo, but related annotation was not indexed with summary yet. ",
			e);
	    }
	}

	return res;
    }

    public ModerationRecord findModerationRecordById(AnnotationId annoId) throws ModerationMongoException {
	return getMongoModerationRecordPersistence().find(annoId);

    }

    /**
     * This method validates AnnotationId object.
     * 
     * @param newAnnotation
     */
    protected void validateAnnotationId(Annotation newAnnotation) {

	if (newAnnotation.getAnnotationId() == null)
	    throw new AnnotationValidationException("Annotaion.AnnotationId must not be null!");

	// if (newAnnotation.getAnnotationId().getResourceId() == null)
	// throw new AnnotationValidationException(
	// "Annotaion.AnnotationId.resourceId must not be null!");
    }

    /**
     * This method validates AnnotationId object for moderation record.
     * 
     * @param newModerationRecord
     */
    protected void validateAnnotationIdForModerationRecord(ModerationRecord newModerationRecord) {

	if (newModerationRecord.getAnnotationId() == null)
	    throw new ModerationRecordValidationException("ModerationRecord.AnnotationId must not be null!");
    }

    @Override
    public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation, Annotation webAnnotation) {
	mergeAnnotationProperties(persistentAnnotation, webAnnotation);
	Annotation res = updateAndReindex(persistentAnnotation);

	return res;
    }

    @SuppressWarnings("deprecation")
    private void mergeAnnotationProperties(PersistentAnnotation annotation, Annotation webAnnotation) {
	if (webAnnotation.getType() != null)
	    annotation.setType(webAnnotation.getType());

//		So my decision for the moment would be to only keep the "id" and "created" immutable.
//
//		With regards to the logic when each of the fields is missing:
//		- If modified is missing, update with the current timestamp, otherwise overwrite.
//		- if creator is missing, keep the previous creator, otherwise overwrite.
//		- if generator is missing, keep the previous generator, otherwise overwrite.
//		- the generated should always be determined by the server.
//		- motivation, body and target are overwritten.

	// Motivation can be changed see #122
//		if (updatedWebAnnotation.getMotivationType() != null
//				&& updatedWebAnnotation.getMotivationType() != storedAnnotation.getMotivationType())
//			throw new RuntimeException("Cannot change motivation type from: " + storedAnnotation.getMotivationType()
//					+ " to: " + updatedWebAnnotation.getMotivationType());
	// if (updatedWebAnnotation.getMotivation() != null)
	// currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());

	if (webAnnotation.getLastUpdate() != null) {
	    annotation.setLastUpdate(webAnnotation.getLastUpdate());
	} else {
	    Date timeStamp = new java.util.Date();
	    annotation.setLastUpdate(timeStamp);
	}

// creator and generator are mandatory, and set during the creation	
//	if (updatedWebAnnotation.getCreator() != null)
//	    annotation.setCreator(updatedWebAnnotation.getCreator());
//
//	if (updatedWebAnnotation.getGenerator() != null)
//	    annotation.setGenerator(updatedWebAnnotation.getGenerator());

//created must not be updated	
//	if (webAnnotation.getCreated() != null)
//	    annotation.setCreated(webAnnotation.getCreated());
//		if (updatedWebAnnotation.getCreator() != null)
//			annotation.setCreator(updatedWebAnnotation.getCreator());
	if (webAnnotation.getGenerated() != null)
	    annotation.setGenerated(webAnnotation.getGenerated());
//		if (updatedWebAnnotation.getGenerator() != null)
//			annotation.setGenerator(updatedWebAnnotation.getGenerator());
	if (webAnnotation.getBody() != null)
	    annotation.setBody(webAnnotation.getBody());
	if (webAnnotation.getTarget() != null)
	    annotation.setTarget(webAnnotation.getTarget());
	if (annotation.isDisabled() != webAnnotation.isDisabled())
	    annotation.setDisabled(webAnnotation.isDisabled());
	if (webAnnotation.getEquivalentTo() != null)
	    annotation.setEquivalentTo(webAnnotation.getEquivalentTo());
	if (webAnnotation.getInternalType() != null)
	    annotation.setInternalType(webAnnotation.getInternalType());
//		if (updatedWebAnnotation.getLastUpdate() != null)
//			annotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
	if (webAnnotation.getSameAs() != null)
	    annotation.setSameAs(webAnnotation.getSameAs());
	if (webAnnotation.getStatus() != null)
	    annotation.setStatus(webAnnotation.getStatus());
	if (webAnnotation.getStyledBy() != null)
	    annotation.setStyledBy(webAnnotation.getStyledBy());
	if (webAnnotation.getCanonical() != null) {
	    // TODO: #404 must never be overwritten
	    if (StringUtils.isEmpty(annotation.getCanonical())) {
		annotation.setCanonical(webAnnotation.getCanonical());
	    }
	}
	if (webAnnotation.getVia() != null)
	    annotation.setVia(webAnnotation.getVia());
    }

    @Override
    public Annotation updateAnnotationStatus(Annotation annotation) {

	return getMongoPersistence().updateStatus(annotation);
    }

    @Override
    public Annotation disableAnnotation(AnnotationId annoId) {
	try {
	    // disable annotation
	    Annotation res = getMongoPersistence().find(annoId);
	    return disableAnnotation(res);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public Annotation disableAnnotation(Annotation annotation) {
	PersistentAnnotation persistentAnnotation;
	try {
	    if (annotation instanceof PersistentAnnotation)
		persistentAnnotation = (PersistentAnnotation) annotation;
	    else
		persistentAnnotation = getMongoPersistence().find(annotation.getAnnotationId());

	    persistentAnnotation.setDisabled(true);
	    persistentAnnotation = getMongoPersistence().update(persistentAnnotation);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	if (getConfiguration().isIndexingEnabled())
	    removeFromIndex(annotation);

	return persistentAnnotation;
    }

    protected void removeFromIndex(Annotation annotation) {
	try {
	    getSolrService().delete(annotation.getAnnotationId());
	} catch (Exception e) {
	    getLogger().error(
		    "Cannot remove annotation from solr index: " + annotation.getAnnotationId().toRelativeUri(), e);
	}
    }

    @Override
    public List<? extends Annotation> getAnnotationListByTarget(String target) {
	return getMongoPersistence().getAnnotationListByTarget(target);
    }

    @Override
    public List<? extends Annotation> getAnnotationListByResourceId(String resourceId) {
	return getMongoPersistence().getAnnotationListByResourceId(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.annotation.web.service.AnnotationService#existsInDb(eu.
     * europeana.annotation.definitions.model.AnnotationId)
     */
    public boolean existsInDb(AnnotationId annoId) {
	boolean res = false;
	try {
	    Annotation dbRes = getMongoPersistence().find(annoId);
	    if (dbRes != null)
		res = true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return res;
    }

    @Override
    public List<? extends Annotation> getExisting(List<String> annotationHttpUrls) {
	try {
	    List<? extends Annotation> dbRes = getMongoPersistence().getAnnotationList(annotationHttpUrls);
	    return dbRes;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public boolean existsModerationInDb(AnnotationId annoId) {
	boolean res = false;
	try {
	    ModerationRecord dbRes = getMongoModerationRecordPersistence().find(annoId);
	    if (dbRes != null)
		res = true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.annotation.web.service.AnnotationService#existsProviderInDb(
     * eu.europeana.annotation.definitions.model.Provider)
     */
//    public boolean existsProviderInDb(Provider provider) {
//	boolean res = false;
//	try {
//	    Provider dbRes = getMongoProviderPersistence().find(provider.getName(), provider.getIdGeneration());
//	    if (dbRes != null)
//		res = true;
//	} catch (Exception e) {
//	    throw new RuntimeException(e);
//	}
//	return res;
//    }

    public void logAnnotationStatusUpdate(String user, Annotation annotation) {
	// store in mongo database
	StatusLog statusLog = new BaseStatusLog();
	statusLog.setUser(user);
	statusLog.setStatus(annotation.getStatus());
	long currentTimestamp = System.currentTimeMillis();
	statusLog.setDate(currentTimestamp);
	statusLog.setAnnotationId(annotation.getAnnotationId());
	getMongoStatusLogPersistence().store(statusLog);
    }

    @Override
    public void validateWebAnnotations(List<? extends Annotation> webAnnotations, BatchReportable batchReportable) {
	for (Annotation webanno : webAnnotations) {
	    try {
		validateWebAnnotation(webanno);
		// TODO: validate via, size must be 1
		batchReportable.incrementSuccessCount();
	    } catch (ParamValidationException | RequestBodyValidationException | PropertyValidationException e) {
		batchReportable.incrementFailureCount();
		String message = i18nService.getMessage(e.getI18nKey(), e.getI18nParams());
		batchReportable.addError(webanno.getAnnotationId().toHttpUrl(), message);
	    }
	}
    }

    @Override
    public void reportNonExisting(List<? extends Annotation> annotations, BatchReportable batchReportable,
	    List<String> missingHttpUrls) {
	for (Annotation anno : annotations) {
	    String httpUrl = anno.getAnnotationId().toHttpUrl();
	    if (httpUrl != null) {
		if (missingHttpUrls.contains(httpUrl)) {
		    batchReportable.incrementFailureCount();
		    batchReportable.addError(anno.getAnnotationId().toHttpUrl(),
			    "Annotation does not exist: " + httpUrl);
		} else
		    batchReportable.incrementSuccessCount();
	    }
	}
    }

    /**
     * Update existing annotations
     * 
     * @param batchReportable          Reportable object for collecting information
     *                                 to be reported
     * @param existingAnnos            Existing annotations
     * @param updateAnnos              Update annotations
     * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting
     *                                 of annotations when returned as response
     */
    @Override
    public void updateExistingAnnotations(BatchReportable batchReportable, List<? extends Annotation> existingAnnos,
	    HashMap<String, ? extends Annotation> updateAnnos,
	    LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
	    throws AnnotationValidationException, BulkOperationException {
	// the size of existing and update lists must match (this must be checked
	// beforehand, so a runtime exception is sufficient here)
	if (existingAnnos.size() != updateAnnos.size())
	    throw new IllegalArgumentException("The existing and update lists must be of equal size");

	// Backup
	getMongoPersistence().createBackupCopy(existingAnnos);

	// merge update annotations (web anno) into existing annotations (db anno)
	for (int i = 0; i < existingAnnos.size(); i++) {
	    Annotation existingAnno = existingAnnos.get(i);
	    String existingHttpUrl = existingAnno.getAnnotationId().getHttpUrl();
	    Annotation updateAnno = updateAnnos.get(existingHttpUrl);

	    // merge update annotation (web anno) into existing annotation (db anno)
	    this.mergeAnnotationProperties((PersistentAnnotation) existingAnno, updateAnno);

	    // set last update
	    existingAnno.setLastUpdate(new Date());

	    // store annotation in the "web annotation - stored annotation" map - used to
	    // preserve the order
	    // of submitted annotations.
	    webAnnoStoredAnnoAnnoMap.put(updateAnno, existingAnno);
	}
	getMongoPersistence().update(existingAnnos);
    }

    /**
     * Update existing annotations
     * 
     * @param batchReportable          Reportable object for collecting information
     *                                 to be reported
     * @param existingAnnos            Existing annotations
     * @param updateAnnos              Update annotations
     * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting
     *                                 of annotations when returned as response
     */
    @Override
    public void insertNewAnnotations(BatchUploadStatus uploadStatus, List<? extends Annotation> annotations,
	    AnnotationDefaults annoDefaults, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
	    throws AnnotationValidationException, BulkOperationException {

	int count = annotations.size();

	List<AnnotationId> annoIdSequence = null;
	annoIdSequence = generateAnnotationIds(count);

	// number of ids must equal number of annotations - not applicable in case the
	// id is provided by the via field
	if ((annotations.size() != annoIdSequence.size()))
	    throw new IllegalStateException("The list of new annotations and corresponding ids are not of equal size");

	AnnotationId newAnnoId;
	Annotation anno;
	AnnotationId genAnnoId;
	for (int i = 0; i < annotations.size(); i++) {
	    // default: use the annotation id from the sequence generated above
	    genAnnoId = annoIdSequence.get(i);
	    newAnnoId = new BaseAnnotationId(genAnnoId.getBaseUrl(), genAnnoId.getIdentifier());

	    anno = annotations.get(i);
	    anno.setAnnotationId(newAnnoId);
	    annoDefaults.putAnnotationDefaultValues(anno);
	    // store annotation in the "web annotation - stored annotation" map - used to
	    // preserve the order
	    // of submitted annotations.
	    webAnnoStoredAnnoAnnoMap.put(anno, anno);
	}
	getMongoPersistence().create(annotations);
    }

    public List<AnnotationId> generateAnnotationIds(int count) {
	List<AnnotationId> annoIdSequence = getMongoPersistence().generateAnnotationIdSequence(count);
	return annoIdSequence;
    }

    @Override
    public void dereferenceSemanticTags(Annotation annotation, SearchProfiles searchProfile, String language)
	    throws HttpException, AnnotationDereferenciationException {
	// will update the body only when dereference profile is used
	if (!isDereferenceProfile(searchProfile)) {
	    return;
	}

	if (!hasBodyUrl(annotation)) {
	    return;
	}

	String bodyValue = annotation.getBody().getValue();
	Map<String, String> dereferencedMap = getDereferenciationClient().dereferenceOne(getConfiguration().getMetisBaseUrl(), bodyValue,
		language);
	setDereferencedBody(annotation, dereferencedMap);
    }

    private void setDereferencedBody(Annotation annotation, Map<String, String> dereferencedMap) {
	String bodyValue = annotation.getBody().getValue();
	if(!dereferencedMap.containsKey(bodyValue)) {
	    return;
	}
	String dereferencedJsonLdMapStr = dereferencedMap.get(bodyValue);
	// replace URI with dereferenced entity
	if (StringUtils.isNotBlank(dereferencedJsonLdMapStr)) {
	    annotation.getBody().setValue(dereferencedJsonLdMapStr);
	    annotation.getBody().setInputString(dereferencedJsonLdMapStr);
	}
    }

    @Override
    public void dereferenceSemanticTags(List<? extends Annotation> annotations, SearchProfiles searchProfile, String languages)
	    throws AnnotationDereferenciationException, HttpException {
	// will update the body only when dereference profile is used
	if (!isDereferenceProfile(searchProfile)) {
	    return;
	}
	if (annotations == null || annotations.isEmpty()) {
	    return;
	}

	List<String> entityIds = extractEntityUris(annotations);
	//check if dereferenciation is possible
	if(entityIds.isEmpty()) {
	    return;
	}
	
	Map<String, String> dereferencedMap = getDereferenciationClient().dereferenceMany(getConfiguration().getMetisBaseUrl(), 
		entityIds, languages);
	
	//update dereferenced bodies
	for (Annotation annotation : annotations) {
	    setDereferencedBody(annotation, dereferencedMap);
	}	
    }

    private List<String> extractEntityUris(List<? extends Annotation> annotations) {
	List<String> entityIds = new ArrayList<String>();
	for (Annotation annotation : annotations) {
	    if (isSemanticTag(annotation)) {
		entityIds.add(annotation.getBody().getValue());
	    }
	}
	return entityIds;
    }

    public List<AnnotationDeletion> getDeletedAnnotationSet(MotivationTypes motivation, String startDate,
	    String startTimestamp) {
	if (!StringUtils.isBlank(startDate)) {
	    startTimestamp = TypeUtils.getUnixDateStringFromDate(startDate);
	}

	String motivationOaType = null;
	if(motivation!=null) { 
		motivationOaType = motivation.getOaType();
	}
	List<AnnotationDeletion> res = getMongoPersistence().getDeletedByLastUpdateTimestamp(motivationOaType, startTimestamp);

	return res;
    }
    
    protected boolean validateResource(String url) throws ParamValidationException {
	    
        String domainName;
        try {
            domainName = getMongoWhitelistPersistence().getDomainName(url);
            Set<String> domains = getMongoWhitelistPersistence().getWhitelistDomains();
            if (!domains.contains(domainName))
        	throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE,
        		I18nConstants.INVALID_PARAM_VALUE, new String[] { "target.value", url });
        } catch (URISyntaxException e) {
            throw new ParamValidationException(ParamValidationException.MESSAGE_URL_NOT_VALID,
        	    I18nConstants.MESSAGE_URL_NOT_VALID, new String[] { "target.value", url });
        }
    
        return true;
    }
}
