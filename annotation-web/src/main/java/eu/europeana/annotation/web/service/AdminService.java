package eu.europeana.annotation.web.service;

import java.util.Date;
import java.util.List;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.web.exception.IndexingJobLockedException;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.api.commons.definitions.exception.ApiWriteLockException;
import eu.europeana.api.commons.web.exception.HttpException;
//import eu.europeana.apikey.client.ValidationRequest;
import eu.europeana.api.commons.web.exception.InternalServerException;

public interface AdminService {

	/**
	 * This method finds annotation object in database by annotation ID and
	 * reindexes it in Solr.
	 * 
	 * @param annoIdentifier
	 * @return success of reindexing operation
	 */
	public boolean reindexAnnotationById(long annoIdentifier, Date lastIndexing);

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
	 * This method deletes the list of annotations.
	 * @param uriList
	 * @return
	 */
	public BatchProcessingStatus deleteAnnotationSet(List<Long> uriList);
	
	/**
	 * This method deletes annotation by the identifier values.
	 * @param annoIdentifier
	 * @throws InternalServerException 
	 * @throws AnnotationServiceException 
	 */
	public void deleteAnnotation(long annoIdentifier) throws InternalServerException, AnnotationServiceException;

	
	/**
	 * This methods reindexes the set of annotations identified by their identifiers
	 * 
	 * @param identifiers
	 * @param action
	 * @return
	 * @throws HttpException
	 * @throws ApiWriteLockException
	 */
	public BatchProcessingStatus reindexAnnotationSet(List<Long> identifiers, String action) throws HttpException, ApiWriteLockException;

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
	
}
