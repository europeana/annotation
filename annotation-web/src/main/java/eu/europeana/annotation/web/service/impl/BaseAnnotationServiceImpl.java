package eu.europeana.annotation.web.service.impl;

import java.util.Date;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.utils.GeneralUtils;
import eu.europeana.annotation.web.exception.AnnotationIndexingException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.api.common.config.I18nConstantsAnnotation;

public abstract class BaseAnnotationServiceImpl extends BaseAnnotationValidator {

  protected static Logger logger = LogManager.getLogger(BaseAnnotationServiceImpl.class);

  @Resource
  AnnotationConfiguration configuration;

  @Autowired
  @Qualifier(AnnotationConfiguration.BEAN_SOLR_ANNO_SERVICE)
  SolrAnnotationService solrAnnotationService;

  @Resource
  PersistentAnnotationService mongoPersistance;

  @Resource(name = "annotation_db_moderationRecordService")
  PersistentModerationRecordService mongoModerationRecordPersistance;



  protected AnnotationConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(AnnotationConfiguration configuration) {
    this.configuration = configuration;
  }

  protected PersistentAnnotationService getMongoPersistence() {
    return mongoPersistance;
  }

  public void setMongoPersistance(PersistentAnnotationService mongoPersistance) {
    this.mongoPersistance = mongoPersistance;
  }

  public PersistentModerationRecordService getMongoModerationRecordPersistence() {
    return mongoModerationRecordPersistance;
  }

  public SolrAnnotationService getSolrService() {
    return solrAnnotationService;
  }

  public void setSolrService(SolrAnnotationService solrAnnotationService) {
    this.solrAnnotationService = solrAnnotationService;
  }

  public Annotation getAnnotationById(long annoIdentifier, String userId, boolean enabled)
      throws AnnotationNotFoundException, UserAuthorizationException {
    Annotation annotation = getMongoPersistence().getByIdentifier(annoIdentifier);
    if (annotation == null)
      throw new AnnotationNotFoundException(null, I18nConstantsAnnotation.ANNOTATION_NOT_FOUND,
          new String[] {String.valueOf(annoIdentifier)});

    try {
      // check privacy
      checkPrivacy(annotation, userId);
    } catch (AnnotationStateException e) {
      // TODO: either change method parameters to accept wsKey or return different exception
      throw new UserAuthorizationException(null, I18nConstantsAnnotation.USER_NOT_AUTHORIZED,
          new String[] {userId}, e);
    }

    if (enabled == true) {
      try {
        // check visibility
        checkVisibility(annotation);
      } catch (AnnotationStateException e) {
        throw new UserAuthorizationException(null, I18nConstantsAnnotation.ANNOTATION_NOT_ACCESSIBLE,
            new String[] {annotation.getStatus()}, HttpStatus.GONE, e);
      }
    }

    return annotation;
  }

  protected void checkVisibility(Annotation annotation) throws AnnotationStateException {
    // Annotation res = null;
    // res =
    // getMongoPersistence().find(annotation.getAnnotationId().getProvider(),
    // annotation.getAnnotationId().getIdentifier());
    if (annotation.isDisabled())
      throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
          AnnotationStates.DISABLED);
  }

  /*
   * (non-Javadoc)
   * 
   * @see eu.europeana.annotation.web.service.AnnotationService#checkVisibility(eu.
   * europeana.annotation.definitions.model.Annotation, java.lang.String)
   */
  protected void checkPrivacy(Annotation annotation, String user) throws AnnotationStateException {
    // TODO update when the authorization concept is specified
    if (annotation.isPrivate() && !annotation.getCreator().getHttpUrl().equals(user))
      throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
          AnnotationStates.PRIVATE);
  }

  // public void indexAnnotation(AnnotationId annoId) {
  // try {
  // Annotation res = getAnnotationById(annoId);
  // getSolrService().delete(res.getAnnotationId());
  // getSolrService().store(res);
  // } catch (Exception e) {
  // throw new RuntimeException(e);
  // }
  // }

  /**
   * Returns true by successful reindexing.
   * 
   * @param res
   * @return reindexing success status
   * @throws AnnotationIndexingException
   */
  protected boolean reindexAnnotation(Annotation res, Date lastIndexing)
      throws AnnotationIndexingException {

    if (!getConfiguration().isIndexingEnabled()) {
      getLogger().warn(
          "Annotation was not reindexed, indexing is disabled. See configuration properties!");
      return false;
    }

    try {
      Summary summary = getMongoModerationRecordPersistence()
          .getModerationSummaryByAnnotationId(res.getIdentifier());
      getSolrService().update(res, summary);
      updateLastIndexingTime(res, lastIndexing);

      return true;
    } catch (Exception e) {
      throw new AnnotationIndexingException(
          "Cannot reindex annotation with the identifier: " + res.getIdentifier(), e);
    }
  }

  /**
   * Returns true by successful reindexing.
   * 
   * @param res
   * @return reindexing success status
   */
  public boolean reindexAnnotationById(long annoIdentifier, Date lastIndexing) {
    boolean success = false;
    try {
      // Annotation res = getAnnotationById(annoId);
      Annotation annotation = getMongoPersistence().getByIdentifier(annoIdentifier);
      success = reindexAnnotation(annotation, lastIndexing);
    } catch (Exception e) {
      getLogger().error(e.getMessage(), e);
      return false;
    }
    return success;
  }

  protected void updateLastIndexingTime(Annotation res, Date lastIndexing) {
    try {
      getMongoPersistence().updateIndexingTime(res, lastIndexing);
      ((PersistentAnnotation) res).setLastIndexed(lastIndexing);
    } catch (Exception e) {
      getLogger()
          .warn("The time of the last SOLR indexing could not be saved in the Mongo database. ", e);
    }
  }

  public Logger getLogger() {
    return logger;
  }

  public PersistentAnnotationService getMongoPersistance() {
    return mongoPersistance;
  }

  protected Annotation updateAndReindex(PersistentAnnotation persistentAnnotation) {
    Annotation res = getMongoPersistence().update(persistentAnnotation);

    // reindex annotation
    try {
      reindexAnnotation(res, res.getLastUpdate());
    } catch (AnnotationIndexingException e) {
      getLogger().warn("The annotation with the identifier: " + persistentAnnotation.getIdentifier()
          + " could not be reindexed successfully.", e);
    }
    return res;
  }

  boolean isSemanticTag(Annotation annotation) {
    return MotivationTypes.TAGGING.equals(annotation.getMotivationType()) && hasBodyUrl(annotation);
  }

  boolean hasBodyUrl(Annotation annotation) {
    return GeneralUtils.isUrl(annotation.getBody().getValue());
  }

  boolean isDereferenceProfile(SearchProfiles searchProfile) {
    return SearchProfiles.DEREFERENCE.equals(searchProfile);
  }

}
