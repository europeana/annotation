package eu.europeana.annotation.definitions.model.authentication;

import java.util.Date;

public interface Client {

	void setAuthenticationConfigJson(String authenticationConfigJson);

	String getAuthenticationConfigJson();

	void setClientId(String clientId);

	String getClientId();

	void setLastUpdate(Date lastUpdate);

	Date getLastUpdate();

	void setCreationDate(Date creationDate);

	Date getCreationDate();

}
