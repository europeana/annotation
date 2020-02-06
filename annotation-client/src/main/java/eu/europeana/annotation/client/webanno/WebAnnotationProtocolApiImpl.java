package eu.europeana.annotation.client.webanno;

import java.io.IOException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.BaseAnnotationApi;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class WebAnnotationProtocolApiImpl extends BaseAnnotationApi implements WebAnnotationProtocolApi {

	@Override
	public ResponseEntity<String> createAnnotation(
			Boolean indexOnCreate, String annotation, String annoType) {

		ResponseEntity<String> res;
		try {
			res = apiConnection.createAnnotation(indexOnCreate, annotation, annoType);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}
	
	@Override
	public ResponseEntity<String> createAnnotation(String annotation,
			String annoType) {
		return createAnnotation(true, annotation, annoType);
	}

	@Override
	public ResponseEntity<String> deleteAnnotation(String identifier) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.deleteAnnotation(identifier);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi deleteAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getAnnotation(String wskey, String identifier) {

		ResponseEntity<String> res;
		try {
			res = apiConnection.getAnnotation(wskey, identifier, false);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi getAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getAnnotation(String wskey, String identifier, SearchProfiles searchProfile) {

		ResponseEntity<String> res;
		try {
			res = apiConnection.getAnnotation(wskey, identifier, false, searchProfile);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi getAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> updateAnnotation(String identifier, String annotation) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.updateAnnotation(identifier, annotation);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi updateAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> createTag(Boolean indexOnCreate,
			String annotation) {
		return createAnnotation(indexOnCreate, annotation, WebAnnotationFields.TAG);
	}
	
	@Override
	public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException{
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		String jsonLdStr = response.getBody();
		return europeanaParser.parseAnnotation(null, jsonLdStr);
	}

	@Override
	public ResponseEntity<String> createAnnotationReport(String wskey, String identifier,
			String userToken) {
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.createAnnotationReport(wskey, identifier, userToken);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> getModerationReport(String wskey, String identifier,
			String userToken) {
		
		ResponseEntity<String> res;
		try {
			res = apiConnection.getModerationReport(wskey, identifier);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
		}

		return res;
	}

	@Override
	public ResponseEntity<String> uploadAnnotations(String tag, Boolean indexOnCreate) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.uploadAnnotations(tag, indexOnCreate);
		} catch (IOException e) {
			throw new TechnicalRuntimeException(
					"Exception occured when invoking the uploadAnnotations method", e);
		}

		return res;
	}

}
