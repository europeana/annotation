package eu.europeana.annotation.web.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
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
		BatchProcessingStatus status = new BatchProcessingStatus();
		List<String> res = getMongoPersistence().filterByTimestamp(startTimestamp, endTimestamp);
		Iterator<String> iter = res.iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			try {
				Annotation annotation = getMongoPersistence().findByID(id);
				if (annotation == null)
					throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, id);
				boolean success = reindexAnnotationById(annotation.getAnnotationId(), new Date());
				if (success) 
					status.incrementSuccessCount();
				else 
					status.incrementFailureCount();
			} catch (IllegalArgumentException iae) {
				String msg = "id: " + id + ". " + iae.getMessage();
				Logger.getLogger(getClass().getName()).error(msg);
				// throw new RuntimeException(iae);
				status.incrementFailureCount();
			} catch (Exception e) {
				Calendar start = new GregorianCalendar();
				start.setTimeInMillis(Long.parseLong(startTimestamp));
				
				Calendar end = new GregorianCalendar();
				start.setTimeInMillis(Long.parseLong(endTimestamp));
				
				
				String msg = "Date error by reindexing of annotation set." + " startTimestamp: " + startTimestamp +  "( " + start +")" 
						+ ", endTimestamp: " + endTimestamp + "(" + end + "). "
						+ e.getMessage();
				Logger.getLogger(getClass().getName()).error(msg);
				status.incrementFailureCount();
				// throw new RuntimeException(e);
			}
		}
		return status;
	}

	@Override
	public BatchProcessingStatus reindexAnnotationSet(List<String> uriList) {
		
		BatchProcessingStatus status = new BatchProcessingStatus();
		Iterator<String> iter = uriList.iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			try {
				Annotation annotation = getMongoPersistence().findByID(id);
				if (annotation == null)
					throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, id);
				boolean success = reindexAnnotationById(annotation.getAnnotationId(), new Date());
				if (success)
					status.incrementSuccessCount();
				else 
					status.incrementFailureCount();
			} catch (IllegalArgumentException iae) {
				String msg = "id: " + id + ". " + iae.getMessage();
				Logger.getLogger(getClass().getName()).error(msg);
				// throw new RuntimeException(iae);
				status.incrementFailureCount();
			} catch (Exception e) {
				String msg = "Cannot (re)index annotation with id: "+ id + "\n "
						+ e.getMessage();
				Logger.getLogger(getClass().getName()).error(msg);
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
}
