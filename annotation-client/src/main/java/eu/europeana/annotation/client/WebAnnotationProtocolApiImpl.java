package eu.europeana.annotation.client;

import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class WebAnnotationProtocolApiImpl extends BaseAnnotationApi implements WebAnnotationProtocolApi {

    public WebAnnotationProtocolApiImpl(ClientConfiguration configuration, AnnotationApiConnection apiConnection) {
	super(configuration, apiConnection);
    }

    public WebAnnotationProtocolApiImpl() {
	super();
    }

    @Override
    public ResponseEntity<String> createAnnotation(Boolean indexOnCreate, String annotation, String annoType, String user) {

	ResponseEntity<String> res;
	try {
	    res = apiConnection.createAnnotation(indexOnCreate, annotation, annoType, user);
	} catch (IOException e) {
	    throw new TechnicalRuntimeException(
		    "Exception occured when invoking the AnnotationJsonApi createAnnotation method", e);
	}

	return res;
    }

    @Override
    public ResponseEntity<String> createAnnotation(String annotation, String annoType, String user) {
	return createAnnotation(true, annotation, annoType, user);
    }

    @Override
    public ResponseEntity<String> deleteAnnotation(long identifier) {
	ResponseEntity<String> res;
	try {
	    res = apiConnection.deleteAnnotation(identifier, null);
	} catch (IOException e) {
	    throw new TechnicalRuntimeException(
		    "Exception occured when invoking the AnnotationJsonApi deleteAnnotation method", e);
	}

	return res;
    }
    
    @Override
    public ResponseEntity<String> removeAnnotation(long identifier) {
    ResponseEntity<String> res;
    try {
        res = apiConnection.removeAnnotation(identifier);
    } catch (IOException e) {
        throw new TechnicalRuntimeException(
            "Exception occured when invoking the AnnotationJsonApi removeAnnotation method", e);
    }

    return res;
    }

    @Override
    public ResponseEntity<String> getAnnotation(String wskey, long identifier) {

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
    public ResponseEntity<String> getAnnotation(String wskey, long identifier, SearchProfiles searchProfile) {

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
    public ResponseEntity<String> getAnnotation(long identifier, SearchProfiles searchProfile) {

	ResponseEntity<String> res;
	try {
	    res = apiConnection.getAnnotation(identifier, false, searchProfile);
	} catch (IOException e) {
	    throw new TechnicalRuntimeException(
		    "Exception occured when invoking the AnnotationJsonApi getAnnotation method", e);
	}

	return res;
    }

    @Override
    public ResponseEntity<String> updateAnnotation(long identifier, String annotation, String user) {
	ResponseEntity<String> res;
	try {
	    res = apiConnection.updateAnnotation(identifier, annotation, user);
	} catch (IOException e) {
	    throw new TechnicalRuntimeException(
		    "Exception occured when invoking the AnnotationJsonApi updateAnnotation method", e);
	}

	return res;
    }

    @Override
    public ResponseEntity<String> createTag(Boolean indexOnCreate, String annotation) {
	return createAnnotation(indexOnCreate, annotation, WebAnnotationFields.TAG, null);
    }

    @Override
    public Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException {
	AnnotationLdParser europeanaParser = new AnnotationLdParser();
	String jsonLdStr = response.getBody();
	return europeanaParser.parseAnnotation(null, jsonLdStr);
    }

    @Override
    public ResponseEntity<String> createAnnotationReport(String wskey, long identifier, String userToken) {

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
    public ResponseEntity<String> getModerationReport(String wskey, long identifier, String userToken) {

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
	    throw new TechnicalRuntimeException("Exception occured when invoking the uploadAnnotations method", e);
	}

	return res;
    }
}
