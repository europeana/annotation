package eu.europeana.annotation.web.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.SolrTagService;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class BaseAnnotationServiceImpl {

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
	
	
	public Annotation getAnnotationById(AnnotationId annoId) throws AnnotationNotFoundException {
		Annotation annotation = getMongoPersistence().find(annoId);
		if(annotation == null)
			throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toHttpUrl());
		
		return annotation;
	}

	public void indexAnnotation(AnnotationId annoId) {
		try {
			Annotation res = getAnnotationById(annoId);
			getSolrService().delete(res.getAnnotationId());
			getSolrService().store(res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns true by successful reindexing.
	 * @param res
	 * @return reindexing success status
	 */
	protected boolean reindexAnnotation(Annotation res, Date lastIndexing) {
		boolean success = false;
		try {
			Summary summary = getMongoModerationRecordPersistence().getModerationSummaryByAnnotationId(res.getAnnotationId());			
			getSolrService().update(res, summary);
			updateLastIndexingTime(res, lastIndexing);
			
			success = true;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).error("Error by reindexing of annotation: "
					+ res.getAnnotationId().toString() + ". " + e.getMessage());
			throw new RuntimeException(e);
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
			Logger.getLogger(getClass().getName()).error(e.getMessage());
			throw new RuntimeException(e);
		}
		return success;
	}

	protected void updateLastIndexingTime(Annotation res, Date lastIndexing) {
		try {
			getMongoPersistence().updateIndexingTime(res.getAnnotationId(), lastIndexing);
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
	
}
