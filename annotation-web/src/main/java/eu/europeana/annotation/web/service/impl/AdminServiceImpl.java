package eu.europeana.annotation.web.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.annotation.web.service.AdminService;

public class AdminServiceImpl extends BaseAnnotationServiceImpl implements AdminService {

	public BatchProcessingStatus deleteAnnotationSet(List<String> uriList) {
		AnnotationId annoId;
		BatchProcessingStatus status = new BatchProcessingStatus();

		for (String annoUri : uriList) {
			annoId = JsonUtils.getIdHelper().parseAnnotationId(annoUri, true);
			try {
				deleteAnnotation(annoId);
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
	public void deleteAnnotation(AnnotationId annoId) throws InternalServerException, AnnotationServiceException {

		// mongo is the master
		try {
			getMongoPersistence().remove(annoId);
		} catch (AnnotationMongoException e) {
			if (StringUtils.startsWith(e.getMessage(), AnnotationMongoException.NO_RECORD_FOUND)) {
				// consider annotation deleted in mongo and try to remove the
				// related items
				getLogger().warn("The annotation with the given Id doesn't exist anymore: " + annoId.toHttpUrl());
			} else {
				// do not remove anything if the master object cannt be deleted
				throw new AnnotationServiceException("Cannot delete annotation from storragee. " + annoId.toHttpUrl(),
						e);
			}
		} catch (Throwable th) {
			throw new InternalServerException(th);
		}

		// delete moderation record if possible
		try {
			getMongoModerationRecordPersistence().remove(annoId);
		} catch (Throwable th) {
			// expected ModerationMongoException
			getLogger().warn("Cannot remove moderation record for annotation id: " + annoId.toHttpUrl());
		}

		// delete from solr .. and throw AnnotationServiceException if operation
		// is not successfull
		getSolrService().delete(annoId);
	}

	public BatchProcessingStatus reindexAnnotationSelection(String startDate, String endDate, String startTimestamp,
			String endTimestamp) {
		
//		int successCount = 0;
//		int failureCount = 0;
		if (!Strings.isNullOrEmpty(startDate)) {
			startTimestamp = TypeUtils.getUnixDateStringFromDate(startDate);
		}

		if (!Strings.isNullOrEmpty(endDate)) {
			endTimestamp = TypeUtils.getUnixDateStringFromDate(endDate);
		}

		return reindexAnnotationSelection(startTimestamp, endTimestamp);
	}

	protected BatchProcessingStatus reindexAnnotationSelection(String startTimestamp, String endTimestamp) {
		List<String> res = getMongoPersistence().filterByLastUpdateTimestamp(startTimestamp, endTimestamp);
		return reindexAnnotationSet(res, true);
	}

	@Override
	//TODO:reimplement using database cursors for higher scalability
	public BatchProcessingStatus reindexAnnotationSet(List<String> ids, boolean isObjectId) {
		
		BatchProcessingStatus status = new BatchProcessingStatus();
		AnnotationId annoId = null;
		Annotation annotation;
		int count = 0;
		for (String id : ids) {
			try {
				count++;
				if(count % 1000 == 0)
					getLogger().info("Processing object: " + count);
				//check
				if(isObjectId){
					annotation = getMongoPersistence().findByID(id);
				}else{
					annoId = JsonUtils.getIdHelper().parseAnnotationId(id, true);
					annotation = getMongoPersistence().find(annoId);
				}
				
				if (annotation == null)
					throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, id);
				boolean success = reindexAnnotationById(annotation.getAnnotationId(), new Date());
				if (success) 
					status.incrementSuccessCount();
				else 
					status.incrementFailureCount();			
			} catch (IllegalArgumentException iae) {
				String msg = "id: " + id + ". " + iae.getMessage();
				getLogger().error(msg);
				// throw new RuntimeException(iae);
				status.incrementFailureCount();
			} catch (Exception e) {
				String msg = "Error when reindexing annotation: "+ annoId
						+ e.getMessage();
				getLogger().error(msg);
				status.incrementFailureCount();
				// throw new RuntimeException(e);
			}
		}
		return status;
	}

	@Override
	public BatchProcessingStatus reindexAll() {
		return reindexAnnotationSelection( "0", ""+System.currentTimeMillis());
	}

	@Override
	public BatchProcessingStatus reindexOutdated() {
		List<String> res = getMongoPersistence().filterByLastUpdateGreaterThanLastIndexTimestamp();
		
		BatchProcessingStatus status = new BatchProcessingStatus();
		status.incrementSuccessCount();
		//return status;
		
		return reindexAnnotationSet(res, true);
	}
	
	//TODO #552
	@Override
	public BatchProcessingStatus updateRecordId(String oldId, String newId) {
		BatchProcessingStatus status = new BatchProcessingStatus();
		
		// find annotation by oldId
		List<? extends Annotation> annotations = getMongoPersistence().getAnnotationListByResourceId(oldId);
		System.out.println(annotations.size());
		for (Annotation anno : annotations) {
			if (!anno.isDisabled()) {
//				System.out.println(anno.getAnnotationId());
				Target annoTarget = anno.getTarget();
				
				// update resourceId
				annoTarget.setResourceId(newId);
				
				//update fields: httpUri, value, inputString
				if (annoTarget.getHttpUri() != null) {
					String newHttpUri = annoTarget.getHttpUri().replace(oldId, newId);
					annoTarget.setHttpUri(newHttpUri);
				}
				
				if (annoTarget.getValue() != null) {
					String newValue = annoTarget.getValue().replace(oldId, newId);
					annoTarget.setValue(newValue);
				}
				
				String newInputString = annoTarget.getInputString().replace(oldId, newId);
				annoTarget.setInputString(newInputString);
				
				// replace target
				anno.setTarget(annoTarget);
				
				// update mongo
				try {
					getMongoPersistence().update((PersistentAnnotation) anno);	
					status.incrementSuccessCount();			
				} catch (Exception e) {
					status.incrementFailureCount();
//					throw e;
				}
			}
		}
		
		return status;
	}
}
