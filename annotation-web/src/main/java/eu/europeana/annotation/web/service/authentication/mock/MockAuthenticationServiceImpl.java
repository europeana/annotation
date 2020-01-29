package eu.europeana.annotation.web.service.authentication.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.google.gson.Gson;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.service.PersistentClientService;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.model.vocabulary.UserGroups;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;
import eu.europeana.annotation.web.service.authentication.model.ApplicationDeserializer;
import eu.europeana.annotation.web.service.authentication.model.BaseDeserializer;
import eu.europeana.annotation.web.service.authentication.model.ClientApplicationImpl;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;

public class MockAuthenticationServiceImpl implements AuthenticationService, ResourceServerTokenServices
// , ApiKeyService
{

	private static final String COLLECTIONS_API_KEY = "phVKTQ8g9F";
	private static final String COLLECTIONS_USER_TOKEN = "pyU4HCDWfS";

	AnnotationConfiguration configuration;
	PersistentClientService clientService;

	Logger logger = LogManager.getLogger(getClass());

	public MockAuthenticationServiceImpl(AnnotationConfiguration configuration, PersistentClientService clientService) {
		this.configuration = configuration;
		this.clientService = clientService;
	}

	public Logger getLogger() {
		return logger;
	}

	public static final String EUROPEANA_FOUNDATION = "Europeana Foundation";
	public static final String EUROPEANA_COLLECTIONS = "Europeana Collections";

	public final String API_KEY_CONFIG_FOLDER = "/config";
	public final String API_KEY_STORAGE_FOLDER = "/authentication";

	private Map<String, Application> cachedClients = new HashMap<String, Application>();

	public Map<String, Application> getCachedClients() {
		return cachedClients;
	}

	// TODO: 524 read authentication config from file
	public Application readApplicationConfigFromFile(String path) throws ApplicationAuthenticationException {

		Application app;

		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String jsonData = br.readLine();

			app = parseApplication(jsonData);
			br.close();

			getLogger().debug("Loaded Api Key: " + app.getApiKey());
		} catch (IOException e) {
			throw new ApplicationAuthenticationException( I18nConstants.APIKEY_FILE_NOT_FOUND, I18nConstants.APIKEY_FILE_NOT_FOUND, new String[]{path}, HttpStatus.UNAUTHORIZED, e);
		}
		return app;
	}

	@Override
	public Application parseApplication(String jsonData) {
		Application app;
		BaseDeserializer deserializer = new BaseDeserializer();
		Gson gson = deserializer.registerDeserializer(Application.class, new ApplicationDeserializer());
		app = gson.fromJson(jsonData, Application.class);
		return app;
	}

	@Override
	public void loadApiKeysFromFiles() throws ApplicationAuthenticationException {

		// read from MongoDB

		URL authenticationConfigFolder = getClass().getResource(API_KEY_CONFIG_FOLDER + API_KEY_STORAGE_FOLDER);

		logger.debug("Loading authentication configurations from File: " + authenticationConfigFolder);

		File folder = new File(authenticationConfigFolder.getFile());
		File[] listOfFiles = folder.listFiles();

		if (!(listOfFiles.length > 0))
			logger.warn("No authentication configurations found!");

		logger.debug("Loading authentication configurations from File: " + authenticationConfigFolder);

		// TODO: 524 get authentication config files
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getAbsolutePath();

				getLogger().info("Loading api keys from file: " + fileName);
				Application application = readApplicationConfigFromFile(fileName);

				// put app in the cache
				getCachedClients().put(application.getApiKey(), application);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadApiKeys() throws ApplicationAuthenticationException {

		// read from MongoDB
		Iterable<PersistentClient> allStoredClients = clientService.findAll();

		Application application;

		for (PersistentClient storedClient : allStoredClients) {
//			System.out.println(storedClient.getAuthenticationConfigJson());
			//TODO allow both until the databases are migrated
			if(storedClient.getClientApplication() != null)
				application = storedClient.getClientApplication();
			else
				application = parseApplication(storedClient.getAuthenticationConfigJson());
			
			// put app in the cache
			getCachedClients().put(application.getApiKey(), application);
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
		annonymous.setUserGroup(UserGroups.anonimous.name());
		app.setAnonymousUser(annonymous);

		Agent admin = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		admin.setName(applicationName + "-" + WebAnnotationFields.USER_ADMIN);
		if (WebAnnotationFields.PROVIDER_EUROPEANA_DEV.equals(applicationName))
			admin.setUserGroup(UserGroups.admin.name());
		else
			admin.setUserGroup(UserGroups.user.name());

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
		collectionsUser.setHttpUrl(username + "@" + applicationName);
		collectionsUser.setUserGroup(UserGroups.user.name());

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
		tester1.setHttpUrl(username + "@" + applicationName);
		tester1.setUserGroup(UserGroups.tester.name());
		return tester1;
	}

	protected String buildHomePage(String provider) {
		if (WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider))
			return "http://historypin.com";

		if (WebAnnotationFields.PROVIDER_PUNDIT.equals(provider))
			return "http://pundit.it";

		if (WebAnnotationFields.DEFAULT_PROVIDER.equals(provider))
			return "http://europeana.eu";

		return null;

	}

	@Override
	public Agent getUserByName(String apiKey, String userName) throws UserAuthorizationException {
		Agent user = null;

		// read user from cache
		try {
			Application clientApp = getByApiKey(apiKey);
			user = getUserByName(userName, clientApp);

		} catch (ApplicationAuthenticationException e) {
			throw new UserAuthorizationException(null, I18nConstants.INVALID_TOKEN, new String[]{userName}, e);
		}

		// refresh cache - add specific api key if found in MongoDB
		if (user == null) {
			// read from MongoDB
			Application application = loadApiKey(apiKey);
			if (application != null) {
				user = getUserByName(userName, application);
			}
		}

		// unknown user
		if (user == null)
			throw new UserAuthorizationException(null, I18nConstants.INVALID_TOKEN, new String[]{userName});

		return user;

	}

	@SuppressWarnings("deprecation")
	Application loadApiKey(String apiKey) {
		Client apiClient = clientService.findByApiKey(apiKey);
		Application application = null;

		if (apiClient != null) {
			application = parseApplication(apiClient.getAuthenticationConfigJson());
			// put app in the cache
			getCachedClients().put(application.getApiKey(), application);
		}
		return application;
	}

	private Agent getUserByName(String userName, Application application) {
		Agent user = null;
		if (WebAnnotationFields.USER_ANONYMOUNS.equals(userName)) {
			user = application.getAnonymousUser();
		} else if (WebAnnotationFields.USER_ADMIN.equals(userName)) {
			user = application.getAdminUser();
		} else {
			Collection<Agent> users = application.getAuthenticatedUsers().values();
			for (Agent agent : users) {
				if(userName.equals(agent.getName())){
					user = agent;
					break;
				}
			}
		}
		return user;
	}

	@Override
	public Application getByApiKey(String apiKey) throws ApplicationAuthenticationException {

		Application app = null;

		if (getCachedClients().isEmpty())
			loadApiKeys();

		app = getCachedClients().get(apiKey);

		// reload from database
		if (app == null)
			app = loadApiKey(apiKey);

		if (app == null)
			throw new ApplicationAuthenticationException(null, I18nConstants.INVALID_APIKEY, new String[]{apiKey});

		return app;
	}

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	public PersistentClientService getClientService() {
		return clientService;
	}

	public void setClientService(PersistentClientService clientService) {
		this.clientService = clientService;
	}
}
