package eu.europeana.annotation.client.admin;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;

public class WebAnnotationAdminApiImpl extends BaseAnnotationApi implements WebAnnotationAdminApi {

	public WebAnnotationAdminApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public WebAnnotationAdminApiImpl() throws Exception{
		super();
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(long identifier) {
		try {
			return apiConnection.deleteAnnotation(identifier, null);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}
	}
	
    @Override
    public ResponseEntity<String> removeAnnotation(long identifier) {
        try {
            return apiConnection.removeAnnotation(identifier);
        } catch (IOException e) {
            throw new TechnicalRuntimeException("Exception occured when invoking the remove annotation api.", e);
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
