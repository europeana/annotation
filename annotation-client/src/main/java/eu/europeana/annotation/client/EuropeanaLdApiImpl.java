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
			String motivation, String provider, Long annotationNr, String europeanaLdStr) {
		return createAnnotationLd(motivation, provider, annotationNr, europeanaLdStr, null);
	}
	
	@Override
	public String createAnnotationLd(
			String motivation, String provider, Long annotationNr, String europeanaLdStr, String apikey) {
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createEuropeanaAnnotationLd(motivation, provider, annotationNr, europeanaLdStr, apikey);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the EuropenaLdApi for createEuropeanaAnnotationLd method", e);
		}

		return res.getJson();
	}

	@Override
	public String getAnnotationLd(String provider, Long annotationNr) {
		return getAnnotationLd(provider, annotationNr, null);
	}
	
	@Override
	public String getAnnotationLd(String provider, Long annotationNr, String apikey) {
		AnnotationSearchResults res;
		try {
			res = apiConnection.getAnnotationLd(provider, annotationNr, apikey);
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
	
	public AnnotationSearchResults getAnnotationSearchResults(String json) {
		AnnotationSearchResults res;
		res = apiConnection.getAnnotationSearchResults(json);
		return res;
	}

	
}
