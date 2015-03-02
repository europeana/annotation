package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationJsonLdApiImpl extends BaseAnnotationApi implements AnnotationJsonLdApi {

	public AnnotationJsonLdApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public AnnotationJsonLdApiImpl(){
		super();
	}

	@Override
	public Annotation createAnnotation(String annotationJsonLdStr){
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createAnnotation(annotationJsonLdStr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonLdApi", e);
		}

		return res.getAnnotation();
	}
	
}
