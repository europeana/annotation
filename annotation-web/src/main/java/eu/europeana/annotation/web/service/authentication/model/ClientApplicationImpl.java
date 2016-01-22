package eu.europeana.annotation.web.service.authentication.model;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.annotation.definitions.model.agent.Agent;

public class ClientApplicationImpl implements Application{

	private String apiKey;
	// private String privateKey;
	private String name;
	private String provider;
	private String organization;
	private String homepage;
	private String openId;
	private Agent anonymousUser;
	private Agent adminUser;
	private Map<String, Agent> authenticatedUsers = new HashMap<String, Agent>();
	
	@Override
	public String getHomepage() {
		return homepage;
	}

	@Override
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	@Override
	public String getOpenId() {
		return openId;
	}

	@Override
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String getProvider() {
		return provider;
	}
	
	@Override
	public String getOrganization() {
		return organization;
	}

	@Override
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public Agent getAnonymousUser() {
		return anonymousUser;
	}

	@Override
	public void setAnonymousUser(Agent anonymousUser) {
		this.anonymousUser = anonymousUser;
	}

	@Override
	public Agent getAdminUser() {
		return adminUser;
	}

	@Override
	public void setAdminUser(Agent adminUser) {
		this.adminUser = adminUser;
	}

	@Override
	public Map<String, Agent> getAuthenticatedUsers() {
		return authenticatedUsers;
	}

	@Override
	public void addAuthenticatedUser(String key, Agent user) {
		getAuthenticatedUsers().put(key, user);
	}
	
	

}
