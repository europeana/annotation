package eu.europeana.annotation.client.webanno;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;

public class WebAnnotationProtocolApiImpl extends BaseAnnotationApi implements WebAnnotationProtocolApi{

	@Override
	public ResponseEntity<String> createAnnotation(String wskey, String provider, String identifier,
			boolean indexOnCreate, String annotation, String userToken, String annoType) {
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.createAnnotation(
					wskey, provider, identifier, indexOnCreate, annotation, userToken, annoType);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(String wskey, String provider, String identifier, String userToken, String format) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.deleteAnnotation(
					wskey, provider, identifier, userToken, format);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi deleteAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getAnnotation(String wskey, String provider, String identifier){
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.getAnnotation(
					wskey, provider, identifier, false);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi getAnnotation method", e);
		}

		return res;	
	}

	@Override
	public ResponseEntity<String> updateAnnotation(String wskey, String provider, String identifier, String annotation,
			String userToken) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.updateAnnotation(
					wskey, provider, identifier, annotation, userToken, null);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi updateAnnotation method", e);
		}

		return res;
	}

}
