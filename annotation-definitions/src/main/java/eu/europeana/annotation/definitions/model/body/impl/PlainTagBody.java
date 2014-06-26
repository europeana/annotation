package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class PlainTagBody extends BaseBody implements TagBody{

	private String tagId;
	
	public PlainTagBody(){
		super();
		setBodyType(BodyTypes.TAG);
	}

	@Override
	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}
