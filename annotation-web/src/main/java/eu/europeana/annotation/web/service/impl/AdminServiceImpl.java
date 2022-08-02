package eu.europeana.annotation.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentApiWriteLockImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentApiWriteLockService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.web.exception.IndexingJobLockedException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.annotation.web.model.vocabulary.Actions;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;

@Service
public class AdminServiceImpl extends BaseAnnotationServiceImpl implements AdminService {

	@Resource(name = "annotation_db_apilockService")
	PersistentApiWriteLockService apiWriteLockService;
	
	public BatchProcessingStatus deleteAnnotationSet(List<Long> identifiers) {
		BatchProcessingStatus status = new BatchProcessingStatus();
		for (Long identifier : identifiers) {
			try {
				deleteAnnotation(identifier);
				status.incrementSuccessCount();
			} catch (Throwable th) {
				getLogger().info(th);
				status.incrementFailureCount();
			}
		}
		return status;
	}

	@Override
	// public void deleteAnnotation(String resourceId, String provider, Long
	// annotationNr) {
	public void deleteAnnotation(long annoIdentifier) throws InternalServerException, AnnotationServiceException {

  	    // delete from solr first, as mongo deletions will not be recovered
        getSolrService().delete(annoIdentifier);
        // delete moderation record if exists
        try {
            getMongoModerationRecordPersistence().remove(annoIdentifier);
        } catch (Throwable th) {
            // expected ModerationMongoException
            getLogger().warn("Cannot remove moderation record for annotation id: " + annoIdentifier);
        }
        
	    // mongo is the master, finally delete the annotation
		try {
			getMongoPersistence().remove(annoIdentifier);
		} catch (AnnotationMongoException e) {
			if (StringUtils.startsWith(e.getMessage(), AnnotationMongoException.NO_RECORD_FOUND)) {
				// consider annotation deleted in mongo and try to remove the
				// related items
				getLogger().warn("The annotation with the given Id doesn't exist anymore: " + annoIdentifier);
			} else {
				// do not remove anything if the master object cannt be deleted
				throw new AnnotationServiceException("Cannot delete annotation from storage. " + annoIdentifier, e);
			}
		} catch (Throwable th) {
			throw new InternalServerException(th);
		}


	}
	
	public BatchProcessingStatus reindexAnnotationSelection(String startDate, String endDate, String startTimestamp,
			String endTimestamp, String action) throws HttpException, ApiWriteLockException {

		// int successCount = 0;
		// int failureCount = 0;
		if (!Strings.isNullOrEmpty(startDate)) {
			startTimestamp = TypeUtils.getUnixDateStringFromDate(startDate);
		}

		if (!Strings.isNullOrEmpty(endDate)) {
			endTimestamp = TypeUtils.getUnixDateStringFromDate(endDate);
		}

		return reindexAnnotationSelection(startTimestamp, endTimestamp, action);
	}

	protected BatchProcessingStatus reindexAnnotationSelection(String startTimestamp, String endTimestamp, String action)
			throws HttpException {
		List<Long> res = getMongoPersistence().filterByLastUpdateTimestamp(startTimestamp, endTimestamp);
		try {
			return reindexAnnotationSet(res, action);
		} catch (ApiWriteLockException e) {
			throw new InternalServerException("Cannot reindex annotation selection", e);
		}
	}

  @Override
  public BatchProcessingStatus reindexAnnotationSet(List<Long> identifiers, String action)
			throws HttpException, ApiWriteLockException {

		if (apiWriteLockService.getLastActiveLock("reindex") != null)
			throw new IndexingJobLockedException(action);
		
		PersistentApiWriteLockImpl pij = null;
		try {
			pij = apiWriteLockService.lock(action);

			synchronized (this) {
				BatchProcessingStatus status = new BatchProcessingStatus();
				Annotation annotation;
				int count = 0;

				for (Long id : identifiers) {
					try {
						count++;
						if (count % 1000 == 0)
							getLogger().info("Processing object: {}", count);

						annotation = getMongoPersistence().getByIdentifier(id);

						if (annotation == null)
							throw new AnnotationNotFoundException(null, I18nConstants.ANNOTATION_NOT_FOUND, new String[]{String.valueOf(id)});
						boolean success = reindexAnnotationById(id, new Date());
						if (success)
							status.incrementSuccessCount();
						else {
							status.incrementFailureCount();
							status.addError(String.valueOf(id), "see error log");
						}
					} catch (IllegalArgumentException iae) {
						String msg = "id: " + id + ". " + iae.getMessage();
						getLogger().error(msg);
						// throw new RuntimeException(iae);
						status.incrementFailureCount();
						status.addError(String.valueOf(id), msg);
					} catch (Throwable e) {
						String msg = "Error when reindexing annotation: " + id + e.getMessage();
						getLogger().error(msg);
						status.incrementFailureCount();
						status.addError(String.valueOf(id), msg);
						// throw new RuntimeException(e);
					}
				}
				return status;
			}
		}finally{
			//unlock the index
			apiWriteLockService.unlock(pij);
			
		} 

	}

	@Override
	public BatchProcessingStatus reindexAll() throws HttpException, ApiWriteLockException {
		return reindexAnnotationSelection("0", "" + System.currentTimeMillis(), Actions.REINDEX_ALL);
	}

	@Override
	public BatchProcessingStatus reindexOutdated() throws HttpException, ApiWriteLockException {

		List<Long> res = getMongoPersistence().filterByLastUpdateGreaterThanLastIndexTimestamp();
		return reindexAnnotationSet(res, Actions.REINDEX_OUTDATED);
	}

	@Override
	public BatchProcessingStatus updateRecordId(String oldId, String newId) {
		BatchProcessingStatus status = new BatchProcessingStatus();

		// find annotation by oldId
		List<? extends Annotation> annotations = getMongoPersistence().getAnnotationListByResourceId(oldId);

		for (Annotation anno : annotations) {
			if (!anno.isDisabled()) {
				Target annoTarget = anno.getTarget();

				// update resourceIds
				if (annoTarget.getResourceIds() != null) {
					List<String> currentResourceIds = annoTarget.getResourceIds();
					List<String> updatedResourceIds = new ArrayList<String>();
					for (String id : currentResourceIds) {
						if (id.equals(oldId)) {
							String updatedId = newId;
							updatedResourceIds.add(updatedId);
						} else {
							updatedResourceIds.add(id);
						}
					}
					annoTarget.setResourceIds(updatedResourceIds);
				}

				// update fields: httpUri, value(s) + inputString
				if (annoTarget.getHttpUri() != null) {
					String newHttpUri = annoTarget.getHttpUri().replace(oldId, newId);
					annoTarget.setHttpUri(newHttpUri);
				}

				if (annoTarget.getValue() != null) {
					String newValue = annoTarget.getValue().replace(oldId, newId);
					annoTarget.setValue(newValue);
					// inputString depends on value(s)
					annoTarget.setInputString(newValue);
				}

				// change "value" and "values" fields, so we only have one of
				// them
				if (annoTarget.getValues() != null && !annoTarget.getValues().isEmpty()) {
					List<String> currentValues = annoTarget.getValues();
					List<String> updatedValues = new ArrayList<String>();
					for (String value : currentValues) {
						String updatedValue = value.replace(oldId, newId);
						updatedValues.add(updatedValue);
					}
					annoTarget.setValues(updatedValues);
					// inputString depends on value(s)
					annoTarget.setInputString(updatedValues.toString());
				}

				// replace target
				anno.setTarget(annoTarget);

				// update mongo
				try {
					PersistentAnnotationImpl storedAnno = (PersistentAnnotationImpl) updateAndReindex(
							(PersistentAnnotationImpl) anno);

					// if not re-indexed or not indexed at all
					if (isIndexInSync(storedAnno))
						status.incrementSuccessCount();
					else
						status.incrementIndexingFailureCount();
				} catch (Exception e) {
					status.incrementFailureCount();
					throw e;
				}
			}
		}

		return status;
	}

	protected boolean isIndexInSync(PersistentAnnotation storedAnno) {
		return storedAnno.getLastIndexed() != null && (!storedAnno.getLastIndexed().before(storedAnno.getLastUpdate()));
	}

	@Override
	protected boolean validateResource(String value) {
	    // TODO Auto-generated method stub
	    return false;
	}
   	
}
  
