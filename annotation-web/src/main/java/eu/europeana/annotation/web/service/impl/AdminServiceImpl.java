package eu.europeana.annotation.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentApiWriteLockService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.IndexingJobLockedException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.model.BatchProcessingStatus;
import eu.europeana.annotation.web.model.vocabulary.Actions;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.apikey.client.Connector;
import eu.europeana.apikey.client.ValidationRequest;
import eu.europeana.apikey.client.ValidationResult;
import eu.europeana.apikey.client.exception.ApiKeyValidationException;

public class AdminServiceImpl extends BaseAnnotationServiceImpl implements AdminService {

	@Resource(name = "annotation_db_apilockService")
	PersistentApiWriteLockService apiWriteLockService;
	
	private final String APP_CONFIG_SELF = "appConfigSelf";

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
		List<String> res = getMongoPersistence().filterByLastUpdateTimestamp(startTimestamp, endTimestamp);
		try {
			return reindexAnnotationSet(res, true, action);
		} catch (ApiWriteLockException e) {
			throw new InternalServerException("Cannot reindex annotation selection", e);
		}
	}

  @Override
  public BatchProcessingStatus reindexAnnotationSet(List<String> ids, boolean isObjectId, String action)
			throws HttpException, ApiWriteLockException {

		if (apiWriteLockService.getLastActiveLock("reindex") != null)
			throw new IndexingJobLockedException(action);
		
		PersistentApiWriteLock pij = null;
		try {
			pij = apiWriteLockService.lock(action);

			synchronized (this) {
				BatchProcessingStatus status = new BatchProcessingStatus();
				AnnotationId annoId = null;
				Annotation annotation;
				int count = 0;

				for (String id : ids) {
					try {
						count++;
						if (count % 1000 == 0)
							getLogger().info("Processing object: " + count);
						// check
						if (isObjectId) {
							annotation = getMongoPersistence().findByID(id);
						} else {
							annoId = JsonUtils.getIdHelper().parseAnnotationId(id, true);
							annotation = getMongoPersistence().find(annoId);
						}
						if (annotation == null)
							throw new AnnotationNotFoundException(null, I18nConstants.ANNOTATION_NOT_FOUND, new String[]{id});
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
					} catch (Throwable e) {
						String msg = "Error when reindexing annotation: " + annoId + e.getMessage();
						getLogger().error(msg);
						status.incrementFailureCount();
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

		List<String> res = getMongoPersistence().filterByLastUpdateGreaterThanLastIndexTimestamp();
		return reindexAnnotationSet(res, true, Actions.REINDEX_OUTDATED);
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
					PersistentAnnotation storedAnno = (PersistentAnnotation) updateAndReindex(
							(PersistentAnnotation) anno);

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
	
	
    /**
     * This method employs API key client library for API key validation
     * @param request The validation request containing API key e.g. ApiKey1
     * @param method The method e.g. read, write, delete...
     * @return true if API key is valid
     */
    public boolean validateApiKey(ValidationRequest request, String method) {
    	
    	boolean res = false;
    	
    	String SUCCESS = "204";
    	
        if (StringUtils.isNotBlank(method)) request.setMethod(method);
        Connector connector;
        try {
            connector = new Connector();
            ValidationResult result = connector.validateApiKey(request);
            if (result.hasConnected()){
                if (StringUtils.isBlank(getConfiguration().getValidationApi())) {
                	logger.debug("Apikeyservice api not provided");
                }
                if (StringUtils.isBlank(method)) {
                	logger.debug("Apikeyservice method not provided");
                }
                if (result.isPageNotFound_404()) {
                	logger.debug("Error: Apikeyservice not found on server");
                }
            } else {
            	logger.debug("Error: could not connect to ApiKey Service");
            }
            if (result.getReturnStatus().equals(SUCCESS))
            	res = true;
            
        } catch (ApiKeyValidationException e) {
        	logger.error(e.getMessage());
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        
        return res;
    }
	
	/**
	 * @param appKey
	 * @param appConfigJson
	 * @return
	 * @throws AnnotationMongoException 
	 */
	public Client updateAuthenticationConfig(String appKey, String appConfigJson) throws AnnotationMongoException {
		Client storedClient = getClientApplicationByApiKey(appKey);
		Client clientApp = updateClientConfig(storedClient, appConfigJson);
		return clientApp;
	}

	public Client getClientApplicationByApiKey(String appKey) {
		PersistentClient storedClient = (PersistentClient) clientService.findByApiKey(appKey);
		if(storedClient == null)
			throw new AnnotationMongoRuntimeException("No Client Application found with the (API) Key: " + appKey);
		return storedClient;
	}

	private Client updateClientConfig(Client storedClient, String appConfigJson) throws AnnotationMongoException {
			
			String newConfig = appConfigJson;
			//support from migration of app config
			if(APP_CONFIG_SELF.equals(appConfigJson))
				newConfig = storedClient.getAuthenticationConfigJson();
			
			Application clientApplication = authenticationService.parseApplication(newConfig);
					
			// put application in the client
			storedClient.setClientApplication(clientApplication);

			// write to MongoDB
			Client clientApp = clientService.update((PersistentClient) storedClient);
			return clientApp;
	}

	@Override
	public Client migrateAuthenticationConfig(String appKey) throws AnnotationMongoException, OperationAuthorizationException {
		Client storedClient = getClientApplicationByApiKey(appKey);
		if(storedClient.getClientApplication() != null)
			throw new OperationAuthorizationException(I18nConstants.OPERATION_EXECUTION_NOT_ALLOWED, I18nConstants.OPERATION_EXECUTION_NOT_ALLOWED, 
					new String[]{"The configuration of the client application was already migrated!"});
			
		Client clientApp = updateClientConfig(storedClient, APP_CONFIG_SELF);
		return clientApp;
	}

}
  
