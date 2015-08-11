package eu.europeana.annotation.client;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;

public abstract class BaseAnnotationApi {

	protected final ClientConfiguration configuration;
	protected final AnnotationApiConnection apiConnection;

	public AnnotationApiConnection getApiConnection() {
		return apiConnection;
	}

	public BaseAnnotationApi(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		this.configuration = configuration;
		this.apiConnection = apiConnection;
	}

	public BaseAnnotationApi() {
		this.configuration = ClientConfiguration.getInstance();
		this.apiConnection = new AnnotationApiConnection(
				configuration.getServiceUri(), configuration.getApiKey());
	}

}
