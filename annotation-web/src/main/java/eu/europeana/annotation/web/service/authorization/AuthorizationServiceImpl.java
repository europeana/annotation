package eu.europeana.annotation.web.service.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.mongo.exception.ApiWriteLockException;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.annotation.mongo.service.PersistentApiWriteLockService;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.model.vocabulary.UserGroups;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.definitions.vocabulary.Role;
import eu.europeana.api.commons.service.authorization.BaseAuthorizationService;

public class AuthorizationServiceImpl extends BaseAuthorizationService implements AuthorizationService {
	
	protected final Logger logger = LogManager.getLogger(getClass());

	@Resource
	AuthenticationService authenticationService;
	
	@Resource
	AnnotationConfiguration configuration;

    @Resource(name = "commons_oauth2_europeanaClientDetailsService")
    ClientDetailsService clientDetailsService;
    
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
	
//	@Override
	/**
	 * use authorizeUser(String userToken, String apiKey, String operationName) instead
	 * @param userToken
	 * @param authentication
	 * @param annoId
	 * @param operationName
	 * @throws UserAuthorizationException
	 */
//	public void authorizeUser(String userId, Authentication authentication, AnnotationId annoId, String operationName)
//			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException {
//		// throws exception if user is not found
//		//TODO: add userToken to agent
//		
//		checkWriteLockInEffect(userId, operationName);
//		
//		
//		if (userId == null)
//			throw new UserAuthorizationException("Invalid User (Token)", I18nConstants.INVALID_TOKEN, new String[]{userId}, HttpStatus.FORBIDDEN);
//		
//		if(!isAdmin(authentication) && !hasPermission(authentication, operationName))
//			throw new OperationAuthorizationException(null, I18nConstants.CLIENT_NOT_AUTHORIZED, 
//					new String[]{"client apikey: " + authentication.getDetails() + "; annotation id: "+ annoId},
//					HttpStatus.FORBIDDEN);
//				
//		//check permissions
//		if(isTester(authentication) && configuration.isProductionEnvironment()){
//			//#20 testers not allowed in production environment
//			throw new UserAuthorizationException(null, I18nConstants.TEST_USER_FORBIDDEN, new String[]{authentication.getName()}, HttpStatus.FORBIDDEN);
//		}
//	}

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
			if(!(operationName.equals(Operations.ADMIN_UNLOCK) || operationName.equals(Operations.ADMIN_REINDEX) || operationName.endsWith(Operations.RETRIEVE)) 
					&& runningJob != null && runningJob.getName().equals("lockWriteOperations") && runningJob.getEnded() == null) {
				throw new UserAuthorizationException(null, I18nConstants.LOCKED_MAINTENANCE, null, HttpStatus.LOCKED);
			}
		} catch (ApiWriteLockException e) {
			throw new UserAuthorizationException(null, I18nConstants.AUTHENTICATION_FAIL, new String[]{userToken}, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method verifies user privileges for given operation 
	 * @param authentication
	 * @param operationName
	 * @return true if user has permission for given operation
	 */
	protected boolean hasPermission(Authentication authentication, String operationName) {
		List<String> roles = getRoles(authentication);
		Set<String> operations = UserGroups.getPermissionSet(roles);
		if (operations != null)
			return operations.contains(operationName);
//			return checkOperationAgainstPermissionSet(operationName,operations);
		
		if (roles.size() > 0) {
			for (String role : roles) {
				UserGroups userGroup = UserGroups.valueOf(role);			
				return checkOperationAgainstPermissionSet(operationName, userGroup.getOperations());
			}
		}
		
		return false;
	}

	/**
	 * This method validates operation against permission set
	 * @param operationName
	 * @param permissionSet
	 * @return true if operation supported
	 */
	private boolean checkOperationAgainstPermissionSet(String operationName, String[] permissionSet) {
		boolean res = false;
		for (String operation : permissionSet) {
			if(operation.equalsIgnoreCase(operationName))
				return true;//users is authorized, everything ok
		}
		return res;
	}

	/**
	 * This method validates if user has admin role
	 * @param authentication
	 * @return true if user has this role
	 */
	protected boolean isAdmin(Authentication authentication) {
		List<String> roles = getRoles(authentication);
		if (roles.size() > 0) {
			for (String role : roles) {
				if (UserGroups.admin.name().toLowerCase().equals(role)) {
					return true;
				}
			}
		}
		return false;
//		return UserGroups.ADMIN.name().equals(user.getUserGroup());
	}
	
	/**
	 * This method extracts roles from authentication object
	 * @param authentication
	 * @return a list of user roles
	 */
	protected List<String> getRoles(Authentication authentication) {
		List<String> roles = new ArrayList<String>();
		if (authentication.getAuthorities() != null && authentication.getAuthorities().size() > 0) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				String role = authority.getAuthority().toString();
				roles.add(role);
			}
		}
		return roles;
	}
	
	/**
	 * This method validates if user has tester role
	 * @param authentication
	 * @return true if user has this role
	 */
	protected boolean isTester(Authentication authentication) {
		List<String> roles = getRoles(authentication);
		if (roles.size() > 0) {
			for (String role : roles) {
				if (UserGroups.tester.name().equals(role)) {
					return true;
				}
			}
		}
		return false;
//		return UserGroups.TESTER.name().equals(user.getUserGroup());
	}
	
//	@Override
//	public void authorizeUser(String userToken, Authentication authentication, String operationName)
//			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException {
//		authorizeUser(userToken, authentication, null, operationName);
//	}

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
    	return UserGroups.getRoleByName(name);
    }

    @Override
    protected String getApiName() {
    	return getConfiguration().getAuthorizationApiName();
    }
	
}
