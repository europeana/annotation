package eu.europeana.annotation.web.service.authorization;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;

public interface AuthorizationService extends eu.europeana.api.commons.service.authorization.AuthorizationService {

	Agent authorizeUser(String userToken, String apiKey, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException;

	Agent authorizeUser(String userToken, String apiKey, AnnotationId annoId, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException;

}
