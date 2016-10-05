package eu.europeana.annotation.web.model;

import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class ConceptOperationResponse extends ApiResponse {
	
	Concept concept;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	
	public static String ERROR_CONCEPT_EXISTS_IN_DB = 
			"Cannot store concept object! An object with the given url already exists in the database: ";
	
	public ConceptOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

}
