package eu.europeana.annotation.definitions.model.authentication;

import java.util.Date;

public interface Client {

	void setAuthenticationConfigJson(String authenticationConfigJson);

	/**
	 * @deprecated EA-760 to be removed in follow up versions
	 * @return
	 */
	String getAuthenticationConfigJson();

	void setClientId(String clientId);

	String getClientId();

	void setLastUpdate(Date lastUpdate);

	Date getLastUpdate();

	void setCreationDate(Date creationDate);

	Date getCreationDate();
	
	void setClientApplication(Application clientApplication);
	
	Application getClientApplication();

}
