package eu.europeana.annotation.client.webanno;

import java.io.IOException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class WebAnnotationProtocolApiImpl extends BaseAnnotationApi implements WebAnnotationProtocolApi {

	@Override
	public ResponseEntity<String> createAnnotation(String wskey, String provider, String identifier,
			Boolean indexOnCreate, String annotation, String userToken, String annoType) {

		ResponseEntity<String> res;
		try {
			res = apiConnection.createAnnotation(wskey, provider, identifier, indexOnCreate, annotation, userToken,
					annoType);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}
	
	@Override
	public ResponseEntity<String> createAnnotation(String wskey, String provider, String identifier, String annotation,
			String userToken, String annoType) {
		return createAnnotation(wskey, provider, identifier,
				true, annotation, userToken, annoType);
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(String wskey, String provider, String identifier, String userToken,
			String format) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.deleteAnnotation(wskey, provider, identifier, userToken, format);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi deleteAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getAnnotation(String wskey, String provider, String identifier) {

		ResponseEntity<String> res;
		try {
			res = apiConnection.getAnnotation(wskey, provider, identifier, false);
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
			res = apiConnection.updateAnnotation(wskey, provider, identifier, annotation, userToken, null);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi updateAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> createTag(String provider, String identifier, Boolean indexOnCreate,
			String annotation, String userToken) {
		return createAnnotation(getConfiguration().getApiKey(), provider, identifier, indexOnCreate, annotation,
				userToken, WebAnnotationFields.TAG);
	}

	@Override
	public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException{
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		return europeanaParser.parseAnnotation(null, response.getBody());
	}

	@Override
	public ResponseEntity<String> createAnnotationReport(String wskey, String provider, String identifier,
			String userToken) {
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.createAnnotationReport(wskey, provider, identifier, userToken);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getModerationReport(String wskey, String provider, String identifier,
			String userToken) {
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.getModerationReport(wskey, provider, identifier, userToken);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}

}
