package eu.europeana.annotation.web.service.authorization;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.annotation.mongo.service.PersistentApiWriteLockService;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.model.vocabulary.UserGroups;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.api.common.config.I18nConstants;

public class AuthorizationServiceImpl implements AuthorizationService {
	
	protected final Logger logger = Logger.getLogger(getClass());

	@Resource
	AuthenticationService authenticationService;
	
	@Resource
	AnnotationConfiguration configuration;

	public AuthorizationServiceImpl(AuthenticationService authenticationService){
		this.authenticationService = authenticationService;
	}
	
	public AuthorizationServiceImpl(){
		
	}
	
	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	

	@Resource(name = "annotation_db_apilockService")
	private PersistentApiWriteLockService apiWriteLockService;
	

	public PersistentApiWriteLockService getPersistentIndexingJobService() {
		return apiWriteLockService;
	}

	public void setPersistentIndexingJobService(PersistentApiWriteLockService apiWriteLockService) {
		this.apiWriteLockService = apiWriteLockService;
	}
	
	@Override
	/**
	 * use authorizeUser(String userToken, String apiKey, String operationName) instead
	 * @param userToken
	 * @param apiKey
	 * @param annoId
	 * @param operationName
	 * @return
	 * @throws UserAuthorizationException
	 */
	public Agent authorizeUser(String userToken, String apiKey, AnnotationId annoId, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException {
		// throws exception if user is not found
		//TODO: add userToken to agent
		
		checkWriteLockInEffect(userToken, operationName);
		
		
		Application app = getAuthenticationService().getByApiKey(apiKey);
		Agent user = getAuthenticationService().getUserByToken(apiKey, userToken);
		
		if (user== null || user.getName() == null || user.getUserGroup() == null)
			throw new UserAuthorizationException("Invalid User (Token)", I18nConstants.INVALID_TOKEN, new String[]{userToken}, HttpStatus.FORBIDDEN);
		
		if(!isAdmin(user) && !hasPermission(app, annoId, operationName))
			throw new OperationAuthorizationException(null, I18nConstants.CLIENT_NOT_AUTHORIZED, 
					new String[]{"client app provider: " + app.getProvider() + "; annotation id: "+ annoId},
					HttpStatus.FORBIDDEN);
				
		//check permissions
		//TODO: isAdmin check is not needed anymore after the implementation of permissions based on user groups
		if(isAdmin(user) && hasPermission(user, operationName))//allow all
			return user;
		else if(isTester(user) && configuration.isProductionEnvironment()){
			//#20 testers not allowed in production environment
			throw new UserAuthorizationException(null, I18nConstants.TEST_USER_FORBIDDEN, new String[]{user.getName()}, HttpStatus.FORBIDDEN);
		} else	if(hasPermission(user, operationName)){
			//user is authorized
			return user;
		}

		//user is not authorized to perform operation
		throw new UserAuthorizationException(null, I18nConstants.USER_NOT_AUTHORIZED, new String[]{user.getName()}, HttpStatus.FORBIDDEN);	
	}

	/**
	 * Check if a write lock is in effect. Returns HttpStatus.LOCKED for change operations in case the write lock is active.
	 * @param userToken
	 * @param operationName
	 * @throws UserAuthorizationException
	 */
	private void checkWriteLockInEffect(String userToken, String operationName) throws UserAuthorizationException {
		PersistentApiWriteLock runningJob;
		try {
			runningJob = getPersistentIndexingJobService().getLastActiveLock("lockWriteOperations");
			// refuse operation if a write lock is effective (allow only unlock and retrieve operations)
			if(!(operationName.equals(Operations.ADMIN_UNLOCK) || operationName.endsWith(Operations.RETRIEVE) ) 
					&& runningJob != null && runningJob.getName().equals("lockWriteOperations") && runningJob.getEnded() == null) {
				throw new UserAuthorizationException(null, I18nConstants.LOCKED_MAINTENANCE, null, HttpStatus.LOCKED);
			}
		} catch (ApiWriteLockException e) {
			throw new UserAuthorizationException(null, I18nConstants.AUTHENTICATION_FAIL, new String[]{userToken}, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//verify client app privileges 
	protected boolean hasPermission(Application app, AnnotationId annoId, String operationName) {
		if(Operations.MODERATION_ALL.equals(operationName) || Operations.RETRIEVE.equals(operationName) )
			return true;
		
		return annoId!= null && app.getProvider().equals(annoId.getProvider());
	}

	//verify user privileges
	protected boolean hasPermission(Agent user, String operationName) {
		UserGroups userGroup = UserGroups.valueOf(user.getUserGroup());
		
		for (String operation : userGroup.getOperations()) {
			if(operation.equalsIgnoreCase(operationName))
				return true;//users is authorized, everything ok
		}
		
		return false;
	}

	protected boolean isAdmin(Agent user) {
		return UserGroups.ADMIN.name().equals(user.getUserGroup());
	}
	
	protected boolean isTester(Agent user) {
		return UserGroups.TESTER.name().equals(user.getUserGroup());
	}
	
	@Override
	public Agent authorizeUser(String userToken, String apiKey, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException {
		return authorizeUser(userToken, apiKey, null, operationName);
	}

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}
}
