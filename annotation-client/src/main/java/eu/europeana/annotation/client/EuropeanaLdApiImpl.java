package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.definitions.model.Annotation;

public class EuropeanaLdApiImpl extends BaseAnnotationApi implements EuropeanaLdApi {

	public EuropeanaLdApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public EuropeanaLdApiImpl(){
		super();
	}

	@Override
	public String createAnnotationLd(
//			public Annotation createAnnotationLd(
			String provider, Long annotationNr, String europeanaLdStr) {
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createEuropeanaAnnotationLd(provider, annotationNr, europeanaLdStr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the EuropenaLdApi", e);
		}

		return res.getJson();
	}

	@Override
	public Annotation getAnnotationLd(String provider, Long annotationNr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation searchLd(String target, String resourceId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
