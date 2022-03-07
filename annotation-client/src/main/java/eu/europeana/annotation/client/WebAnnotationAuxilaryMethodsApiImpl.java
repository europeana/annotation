package eu.europeana.annotation.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationDeletion;

public class WebAnnotationAuxilaryMethodsApiImpl extends BaseAnnotationApi implements WebAnnotationAuxilaryMethodsApi {

    public WebAnnotationAuxilaryMethodsApiImpl(ClientConfiguration configuration, AnnotationApiConnection apiConnection) {
	super(configuration, apiConnection);
    }

    public WebAnnotationAuxilaryMethodsApiImpl() {
	super();
    }
    
    @Override
    public List<String> getDeleted(String motivation, String from, String to, int page, int limit) {
    	List<String> res = null;
    	
    	try {
    	    String json = apiConnection.getDeleted(motivation, from, to, page, limit);
    	    ObjectMapper objectMapper = new ObjectMapper();
    	    res = objectMapper.readValue(json, new TypeReference<List<String>>(){});	
    	} catch (IOException | JsonParseException e) {
    	    throw new TechnicalRuntimeException(
    		    "Exception occured when invoking the AnnotationJsonLdApi for getAnnotationLd method", e);
    	}
    	
    	return res;
    }

}
