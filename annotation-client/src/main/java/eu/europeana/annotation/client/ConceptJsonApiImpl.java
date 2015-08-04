package eu.europeana.annotation.client;

import java.io.IOException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.ConceptOperationResponse;

public class ConceptJsonApiImpl extends BaseAnnotationApi implements ConceptJsonApi {

	public ConceptJsonApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public ConceptJsonApiImpl(){
		super();
	}

	@Override
	public String createConcept(String conceptJson){
		
		ConceptOperationResponse res;
		try {
			res = apiConnection.createConcept(conceptJson);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the ConceptJsonApi", e);
		}

		return res.getJson();
	}
	
	@Override
	public String getConcept(String url) {
		
		ConceptOperationResponse res;
		
		try {
			res = apiConnection.getConcept(url);
			
			if(!Boolean.valueOf(res.getSuccess()))
				throw new TechnicalRuntimeException(res.getError() + " " + res.getAction());
		
		} catch (IOException e) {
				throw new TechnicalRuntimeException("Exception occured when invoking the ConceptApi", e);
		}

		return res.getJson();
	}
	
}
