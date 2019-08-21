package eu.europeana.annotation.client.admin;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;

public class WebAnnotationAdminApiImpl extends BaseAnnotationApi implements WebAnnotationAdminApi {
	
	protected final Logger logger = LogManager.getLogger(this.getClass());

	public WebAnnotationAdminApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public WebAnnotationAdminApiImpl(){
		super();
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(String provider, String identifier) {
		try {
			return apiConnection.deleteAnnotation(provider, identifier);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}
	}

	@Override
	public ResponseEntity<String> reindexOutdated() {
		try {
			return apiConnection.reindexOutdated();
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}
	}

}
