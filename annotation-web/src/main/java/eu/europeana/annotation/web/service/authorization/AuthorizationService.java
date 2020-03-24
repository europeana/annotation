package eu.europeana.annotation.web.service.authorization;

import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;

public interface AuthorizationService extends eu.europeana.api.commons.service.authorization.AuthorizationService {

    void checkWriteLockInEffect(String operationName) throws ApplicationAuthenticationException;

}
