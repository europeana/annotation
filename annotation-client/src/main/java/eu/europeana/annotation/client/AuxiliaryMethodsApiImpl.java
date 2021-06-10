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
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApiImpl;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationDeletion;

public class AuxiliaryMethodsApiImpl extends WebAnnotationProtocolApiImpl {

    public AuxiliaryMethodsApiImpl(ClientConfiguration configuration, AnnotationApiConnection apiConnection) {
	super(configuration, apiConnection);
    }

    public AuxiliaryMethodsApiImpl() {
	super();
    }

    public List<AnnotationDeletion> getDeleted(String motivation, Long afterTimestamp) {
	List<AnnotationDeletion> res = null;
	
	try {
	    String json = apiConnection.getDeleted(motivation, afterTimestamp);
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<BaseAnnotationDeletion> resBaseAnnotation = objectMapper.readValue(json, new TypeReference<List<BaseAnnotationDeletion>>(){});
	    if(resBaseAnnotation!=null) {
	    	res = new ArrayList<AnnotationDeletion>();
	    	for (BaseAnnotationDeletion del : resBaseAnnotation) {
	    		res.add(del);
	    	}
	    }
	
	} catch (IOException | JsonParseException e) {
	    throw new TechnicalRuntimeException(
		    "Exception occured when invoking the AnnotationJsonLdApi for getAnnotationLd method", e);
	}
	
	return res;
    }

}
