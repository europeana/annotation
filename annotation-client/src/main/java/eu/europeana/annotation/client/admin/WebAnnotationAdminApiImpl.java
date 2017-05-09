package eu.europeana.annotation.client.admin;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;

public class WebAnnotationAdminApiImpl extends BaseAnnotationApi implements WebAnnotationAdminApi {
	
	protected final Logger logger = Logger.getLogger(this.getClass());

	public WebAnnotationAdminApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public WebAnnotationAdminApiImpl(){
		super();
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(Integer id) {
		AnnotationPage res;
		try {
			return apiConnection.deleteAnnotation(id);
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
