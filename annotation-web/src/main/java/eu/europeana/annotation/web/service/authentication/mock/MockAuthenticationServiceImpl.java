package eu.europeana.annotation.web.service.authentication.mock;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.Person;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.annotation.web.service.authentication.model.ClientApplicationImpl;

public class MockAuthenticationServiceImpl implements AuthenticationService
// , ApiKeyService
{
	private Map<String, Application> cachedClients = new HashMap<String, Application>();
	
	
	protected Map<String, Application> getCachedClients() {
		return cachedClients;
	}

	@Override
	public Application findByApiKey(String apiKey) throws ApplicationAuthenticationException {

		Application app;
		switch (apiKey) {

		case "apidemo":
			app = createMockClientApplication(apiKey, "Europeana Foundation", WebAnnotationFields.PROVIDER_WEBANNO);			
			break;
		case "hpdemo":
			app = createMockClientApplication(apiKey, "HistoryPin", WebAnnotationFields.PROVIDER_HISTORY_PIN);			
			break;
		case "punditdemo":
			app = createMockClientApplication(apiKey, "Pundit", WebAnnotationFields.PROVIDER_PUNDIT);			
			break;
		default:
			throw new ApplicationAuthenticationException(ApplicationAuthenticationException.MESSAGE_INVALID_APIKEY, apiKey);
		}

		return app;
	}

	protected Application createMockClientApplication(String apiKey, String organization, String applicationName)  {
		Application app = new ClientApplicationImpl(); 
	    app.setApiKey(apiKey);
	    app.setName(applicationName);
	    if(applicationName != null) {
			String provider = applicationName.toLowerCase().replace(" ", "");
			app.setProvider(provider);
			app.setHomepage(buildHomePage(provider));
		}
	    	
		app.setOrganization(organization);
		Agent annonymous = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		annonymous.setName(applicationName + "-" + WebAnnotationFields.USER_ANONYMOUNS);
		app.setAnonymousUser(annonymous);

		Agent admin = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		admin.setName(applicationName + "-" + WebAnnotationFields.USER_ADMIN);
		app.setAdminUser(admin);
		
		return app;
	}

	protected String buildHomePage(String provider) {
		if(WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider))
			return "http://historypin.com";

		if(WebAnnotationFields.PROVIDER_PUNDIT.equals(provider))
			return "http://pundit.it";

		if(WebAnnotationFields.PROVIDER_WEBANNO.equals(provider))
			return "http://europeana.eu";
		
		return null;

	}

	@Override
	public Agent getUserByToken(String apiKey, String userToken) throws UserAuthorizationException {
		Agent user = null;
		
		try {
			Application clientApp = getByApiKey(apiKey);
			if(WebAnnotationFields.USER_ANONYMOUNS.equals(userToken))
				user = clientApp.getAnonymousUser();
			else if (WebAnnotationFields.USER_ADMIN.equals(userToken))
				user = clientApp.getAdminUser();
			
		} catch (ApplicationAuthenticationException e) {
			throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_INVALID_TOKEN, userToken, e);
		}
		
		if(user == null)
			throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_INVALID_TOKEN, userToken);
		
		return user;
	}

	@Override
	public Application getByApiKey(String apiKey) throws ApplicationAuthenticationException{
		Application app = getCachedClients().get(apiKey);
		if(app != null)
			return app;
		
		//else
		app = findByApiKey(apiKey); //throws exception
		getCachedClients().put(apiKey, app);
		
		return app;
	}

}
