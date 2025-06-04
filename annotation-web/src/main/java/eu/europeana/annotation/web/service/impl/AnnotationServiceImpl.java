package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationDereferenciationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;
import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.AnnotationIndexingException;
import eu.europeana.annotation.web.exception.request.AnnotationUniquenessValidationException;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.annotation.web.service.AnnotationDefaults;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api.common.config.I18nConstantsAnnotation;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.web.exception.HttpException;

@Service(AnnotationConfiguration.BEAN_ANNO_SERVICE)
public class AnnotationServiceImpl extends BaseAnnotationServiceImpl implements AnnotationService {

  @Resource(name = "annotation_db_whitelistService")
  PersistentWhitelistService mongoWhitelistPersistence;

  @Resource(name = "annotation_db_statusLogService")
  PersistentStatusLogService mongoStatusLogPersistence;

  @Resource(name = "i18nService")
  I18nService i18nService;

  @Autowired
  @Qualifier(AnnotationConfiguration.BEAN_METIS_DEREFERENCE_CLIENT)
  MetisDereferenciationClient dereferenciationClient;
     
  public AnnotationConfiguration getConfiguration() {
    return configuration;
  }
  
  public PersistentWhitelistService getMongoWhitelistPersistence() {
    return mongoWhitelistPersistence;
  }

  public PersistentStatusLogService getMongoStatusLogPersistence() {
    return mongoStatusLogPersistence;
  }

  @Override
  public List<? extends Annotation> getAnnotationList(List<Long> identifiers) {
    return getMongoPersistence().getAnnotationList(identifiers);
  }

  @Override
  public List<? extends Annotation> getAllAnnotations() {
    return getMongoPersistence().getAllAnnotations();
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
     * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser -> JsonLd object
     */
    try {
      AnnotationLdParser europeanaParser = new AnnotationLdParser();
      return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
    } catch (AnnotationAttributeInstantiationException e) {
      throw new RequestBodyValidationException(annotationJsonLdStr,
          I18nConstantsAnnotation.ANNOTATION_CANT_PARSE_BODY, e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
   * europeana.annotation.definitions.model.Annotation)
   */
  @Override
  public Annotation storeAnnotation(Annotation newAnnotation) throws AnnotationServiceException {
    return storeAnnotation(newAnnotation, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
   * europeana.annotation.definitions.model.Annotation, boolean)
   */
  @Override
  public Annotation storeAnnotation(Annotation newAnnotation, boolean indexing) throws AnnotationServiceException {

    validateAnnotationIdentifier(newAnnotation);

    // store in mongo database
    Annotation res = getMongoPersistence().store(newAnnotation);

    if (indexing && getConfiguration().isIndexingEnabled()) {
      try {
        // add solr indexing here
        getSolrService().store(res);
      } catch(AnnotationServiceException e) {
        //need to annotation created in mongo
        rollbackCreatedAnnotation(newAnnotation);
        //rethrow exception
        throw e;    
      }
      
      // save the time of the last SOLR indexing
      updateLastIndexingTime(res, newAnnotation.getLastUpdate());
    }

    return res;
  }

  private void rollbackCreatedAnnotation(Annotation newAnnotation) throws AnnotationServiceException {
    try {
      getMongoPersistence().remove(newAnnotation.getIdentifier());
    } catch (AnnotationMongoException e) {
      throw new AnnotationServiceException("Rollback for reated annotation failed, annotation identifier: " + newAnnotation.getIdentifier());
    }
  }

  public ModerationRecord storeModerationRecord(ModerationRecord newModerationRecord) throws AnnotationIndexingException {

    // must have annotaionId with resourceId and provider.
    validateAnnotationIdForModerationRecord(newModerationRecord);

    // store in mongo database
    ModerationRecord res = getMongoModerationRecordPersistence().store(newModerationRecord);

    // lastindexe
    Date lastindexing = res.getLastUpdated();

    if (getConfiguration().isIndexingEnabled()) {
        Annotation annotation = getMongoPersistance().getByIdentifier(res.getIdentifier());
        reindexAnnotation(annotation, lastindexing);
    }

    return res;
  }

  public ModerationRecord findModerationRecordById(long annoIdentifier)
      throws ModerationMongoException {
    return getMongoModerationRecordPersistence().find(annoIdentifier);

  }

  protected void validateAnnotationIdentifier(Annotation newAnnotation) {
    if (!(newAnnotation.getIdentifier() > 0)) {
      throw new AnnotationValidationException("Annotaion identifier must be >0!");
    }
  }

  /**
   * This method validates AnnotationId object for moderation record.
   * 
   * @param newModerationRecord
   */
  protected void validateAnnotationIdForModerationRecord(ModerationRecord newModerationRecord) {

    if (!(newModerationRecord.getIdentifier() > 0))
      throw new ModerationRecordValidationException("ModerationRecord identifier must be >0!");
  }

  @Override
  public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation,
      Annotation webAnnotation) throws AnnotationServiceException, HttpException {
    replaceAnnotationProperties(persistentAnnotation, webAnnotation);
    // check that the updated annotation is unique
    Set<String> duplicateAnnotationIds = checkDuplicateAnnotations(persistentAnnotation, true);
    if (!duplicateAnnotationIds.isEmpty()) {
      String[] i18nParamsAnnoDuplicates = new String[1];
      i18nParamsAnnoDuplicates[0] = String.join(",", duplicateAnnotationIds);
      throw new AnnotationUniquenessValidationException(I18nConstantsAnnotation.ANNOTATION_DUPLICATION,
          I18nConstantsAnnotation.ANNOTATION_DUPLICATION, i18nParamsAnnoDuplicates);
    }

    Annotation res = updateAndReindex(persistentAnnotation);
    return res;
  }

  @SuppressWarnings("deprecation")
  private void replaceAnnotationProperties(PersistentAnnotation annotation,
      Annotation webAnnotation) {
    annotation.setType(webAnnotation.getType());
    annotation.setBody(webAnnotation.getBody());
    annotation.setTarget(webAnnotation.getTarget());
    annotation.setDisabled(webAnnotation.getDisabled());
    annotation.setEquivalentTo(webAnnotation.getEquivalentTo());
    annotation.setInternalType(webAnnotation.getInternalType());
    annotation.setStatus(webAnnotation.getStatus());
    annotation.setStyledBy(webAnnotation.getStyledBy());
    
    replaceReferenceFields(annotation, webAnnotation);   
    
    Date now = new Date();
    //reset generated
    if(webAnnotation.getGenerated() != null) {
      annotation.setGenerated(webAnnotation.getGenerated());
    } else {
      annotation.setGenerated(now);
    }
    resetLastUpdate(annotation, now);
  }

  private void replaceReferenceFields(PersistentAnnotation annotation, Annotation webAnnotation) {
    annotation.setSameAs(webAnnotation.getSameAs());    
    annotation.setCanonical(webAnnotation.getCanonical());
    annotation.setVia(webAnnotation.getVia());
  }

  private void resetLastUpdate(PersistentAnnotation annotation, Date now) {
    // So my decision for the moment would be to only keep the "id" and "created" immutable.
    //
    // With regards to the logic when each of the fields is missing:
    // - If modified is missing, update with the current timestamp, otherwise overwrite.
    // - if creator is missing, keep the previous creator, otherwise overwrite.
    // - if generator is missing, keep the previous generator, otherwise overwrite.
    // - the generated should always be determined by the server.
    // - motivation, body and target are overwritten.

    // Motivation can be changed see #122
    // if (updatedWebAnnotation.getMotivationType() != null
    // && updatedWebAnnotation.getMotivationType() != storedAnnotation.getMotivationType())
    // throw new RuntimeException("Cannot change motivation type from: " +
    // storedAnnotation.getMotivationType()
    // + " to: " + updatedWebAnnotation.getMotivationType());
    // if (updatedWebAnnotation.getMotivation() != null)
    // currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());
    annotation.setLastUpdate(now);
  }

  @Override
  public Annotation updateAnnotationStatus(Annotation annotation) {

    return getMongoPersistence().updateStatus(annotation);
  }

  @Override
  public Annotation disableAnnotation(long annoIdentifier) {
    // disable annotation
    Annotation res = getMongoPersistence().getByIdentifier(annoIdentifier);
    return disableAnnotation(res);
  }

  @Override
  public Annotation disableAnnotation(Annotation annotation) {
    PersistentAnnotation persistentAnnotation;

    if (annotation instanceof PersistentAnnotation)
      persistentAnnotation = (PersistentAnnotation) annotation;
    else
      persistentAnnotation = getMongoPersistence().getByIdentifier(annotation.getIdentifier());

    persistentAnnotation.setDisabled(new Date());
    persistentAnnotation = getMongoPersistence().update(persistentAnnotation);

    if (getConfiguration().isIndexingEnabled())
      removeFromIndex(annotation);

    return persistentAnnotation;
  }

  @Override
  public Annotation enableAnnotation(long annoIdentifier) throws AnnotationServiceException {
    PersistentAnnotation persistentAnnotation;
    persistentAnnotation = getMongoPersistence().getByIdentifier(annoIdentifier);
    persistentAnnotation.setDisabled(null);
    persistentAnnotation = getMongoPersistence().update(persistentAnnotation);

    if (getConfiguration().isIndexingEnabled()) {
        getSolrService().store(persistentAnnotation);
      // save the time of the last SOLR indexing
      updateLastIndexingTime(persistentAnnotation, persistentAnnotation.getLastUpdate());
    }
    return persistentAnnotation;
  }

  protected boolean removeFromIndex(Annotation annotation) {
    try {
      getSolrService().delete(annotation.getIdentifier());
    } catch (Exception e) {
      getLogger().error("Cannot remove annotation from solr index: " + annotation.getIdentifier(),
          e);
      return false;
    }
    return true;
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
  public boolean existsInDb(long annoIdentifier) {
    boolean res = false;
    try {
      Annotation dbRes = getMongoPersistence().getByIdentifier(annoIdentifier);
      if (dbRes != null)
        res = true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return res;
  }

  @Override
  public List<? extends Annotation> getExisting(List<Long> annotationIdentifiers) {
    List<? extends Annotation> dbRes =
        getMongoPersistence().getAnnotationList(annotationIdentifiers);
    return dbRes;
  }

  public boolean existsModerationInDb(long annoIdentifier) throws ModerationMongoException {
    boolean res = false;
    ModerationRecord dbRes = getMongoModerationRecordPersistence().find(annoIdentifier);
    if (dbRes != null)
      res = true;
    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see eu.europeana.annotation.web.service.AnnotationService#existsProviderInDb(
   * eu.europeana.annotation.definitions.model.Provider)
   */
  // public boolean existsProviderInDb(Provider provider) {
  // boolean res = false;
  // try {
  // Provider dbRes = getMongoProviderPersistence().find(provider.getName(),
  // provider.getIdGeneration());
  // if (dbRes != null)
  // res = true;
  // } catch (Exception e) {
  // throw new RuntimeException(e);
  // }
  // return res;
  // }

  public void logAnnotationStatusUpdate(String user, Annotation annotation) {
    // store in mongo database
    StatusLog statusLog = new BaseStatusLog();
    statusLog.setUser(user);
    statusLog.setStatus(annotation.getStatus());
    long currentTimestamp = System.currentTimeMillis();
    statusLog.setDate(currentTimestamp);
    statusLog.setIdentifier(annotation.getIdentifier());
    getMongoStatusLogPersistence().store(statusLog);
  }

  @Override
  public void validateWebAnnotations(List<? extends Annotation> webAnnotations,
      BatchReportable batchReportable, Authentication authentication) {
    for (Annotation webanno : webAnnotations) {
      try {
        validateWebAnnotation(webanno, authentication);
        //NOTE: validate via, size must be 1
        batchReportable.incrementSuccessCount();
      } catch (ParamValidationI18NException | RequestBodyValidationException
          | PropertyValidationException e) {
        batchReportable.incrementFailureCount();
        String message = i18nService.getMessage(e.getI18nKey(), e.getI18nParams());
        batchReportable.addError(String.valueOf(webanno.getIdentifier()), message);
      }
    }
  }

  @Override
  public void reportNonExisting(List<? extends Annotation> annotations,
      BatchReportable batchReportable, List<Long> missingIdentifiers) {
    for (Annotation anno : annotations) {
      if (missingIdentifiers.contains(anno.getIdentifier())) {
        batchReportable.incrementFailureCount();
        batchReportable.addError(String.valueOf(anno.getIdentifier()),
            "Annotation does not exist: " + anno.getIdentifier());
      } else {
        batchReportable.incrementSuccessCount();
      }
    }
  }

  /**
   * Update existing annotations
   * 
   * @param batchReportable Reportable object for collecting information to be reported
   * @param existingAnnos Existing annotations
   * @param updateAnnos Update annotations
   * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting of annotations
   *        when returned as response
   * @throws InterruptedException
   * @throws IOException
   */
  @Override
  public void updateExistingAnnotations(BatchReportable batchReportable,
      List<? extends Annotation> existingAnnos, List<? extends Annotation> updateAnnos,
      LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
      throws AnnotationValidationException, BulkOperationException, IOException,
      InterruptedException {
    // the size of existing and update lists must match (this must be checked
    // beforehand, so a runtime exception is sufficient here)
    if (existingAnnos.size() != updateAnnos.size())
      throw new IllegalArgumentException("The existing and update lists must be of equal size");

    // Backup
    getMongoPersistence().createBackupCopy(existingAnnos);

    // merge update annotations (web anno) into existing annotations (db anno)
    for (int i = 0; i < existingAnnos.size(); i++) {
      Annotation existingAnno = existingAnnos.get(i);
      Annotation updateAnno =
          updateAnnos.stream().filter(anno -> anno.getIdentifier() == existingAnno.getIdentifier())
              .findAny().orElse(null);

      if (updateAnno == null)
        continue;

      // merge update annotation (web anno) into existing annotation (db anno)
      this.replaceAnnotationProperties((PersistentAnnotation) existingAnno, updateAnno);

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
   * @param batchReportable Reportable object for collecting information to be reported
   * @param existingAnnos Existing annotations
   * @param updateAnnos Update annotations
   * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting of annotations
   *        when returned as response
   * @throws InterruptedException
   * @throws IOException
   */
  @Override
  public void insertNewAnnotations(BatchUploadStatus uploadStatus,
      List<? extends Annotation> annotations, AnnotationDefaults annoDefaults,
      LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
      throws AnnotationValidationException, BulkOperationException, IOException,
      InterruptedException {

    int count = annotations.size();

    List<Long> annoIdSequence = generateAnnotationIdentifiers(count);

    // number of ids must equal number of annotations - not applicable in case the
    // id is provided by the via field
    if ((annotations.size() != annoIdSequence.size()))
      throw new IllegalStateException(
          "The list of new annotations and corresponding ids are not of equal size");

    Annotation anno;
    for (int i = 0; i < annotations.size(); i++) {
      // default: use the annotation id from the sequence generated above
      anno = annotations.get(i);
      anno.setIdentifier(annoIdSequence.get(i));
      annoDefaults.putAnnotationDefaultValues(anno);
      // store annotation in the "web annotation - stored annotation" map - used to
      // preserve the order
      // of submitted annotations.
      webAnnoStoredAnnoAnnoMap.put(anno, anno);
    }
    getMongoPersistence().create(annotations);
  }

  public List<Long> generateAnnotationIdentifiers(int count) {
    List<Long> annoIdSequence = getMongoPersistence().generateAnnotationIdentifierSequence(count);
    return annoIdSequence;
  }

  @Override
  public void dereferenceSemanticTags(Annotation annotation, SearchProfiles searchProfile,
      String language) throws HttpException, AnnotationDereferenciationException {
    // will update the body only when dereference profile is used
    if (!isDereferenceProfile(searchProfile)) {
      return;
    }

    if (!hasBodyUrl(annotation)) {
      return;
    }

    String bodyValue = annotation.getBody().getValue();
    Map<String, String> dereferencedMap = dereferenciationClient.dereferenceOne(bodyValue, language);
    setDereferencedBody(annotation, dereferencedMap);
  }

  private void setDereferencedBody(Annotation annotation, Map<String, String> dereferencedMap) {
    String bodyValue = annotation.getBody().getValue();
    if (!dereferencedMap.containsKey(bodyValue)) {
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
  public void dereferenceSemanticTags(List<? extends Annotation> annotations,
      SearchProfiles searchProfile, String languages)
      throws AnnotationDereferenciationException, HttpException {
    // will update the body only when dereference profile is used
    if (!isDereferenceProfile(searchProfile)) {
      return;
    }
    if (annotations == null || annotations.isEmpty()) {
      return;
    }
    
    List<String> entityIds = extractEntityUrisFromBody(annotations);
    // check if dereferenciation is possible
    if (entityIds.isEmpty()) {
      return;
    }

    Map<String, String> dereferencedMap = dereferenciationClient.dereferenceMany(entityIds, languages);

    // update dereferenced bodies
    for (Annotation annotation : annotations) {
      setDereferencedBody(annotation, dereferencedMap);
    }
  }

  private List<String> extractEntityUrisFromBody(List<? extends Annotation> annotations) {
    List<String> entityIds = new ArrayList<String>();
    for (Annotation annotation : annotations) {
      if (isSemanticTagWithUrl(annotation)) {
        entityIds.add(annotation.getBody().getValue());
      }else if(isHighlightWithUrl(annotation)) {
        entityIds.add(annotation.getBody().getValue());
      }
    }
    return entityIds;
  }

  public List<String> getDeletedAnnotationSet(MotivationTypes motivation, Date startDate,
      Date stopDate, int page, int limit) {
    List<String> res =
        getMongoPersistence().getDisabled(motivation, startDate, stopDate, page, limit);
    return res;
  }

  @Deprecated
  /**
   * @deprecated change to property validation exception
   */
  @Override
  protected boolean validateLinkingAgainstWhitelist(String url) throws ParamValidationI18NException {

    String domainName;
    try {
      domainName = getMongoWhitelistPersistence().getDomainName(url);
      Set<String> domains = getMongoWhitelistPersistence().getWhitelistDomains();
      if (!domains.contains(domainName))
        throw new ParamValidationI18NException(I18nConstantsAnnotation.INVALID_PARAM_VALUE,
            I18nConstantsAnnotation.INVALID_PARAM_VALUE, new String[] {"target.value", url});
    } catch (URISyntaxException e) {
      throw new ParamValidationI18NException(ParamValidationI18NException.MESSAGE_URL_NOT_VALID,
          I18nConstantsAnnotation.MESSAGE_URL_NOT_VALID, new String[] {"target.value", url});
    }

    return true;
  }

  @Override
  public Set<String> checkDuplicateAnnotations(Annotation annotation, boolean noSelfCheck)
      throws AnnotationServiceException {
    return getSolrService().checkDuplicateAnnotations(annotation, noSelfCheck);
  }

  @Override
  public void validateImmutableFields(Annotation updateWebAnnotation, Annotation storedAnnotation) throws HttpException{
    
    final String imutable = " (immutable)";
    //verify id/identifier
    if(updateWebAnnotation.getIdentifier() > 0 && updateWebAnnotation.getIdentifier() != storedAnnotation.getIdentifier()) {
      throw new ParamValidationI18NException(I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          I18nConstantsAnnotation.INVALID_PARAM_VALUE, new String[] {WebAnnotationFields.ID + imutable,
              AnnotationIdHelper.buildAnnotationUri(configuration.getAnnotationBaseUrl(), updateWebAnnotation.getIdentifier())}); 
    }
    
    //verify creator
    if(updateWebAnnotation.getCreator() != null && !updateWebAnnotation.getCreator().equals(storedAnnotation.getCreator())) {
      throw new ParamValidationI18NException(I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          I18nConstantsAnnotation.INVALID_PARAM_VALUE, new String[] {WebAnnotationModelFields.CREATOR + imutable, updateWebAnnotation.getCreator().toString()});
    }
    
    //verify created
    if(updateWebAnnotation.getCreated() != null && !updateWebAnnotation.getCreated().equals(storedAnnotation.getCreated())) {
      throw new ParamValidationI18NException(I18nConstantsAnnotation.INVALID_PARAM_VALUE,
          I18nConstantsAnnotation.INVALID_PARAM_VALUE, new String[] {WebAnnotationModelFields.CREATED + imutable, TypeUtils.convertDateToStr(updateWebAnnotation.getCreated())});
    }    
    
  }

}
