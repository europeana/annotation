package eu.europeana.annotation.client;

import java.io.IOException;
import java.util.List;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationRetrievalApi extends BaseAnnotationApi implements AnnotationRetrieval{

	public AnnotationRetrievalApi(ClientConfiguration configuration, AnnotationApiConnection apiConnection){
		super(configuration, apiConnection);
	}
	
	public AnnotationRetrievalApi(){
		 super();		
	}
	
	@Override
	public List<Annotation> getAnnotations(String collectionId, String objectHash){
		AnnotationSearchResults res;
		try {
			res = apiConnection.getAnnotationsForObject(collectionId, objectHash);
		} catch (IOException e) {

			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}
		return res.getItems();
		
	}

	@Override
	public Annotation getAnnotation(String collectionId, String objectHash,
			Integer annotationNr) {
		
		return getAnnotation(collectionId + "/" + objectHash, annotationNr);
	}
	
	@Override
	public Annotation getAnnotation(String europeanaId,
			Integer annotationNr) {
		
		AnnotationOperationResponse res;
		
		try {
			res = apiConnection.getAnnotation(europeanaId, annotationNr);
			
			if(!Boolean.valueOf(res.getSuccess()))
				throw new TechnicalRuntimeException(res.getError() + " " + res.getAction());
		
		} catch (IOException e) {
				throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}

		return res.getAnnotation();
	}
}
