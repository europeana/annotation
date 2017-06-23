package eu.europeana.annotation.web.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.SolrTagService;
import eu.europeana.annotation.web.exception.AnnotationIndexingException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.api.common.config.I18nConstants;

public abstract class BaseAnnotationServiceImpl{

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	AuthenticationService authenticationService;
	
	@Resource
	SolrAnnotationService solrService;

	@Resource
	SolrTagService solrTagService;	

	@Resource
	PersistentAnnotationService mongoPersistance;

	@Resource
	PersistentModerationRecordService mongoModerationRecordPersistance;
	
	Logger logger = Logger.getLogger(getClass());


	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public String getComponentName() {
		return configuration.getComponentName();
	}

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
		return solrService;
	}

	public void setSolrService(SolrAnnotationService solrService) {
		this.solrService = solrService;
	}

	public SolrTagService getSolrTagService() {
		return solrTagService;
	}

	public void setSolrTagService(SolrTagService solrTagService) {
		this.solrTagService = solrTagService;
	}
	
	
	public Annotation getAnnotationById(AnnotationId annoId) throws AnnotationNotFoundException, UserAuthorizationException {
		Annotation annotation = getMongoPersistence().find(annoId);
		if(annotation == null)
			throw new AnnotationNotFoundException(null, I18nConstants.ANNOTATION_NOT_FOUND, new String[]{annoId.toHttpUrl()});
		
		String user = (String)null;
		try {
			// check visibility	
			checkVisibility(annotation, user);
		} catch (AnnotationStateException e) {
			if (annotation.isDisabled())
				throw new UserAuthorizationException(null, I18nConstants.ANNOTATION_NOT_ACCESSIBLE, 
						new String[]{annotation.getStatus()}, HttpStatus.GONE, e);
			else
				// TODO: either change method parameters to accept wsKey or return different exception
				throw new UserAuthorizationException(null, I18nConstants.USER_NOT_AUTHORIZED, new String[]{user}, e);
		}		
		
		return annotation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#checkVisibility(eu.
	 * europeana.annotation.definitions.model.Annotation, java.lang.String)
	 */
	public void checkVisibility(Annotation annotation, String user) throws AnnotationStateException {
		// Annotation res = null;
		// res =
		// getMongoPersistence().find(annotation.getAnnotationId().getProvider(),
		// annotation.getAnnotationId().getIdentifier());
		if (annotation.isDisabled())
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
					AnnotationStates.DISABLED);

		// if (annotation.
		// StringUtils.isNotEmpty(user) && !user.equals("null") &&
		// !res.getAnnotatedBy().getName().equals(user))
		// throw new AnnotationStateException("Given user (" + user + ") does
		// not match to the 'annotatedBy' user ("
		// + res.getAnnotatedBy().getName() + ").");
		// TODO update when the authorization concept is specified
		if (annotation.isPrivate() && !annotation.getCreator().getHttpUrl().equals(user))
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
					AnnotationStates.PRIVATE);

	}

//	public void indexAnnotation(AnnotationId annoId) {
//		try {
//			Annotation res = getAnnotationById(annoId);
//			getSolrService().delete(res.getAnnotationId());
//			getSolrService().store(res);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	/**
	 * Returns true by successful reindexing.
	 * @param res
	 * @return reindexing success status
	 * @throws AnnotationIndexingException 
	 */
	protected boolean reindexAnnotation(Annotation res, Date lastIndexing) throws AnnotationIndexingException {
		boolean success = false;
		
		if (!getConfiguration().isIndexingEnabled()){
			getLogger().warn("Annotation was not reindexed, indexing is disabled. See configuration properties!");
			return false;
		}
			
		try {
			Summary summary = getMongoModerationRecordPersistence().getModerationSummaryByAnnotationId(res.getAnnotationId());			
			getSolrService().update(res, summary);
			updateLastIndexingTime(res, lastIndexing);
			
			success = true;
		} catch (Exception e) {
//			Logger.getLogger(getClass().getName()).error("Error by reindexing of annotation: "
//					+ res.getAnnotationId().toString() + ". " + e.getMessage());
			throw new AnnotationIndexingException("cannot reindex annotation with ID: " + res.getAnnotationId(), e);
		}
		
		// check if the tag is already indexed
//		try {
//			getSolrTagService().update(res);
//		} catch (Exception e) {
//			Logger.getLogger(getClass().getName())
//					.warn("The annotation was updated correctly in the Mongo, but the Body tag was not updated yet. "
//							, e);
//		}

		// save the time of the last SOLR indexing
		return success;
	}

	

	/**
	 * Returns true by successful reindexing.
	 * @param res
	 * @return reindexing success status
	 */
	public boolean reindexAnnotationById(AnnotationId annoId, Date lastIndexing) {
		boolean success = false;
		try {
			Annotation res = getAnnotationById(annoId);
			success = reindexAnnotation(res, lastIndexing);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return success;
	}

	protected void updateLastIndexingTime(Annotation res, Date lastIndexing) {
		try {
			getMongoPersistence().updateIndexingTime(res.getAnnotationId(), lastIndexing);
			((PersistentAnnotation)res).setLastIndexed(lastIndexing);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName())
					.warn("The time of the last SOLR indexing could not be saved. " , e);
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public PersistentAnnotationService getMongoPersistance() {
		return mongoPersistance;
	}

	protected Annotation updateAndReindex(PersistentAnnotation persistentAnnotation) {
		Annotation res = getMongoPersistence().update(persistentAnnotation);
	
		//reindex annotation
		try {
			reindexAnnotation(res, res.getLastUpdate());	
		} catch (AnnotationIndexingException e) {
		   getLogger().warn("The annotation could not be reindexed successfully: " + persistentAnnotation.getAnnotationId(), e);		
		}
		return res;
	}
	
}
