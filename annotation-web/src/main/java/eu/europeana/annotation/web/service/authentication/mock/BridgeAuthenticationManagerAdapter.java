package eu.europeana.annotation.web.service.authentication.mock;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.api.commons.oauth2.service.impl.BridgeAuthenticationManager;

/**
 * This class implements an adapter for the existing authorization service (MockAuthenticationServiceImpl), in order to implement the OAuth2 interfaces.
 * This service reuses the apiKey as the clientId, and the userToken as accessToken (and refreshToken)
 * @author GordeaS
 *
 */
public class BridgeAuthenticationManagerAdapter extends BridgeAuthenticationManager implements AuthenticationService, ClientDetailsService {

	AuthenticationService mockOauthService;
	
	public BridgeAuthenticationManagerAdapter(){
		super();
		initService();
	}
	
	public void initService(){
		//initialize MockOautService
		//initialize tokeStoreService
		setTokenServices((MockAuthenticationServiceImpl)getMockOauthService());
	}
	
//	@Resource
//	AnnotationConfiguration configuration;
	
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
		if(mockOauthService == null)
			mockOauthService = new MockAuthenticationServiceImpl();
		
		return mockOauthService;
	}

	public void setMockOauthService(AuthenticationService mockOauthService) {
		this.mockOauthService = mockOauthService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authentication;
		
		//getMockOauthService().
	}
	
	
}
