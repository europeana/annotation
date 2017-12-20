package eu.europeana.annotation.web.service.authentication.mock;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.api.commons.oauth2.service.impl.BridgeAuthenticationManager;

/**
 * This class implements an adapter for the existing authorization service (MockAuthenticationServiceImpl), in order to implement the OAuth2 interfaces.
 * This service reuses the apiKey as the clientId, and the userToken as accessToken (and refreshToken)
 * @author GordeaS
 *
 */
public class BridgeAuthenticationManagerAdapter extends BridgeAuthenticationManager implements AuthenticationService, ClientDetailsService {

	MockAuthenticationServiceImpl mockOauthService;
	
	@Resource
	PersistentClientService clientService;
	
	@Resource
	AnnotationConfiguration configuration;
	
	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public PersistentClientService getClientService() {
		return clientService;
	}

	public void setClientService(PersistentClientService clientService) {
		this.clientService = clientService;
	}
	
	public BridgeAuthenticationManagerAdapter(){
		super();
		initService();
	}
	
	public void initService(){
		//initialize MockOautService
		//initialize tokeStoreService
		setTokenServices((MockAuthenticationServiceImpl)getMockOauthService());
	}
	
	@Override
	public void loadApiKeys() throws ApplicationAuthenticationException {
		
		getMockOauthService().loadApiKeys();
	}

	@Override
	public Agent getUserByToken(String apiKey, String userToken) throws UserAuthorizationException {
		return getMockOauthService().getUserByToken(apiKey, userToken);
	}

	@Override
	public Application getByApiKey(String apiKey) throws ApplicationAuthenticationException {
		return getMockOauthService().getByApiKey(apiKey);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		try {
			Application app = getMockOauthService().getByApiKey(clientId);
			return new ClientAppDetailsAdapter(app);
		} catch (ApplicationAuthenticationException e) {
			throw new ClientRegistrationException("Cannot load client details", e);
		}
	}

	public AuthenticationService getMockOauthService() {
		//avoid NPE at bean initialization
		if(mockOauthService == null){
			mockOauthService = new MockAuthenticationServiceImpl(getConfiguration(), getClientService());
		}
		
		//ensure proper initialization of mockOauthService
		if(mockOauthService.getConfiguration() == null)
			mockOauthService.setConfiguration(getConfiguration());
		
		if(mockOauthService.getClientService() == null)
			mockOauthService.setClientService(getClientService());
		
		return mockOauthService;
	}

	public void setMockOauthService(MockAuthenticationServiceImpl mockOauthService) {
		this.mockOauthService = mockOauthService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authentication;
		
		//getMockOauthService().
	}

	public Application parseApplication(String jsonData) {
		return mockOauthService.parseApplication(jsonData);
	}

	@Override
	public void loadApiKeysFromFiles() throws ApplicationAuthenticationException {
		getMockOauthService().loadApiKeysFromFiles();
		
	}

	
	
}
