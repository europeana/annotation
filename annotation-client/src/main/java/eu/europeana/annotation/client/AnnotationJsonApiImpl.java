package eu.europeana.annotation.client;

import java.io.IOException;
import java.util.List;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class AnnotationJsonApiImpl extends BaseAnnotationApi implements AnnotationJsonApi {

	public AnnotationJsonApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public AnnotationJsonApiImpl(){
		super();
	}

	@Override
//	public String createAnnotation(String annotation){
	public String createAnnotation(Annotation annotation){
//		public Annotation createAnnotation(Annotation annotation){
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createAnnotation(annotation);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();
//		return res.getAnnotation();
	}
	
	@Override
	public ImageAnnotation createImageAnnotation(ImageAnnotation annotation){
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createAnnotation(annotation);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}

		return (ImageAnnotation)res.getAnnotation();
	}
	
//	@Override
//	public SemanticTag createSemanticTag(SemanticTag annotation){
//		
//		AnnotationOperationResponse res;
//		try {
//			res = apiConnection.createAnnotation(annotation);
//		} catch (IOException e) {
//			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
//		}
//
//		return (SemanticTag)res.getAnnotation();
//	}
	
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
	public String getAnnotations(String collectionId, String objectHash, String provider){
//		public List<Annotation> getAnnotations(String collectionId, String objectHash, String provider){
		AnnotationSearchResults res;
		try {
			res = apiConnection.getAnnotationsForObject(collectionId, objectHash, provider);
		} catch (IOException e) {

			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}
//		return res.getItems();
		return res.getJson();
		
	}

	@Override
	public Annotation getAnnotation(String collectionId, String objectHash, String provider,
			Integer annotationNr) {
		
		return getAnnotation(collectionId + WebAnnotationFields.SLASH + objectHash, provider, annotationNr);
	}
	
	@Override
	public Annotation getAnnotation(String europeanaId, String provider, Integer annotationNr) {
		
		AnnotationOperationResponse res;
		
		try {
			res = apiConnection.getAnnotation(europeanaId, provider, annotationNr);
			
			if(!Boolean.valueOf(res.getSuccess()))
				throw new TechnicalRuntimeException(res.getError() + " " + res.getAction());
		
		} catch (IOException e) {
				throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}

		return res.getAnnotation();
	}
	
	public String setAnnotationStatus(String provider, Long annotationNr, String status) {
		AnnotationOperationResponse res;
		try {
			res = apiConnection.setAnnotationStatus(provider, annotationNr, status);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();		
	}
	
	public String searchAnnotationStatusLogs(String status, String startOn, String limit) {
		AnnotationOperationResponse res;
		try {
			res = apiConnection.searchAnnotationStatusLogs(status, startOn, limit);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();		
	}
	
	public String getAnnotationStatus(String provider, Long annotationNr) {
		AnnotationOperationResponse res;
		try {
			res = apiConnection.getAnnotationStatus(provider, annotationNr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();		
	}
	

	public String disableAnnotation(String provider, Long annotationNr){
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.disableAnnotation(provider, annotationNr);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();
//		return res.getAnnotation();
	}
	

	public String checkVisibility(Annotation annotation, String user) {
		AnnotationOperationResponse res;
		try {
			res = apiConnection.checkVisibility(annotation, user);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationJsonApi", e);
		}

		return res.getJson();		
	}
	

}
