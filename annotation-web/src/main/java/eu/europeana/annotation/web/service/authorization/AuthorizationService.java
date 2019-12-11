package eu.europeana.annotation.web.service.authorization;

import org.springframework.security.core.Authentication;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;

public interface AuthorizationService extends eu.europeana.api.commons.service.authorization.AuthorizationService {

	void authorizeUser(String userToken, Authentication authentication, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException;

	void authorizeUser(String userToken, Authentication authentication, AnnotationId annoId, String operationName)
			throws UserAuthorizationException, ApplicationAuthenticationException, OperationAuthorizationException;

}
