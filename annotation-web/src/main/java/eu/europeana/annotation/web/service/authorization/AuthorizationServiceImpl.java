package eu.europeana.annotation.web.service.authorization;

import javax.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.annotation.mongo.service.PersistentApiWriteLockService;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.model.vocabulary.UserRoles;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.definitions.vocabulary.Role;
import eu.europeana.api.commons.service.authorization.BaseAuthorizationService;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;

public class AuthorizationServiceImpl extends BaseAuthorizationService implements AuthorizationService {

    @Resource
    AnnotationConfiguration configuration;

    @Resource(name = "commons_oauth2_europeanaClientDetailsService")
    ClientDetailsService clientDetailsService;

    public AuthorizationServiceImpl() {

    }

    @Resource(name = "annotation_db_apilockService")
    private PersistentApiWriteLockService apiWriteLockService;

    public PersistentApiWriteLockService getPersistentIndexingJobService() {
	return apiWriteLockService;
    }

    public void setPersistentIndexingJobService(PersistentApiWriteLockService apiWriteLockService) {
	this.apiWriteLockService = apiWriteLockService;
    }


    /**
     * Check if a write lock is in effect. Returns HttpStatus.LOCKED for change
     * operations in case the write lock is active.
     * 
     * @param userToken
     * @param operationName
     * @throws UserAuthorizationException
     */
    public void checkWriteLockInEffect(String operationName) throws ApplicationAuthenticationException {
	PersistentApiWriteLock runningJob;
	try {
	    runningJob = getPersistentIndexingJobService().getLastActiveLock("lockWriteOperations");
	    // refuse operation if a write lock is effective (allow only unlock and retrieve
	    // operations)
	    if (!(operationName.equals(Operations.ADMIN_UNLOCK) || operationName.equals(Operations.ADMIN_REINDEX)
		    || operationName.endsWith(Operations.RETRIEVE)) && runningJob != null
		    && runningJob.getName().equals("lockWriteOperations") && runningJob.getEnded() == null) {
		throw new ApplicationAuthenticationException(I18nConstants.LOCKED_MAINTENANCE, I18nConstants.LOCKED_MAINTENANCE, null, HttpStatus.LOCKED, null);
	    }
	} catch (ApiWriteLockException e) {
	    throw new ApplicationAuthenticationException(I18nConstants.LOCKED_MAINTENANCE, I18nConstants.LOCKED_MAINTENANCE, null,
		    HttpStatus.LOCKED, e);
	}
    }

    public AnnotationConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(AnnotationConfiguration configuration) {
	this.configuration = configuration;
    }

//    @Override
    protected String getAuthorizationApiName() {
	return getConfiguration().getAuthorizationApiName();
    }

    @Override
    protected ClientDetailsService getClientDetailsService() {
	return clientDetailsService;
    }

    @Override
    protected String getSignatureKey() {
	return getConfiguration().getJwtTokenSignatureKey();
    }

    @Override
    protected Role getRoleByName(String name) {
	return UserRoles.getRoleByName(name);
    }

    @Override
    protected String getApiName() {
	return getConfiguration().getAuthorizationApiName();
    }

}
