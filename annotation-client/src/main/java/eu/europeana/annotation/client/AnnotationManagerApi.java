package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;

public class AnnotationManagerApi extends BaseAnnotationApi implements AnnotationManager{

	public AnnotationManagerApi(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public AnnotationManagerApi(){
		super();
	}

	@Override
	public Annotation createAnnotation(Annotation annotation){
		
		AnnotationOperationResponse res;
		try {
			res = apiConnection.createAnnotation(annotation);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationApi", e);
		}

		return res.getAnnotation();
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
}
