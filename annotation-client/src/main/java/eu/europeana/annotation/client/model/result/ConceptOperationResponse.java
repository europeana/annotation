package eu.europeana.annotation.client.model.result;

import eu.europeana.annotation.definitions.model.entity.Concept;

public class ConceptOperationResponse extends AbstractAnnotationApiResponse{

	private Concept concept;
	
	private String json;
	
	public Concept getConcept() {
		return concept;
	}
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
}
