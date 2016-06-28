package eu.europeana.annotation.web.service.authorization;

import javax.annotation.Resource;

import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class AuthorizationServiceImpl implements AuthorizationService {

	@Resource
	AuthenticationService authenticationService;

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
}
