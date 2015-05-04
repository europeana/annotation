package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class PlainTagBody extends BaseBody implements TagBody{

	private String tagId;
	
	public PlainTagBody(){
		super();
		setTypeEnum(BodyTypes.TAG);
	}

	@Override
	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	@Override
	public String toString() {
        String res = super.toString();		
		if (getTagId() != null) 
			res = res + "\t\t" + "tagId:" + getTagId() + "\n";
		return res;
	}	

}
