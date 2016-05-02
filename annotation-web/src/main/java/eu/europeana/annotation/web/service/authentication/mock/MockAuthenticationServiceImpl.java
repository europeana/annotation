package eu.europeana.annotation.web.service.authentication.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.web.exception.authentication.ApplicationAuthenticationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.UserGroups;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.annotation.web.service.authentication.model.ApplicationDeserializer;
import eu.europeana.annotation.web.service.authentication.model.BaseDeserializer;
import eu.europeana.annotation.web.service.authentication.model.ClientApplicationImpl;

public class MockAuthenticationServiceImpl implements AuthenticationService
// , ApiKeyService
{

	private static final String COLLECTIONS_API_KEY = "phVKTQ8g9F";
	private static final String COLLECTIONS_USER_TOKEN = "pyU4HCDWfS";

	@Resource
	AnnotationConfiguration configuration;
	
	Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	} 

	public static final String EUROPEANA_FOUNDATION = "Europeana Foundation";
	public static final String EUROPEANA_COLLECTIONS = "Europeana Collections";

	public final String API_KEY_CONFIG_FOLDER = "/config";
	public final String API_KEY_STORAGE_FOLDER = "/authentication";

	private Map<String, Application> cachedClients = new HashMap<String, Application>();

	protected Map<String, Application> getCachedClients() {
		return cachedClients;
	}

	public Application readApiKeyApplicationFromFile(String path)
			throws ApplicationAuthenticationException {

		Application app;

		try {
			BufferedReader br = new BufferedReader(new FileReader(path));

			BaseDeserializer deserializer = new BaseDeserializer();
			Gson gson = deserializer.registerDeserializer(Application.class, new ApplicationDeserializer());
			String jsonData = br.readLine();
			app = gson.fromJson(jsonData, Application.class);
			br.close();

			getLogger().debug("Loaded Api Key: " + app.getApiKey());
		} catch (IOException e) {
			throw new ApplicationAuthenticationException(
					ApplicationAuthenticationException.MESSAGE_APIKEY_FILE_NOT_FOUND, path , e);
		}
		return app;
	}

	@Override
	public void loadApiKeys() throws ApplicationAuthenticationException {

		Application app = null;

		URL authenticationConfigFolder = getClass().getResource(API_KEY_CONFIG_FOLDER + API_KEY_STORAGE_FOLDER);
		
		logger.debug("Loading authentication configurations from File: " + authenticationConfigFolder);
		
		File folder = new File(authenticationConfigFolder.getFile());
		File[] listOfFiles = folder.listFiles();
		
		if(!(listOfFiles.length > 0))
			logger.warn("No authentication configurations found!");
		
		
		logger.debug("Loading authentication configurations from File: " + authenticationConfigFolder);
		

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getAbsolutePath();
				
				getLogger().info("Loading api keys from file: " + fileName);
				Application templateApp = readApiKeyApplicationFromFile(fileName);
				app = createMockClientApplication(
							templateApp.getApiKey(), templateApp.getOrganization(),
							templateApp.getName());
					
				//put app in the cache
				getCachedClients().put(app.getApiKey(), app);				
			}
		}
	}

	protected Application createMockClientApplication(String apiKey, String organization, String applicationName) {
		Application app = new ClientApplicationImpl();
		app.setApiKey(apiKey);
		app.setName(applicationName);
		if (applicationName != null) {
			String provider = applicationName.toLowerCase().replace(" ", "");
			app.setProvider(provider);
			app.setHomepage(buildHomePage(provider));
		}

		app.setOrganization(organization);
		Agent annonymous = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		annonymous.setName(applicationName + "-" + WebAnnotationFields.USER_ANONYMOUNS);
		annonymous.setUserGroup(UserGroups.ANONYMOUS.name());
		app.setAnonymousUser(annonymous);

		Agent admin = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		admin.setName(applicationName + "-" + WebAnnotationFields.USER_ADMIN);
		if (WebAnnotationFields.PROVIDER_EUROPEANA_DEV.equals(applicationName))
			admin.setUserGroup(UserGroups.ADMIN.name());
		else
			admin.setUserGroup(UserGroups.USER.name());

		app.setAdminUser(admin);

		// authenticated users
		createTesterUsers(applicationName, app);

		createRegularUser(apiKey, applicationName, app);

		return app;
	}

	protected void createRegularUser(String apiKey, String applicationName, Application app) {
		if (!COLLECTIONS_API_KEY.equals(apiKey))
			return;

		Agent collectionsUser = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		String username = "Europeana Collections Curator";
		collectionsUser.setName(applicationName + "-" + username);
		collectionsUser.setOpenId(username + "@" + applicationName);
		collectionsUser.setUserGroup(UserGroups.USER.name());

		app.addAuthenticatedUser(COLLECTIONS_USER_TOKEN, collectionsUser);
	}

	protected void createTesterUsers(String applicationName, Application app) {

		// testers not allowed in production
		if (getConfiguration().isProductionEnvironment())
			return;

		String username = "tester1";
		Agent tester1 = createTesterUser(username, applicationName);
		app.addAuthenticatedUser(username, tester1);

		username = "tester2";
		Agent tester2 = createTesterUser(username, applicationName);
		app.addAuthenticatedUser(username, tester2);

		username = "tester3";
		Agent tester3 = createTesterUser(username, applicationName);
		app.addAuthenticatedUser(username, tester3);
	}

	protected Agent createTesterUser(String username, String applicationName) {
		Agent tester1 = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		tester1.setName(applicationName + "-" + username);
		tester1.setOpenId(username + "@" + applicationName);
		tester1.setUserGroup(UserGroups.TESTER.name());
		return tester1;
	}

	protected String buildHomePage(String provider) {
		if (WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider))
			return "http://historypin.com";

		if (WebAnnotationFields.PROVIDER_PUNDIT.equals(provider))
			return "http://pundit.it";

		if (WebAnnotationFields.PROVIDER_WEBANNO.equals(provider))
			return "http://europeana.eu";

		return null;

	}

	@Override
	public Agent getUserByToken(String apiKey, String userToken) throws UserAuthorizationException {
		Agent user = null;

		try {
			Application clientApp = getByApiKey(apiKey);
			if (WebAnnotationFields.USER_ANONYMOUNS.equals(userToken))
				user = clientApp.getAnonymousUser();
			else if (WebAnnotationFields.USER_ADMIN.equals(userToken))
				user = clientApp.getAdminUser();
			else
				user = clientApp.getAuthenticatedUsers().get(userToken);

		} catch (ApplicationAuthenticationException e) {
			throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_INVALID_TOKEN, userToken, e);
		}

		if (user == null)
			throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_INVALID_TOKEN, userToken);

		return user;
	}

	@Override
	public Application getByApiKey(String apiKey) throws ApplicationAuthenticationException {

		Application app = null;

		if (getCachedClients().isEmpty())
			loadApiKeys(); 
		
		app = getCachedClients().get(apiKey);
				
		if (app == null)
			throw new ApplicationAuthenticationException(ApplicationAuthenticationException.MESSAGE_INVALID_APIKEY,
					apiKey);

		return app;
	}

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

}
