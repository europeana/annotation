package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class SemanticTagBody extends PlainTagBody{

	private String related;
	
	public SemanticTagBody(){
		super();
		setInternalType(BodyInternalTypes.SEMANTIC_TAG.name());
	}

	public String getRelated() {
		return related;
	}

	public void setRelated(String related) {
		this.related = related;
	}

}
