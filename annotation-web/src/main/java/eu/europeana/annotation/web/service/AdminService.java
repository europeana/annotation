package eu.europeana.annotation.web.service;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.web.exception.IndexingJobLockedException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.api.commons.web.exception.HttpException;
//import eu.europeana.apikey.client.ValidationRequest;

public interface AdminService {

	public String getComponentName();

	/**
	 * This method finds annotation object in database by annotation ID and
	 * reindexes it in Solr.
	 * 
	 * @param annoId
	 * @return success of reindexing operation
	 */
	public boolean reindexAnnotationById(AnnotationId annoId, Date lastIndexing);

	/**
	 * This method updates the client application identified by the given apiKey with the 
	 * Application configuration provided in JSON format
	 * @param appKey
	 * @param appConfigJson
	 * @return success of migration operation
	 * @throws AnnotationMongoException 
	 */
	public Client updateAuthenticationConfig(String appKey, String appConfigJson) throws AnnotationMongoException;
	
	/**
	 * This method migrates configuration of the client application from the outdated format using stored JSON, to the new one using 
	 * proper database representation (as Application Object)
	 *  
	 * @param appKey
	 * @param appConfigJson
	 * @return success of migration operation
	 * @throws AnnotationMongoException 
	 * @throws OperationAuthorizationException 
	 */
	public Client migrateAuthenticationConfig(String appKey) throws AnnotationMongoException, OperationAuthorizationException;
	
	/**
	 * This method retrieves the client application from the database
	 *  
	 * @param appKey
	 * @return the Client application from database
	 */
	public Client getClientApplicationByApiKey(String appKey);
	
	
	/**
	 * This method performs Solr reindexing for all annotation objects stored in
	 * database between start and end date or timestamp.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return status of reindexing
	 * @throws ApiWriteLockException 
	 * @throws IndexingJobLockedException 
	 * @throws HttpException 
	 */
	public BatchProcessingStatus reindexAnnotationSelection(String startDate, String endDate, String startTimestamp,
			String endTimestamp, String action) throws HttpException, ApiWriteLockException;

	
	/**
	 * This method deletes the list of annotations identified by the provided uris
	 * @param uriList
	 * @return
	 */
	public BatchProcessingStatus deleteAnnotationSet(List<String> uriList);
	
	/**
	 * This method deletes annotation by annotationId values.
	 * @param annoId
	 * @throws InternalServerException 
	 * @throws AnnotationServiceException 
	 */
	public void deleteAnnotation(AnnotationId annoId) throws InternalServerException, AnnotationServiceException;

	/*
	 * This methods returns annotation ids which where deleted after a given date 
	 * @param startDate
	 * @param startTimestamp
	 * @return deleted annotation ids
	 */
	public List<String> getDeletedAnnotationSet(String startDate, String startTimestamp);
	
	/**
	 * This methods reindexes the set of annotations identified by their uris or objectIds
	 * @param uriList
	 * @return
	 * @throws ApiWriteLockException 
	 * @throws IndexingJobLockedException 
	 * @throws HttpException 
	 */
	public BatchProcessingStatus reindexAnnotationSet(List<String> uriList, boolean isObjectId, String action) throws HttpException, ApiWriteLockException;

	/**
	 * this method is used to reindex all annotations available in the database 
	 * @return
	 * @throws ApiWriteLockException 
	 * @throws IndexingJobLockedException 
	 */
	public BatchProcessingStatus reindexAll() throws HttpException, ApiWriteLockException;

	/**
	 * @throws ApiWriteLockException 
	 * @throws HttpException 
	 * @throws IndexingJobLockedException 
	 * @throws ApiWriteLockException 
	 * this method is used to index new and reindex outdated annotations available in the database 
	 * @return
	 * @throws  
	 */
	public BatchProcessingStatus reindexOutdated() throws HttpException, ApiWriteLockException;

	/**
	 * this method is used to update record ids
	 * @return
	 */
	public BatchProcessingStatus updateRecordId(String oldId, String newId);
	
    /**
     * This method employs API key client library for API key validation
     * @param request The validation request containing API key e.g. ApiKey1
     * @param method The method e.g. read, write, delete...
     * @return true if API key is valid
     */
//	public boolean validateApiKey(ValidationRequest request, String method) throws ApplicationAuthenticationException;

}
