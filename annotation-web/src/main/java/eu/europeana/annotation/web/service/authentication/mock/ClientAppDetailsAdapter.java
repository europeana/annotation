package eu.europeana.annotation.web.service.authentication.mock;

import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.api.commons.oauth2.model.impl.ClientDetailsAdapter;

public class ClientAppDetailsAdapter extends ClientDetailsAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633217994274563272L;

	Application clientApp;
	
	public ClientAppDetailsAdapter(Application app){
		this.clientApp = app;
	}
	
	@Override
	public String getClientId() {
		return getClientApp().getApiKey();
	}

	public Application getClientApp() {
		return clientApp;
	}

	public void setClientApp(Application clientApp) {
		this.clientApp = clientApp;
	}
}
