package eu.europeana.annotation.web.service.authorization;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.model.vocabulary.UserGroups;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;

public class AuthorizationServiceImpl implements AuthorizationService {

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
	
	@Override
	@Deprecated
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
		Application app = getAuthenticationService().getByApiKey(apiKey);
		Agent user = getAuthenticationService().getUserByToken(apiKey, userToken);
		
		if (user== null || user.getName() == null || user.getUserGroup() == null)
			throw new UserAuthorizationException("Invalid User (Token): ", userToken, HttpStatus.FORBIDDEN);
		
		if(!isAdmin(user) && !hasPermission(app, annoId, operationName))
			throw new OperationAuthorizationException(OperationAuthorizationException.MESSAGE_CLIENT_NOT_AUTHORIZED, 
					"client app provider: " + app.getProvider() + "; annotation id: "+ annoId, HttpStatus.FORBIDDEN);
				
		//check permissions
		//TODO: isAdmin check is not needed anymore after the implementation of permissions based on user groups
		if(isAdmin(user) && hasPermission(user, operationName))//allow all
			return user;
		else if(isTester(user) && configuration.isProductionEnvironment()){
			//#20 testers not allowed in production environment
			throw new UserAuthorizationException("Test users are not authorized to perform operations in production environments", user.getName(), HttpStatus.FORBIDDEN);
		} else	if(hasPermission(user, operationName)){
			//user is authorized
			return user;
		}

		//user is not authorized to perform operation
		throw new UserAuthorizationException("User not authorized to perform this operation: ", user.getName(), HttpStatus.FORBIDDEN);			
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
