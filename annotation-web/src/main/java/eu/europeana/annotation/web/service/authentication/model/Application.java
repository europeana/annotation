package eu.europeana.annotation.web.service.authentication.model;

import eu.europeana.annotation.definitions.model.agent.Agent;

public interface Application {

	void setAdminUser(Agent adminUser);

	Agent getAdminUser();

	void setAnonymousUser(Agent anonymousUser);

	Agent getAnonymousUser();

	void setOrganization(String organization);

	String getOrganization();

	void setApiKey(String apiKey);

	String getApiKey();

	void setName(String name);

	String getName();

	String getProvider();

	void setProvider(String provider);

	void setOpenId(String openId);

	String getOpenId();

	void setHomepage(String homepage);

	String getHomepage();

}
