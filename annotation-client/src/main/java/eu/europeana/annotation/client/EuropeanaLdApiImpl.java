package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;

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
			throw new TechnicalRuntimeException("Exception occured when invoking the EuropenaLdApi for createEuropeanaAnnotationLd method", e);
		}

		return res.getJson();
	}

	@Override
	public String getAnnotationLd(String provider, Long annotationNr) {
//		public Annotation getAnnotationLd(String provider, Long annotationNr) {
		AnnotationSearchResults res;
		try {
			res = apiConnection.getAnnotationLd(provider, annotationNr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the EuropenaLdApi for getAnnotationLd method", e);
		}

		return res.getJson();
	}

	@Override
	public String searchLd(String target, String resourceId) {
//		public Annotation searchLd(String target, String resourceId) {
		AnnotationSearchResults res;
		try {
			res = apiConnection.searchAnnotationLd(target, resourceId);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the EuropenaLdApi for searchAnnotationLd method", e);
		}

		return res.getJson();
	}
	
}
