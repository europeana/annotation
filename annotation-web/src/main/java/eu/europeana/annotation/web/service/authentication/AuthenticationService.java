package eu.europeana.annotation.web.service.authentication;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;

public interface AuthenticationService {

	public void loadApiKeys() throws ApplicationAuthenticationException;
	
	public Agent getUserByToken(String apiKey, String userToken) throws UserAuthorizationException;

	public Application getByApiKey(String apiKey) throws ApplicationAuthenticationException;

	public Application parseApplication(String jsonData);

	void loadApiKeysFromFiles() throws ApplicationAuthenticationException;
	
}
