package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;

public class AnnotationJsonLdApiImpl extends BaseAnnotationApi implements AnnotationJsonLdApi {

	public AnnotationJsonLdApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public AnnotationJsonLdApiImpl(){
		super();
	}

//	@Override
//	public Annotation createAnnotation(String annotationJsonLdStr){
//		
//		AnnotationOperationResponse res;
//		try {
//			res = apiConnection.createAnnotation(annotationJsonLdStr);
//		} catch (IOException e) {
//			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonLdApi", e);
//		}
//
//		return res.getAnnotation();
//	}
	
	@Override
	public String createAnnotationLd(
			String provider, Long annotationNr, String annotationJsonLdStr) {
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createAnnotationLd(provider, annotationNr, annotationJsonLdStr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonLdApi for createAnnotationJsonLd method", e);
		}

		return res.getJson();
	}

	@Override
	public String getAnnotationLd(String provider, Long annotationNr) {
		AnnotationSearchResults res;
		try {
			res = apiConnection.getAnnotationLd(provider, annotationNr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonLdApi for getAnnotationLd method", e);
		}

		return res.getJson();
	}

	
}
