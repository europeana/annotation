package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class SemanticTagBody extends PlainTagBody{

	private String related;
	
	public SemanticTagBody(){
		super();
		setTypeEnum(BodyTypes.SEMANTIC_TAG);
	}

	public String getRelated() {
		return related;
	}

	public void setRelated(String related) {
		this.related = related;
	}

}
