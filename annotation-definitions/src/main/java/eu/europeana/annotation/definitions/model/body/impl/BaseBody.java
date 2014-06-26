package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public abstract class BaseBody extends BaseInternetResource implements Body {
	private String bodyType;
	
	@Override
	public String getBodyType() {
		return bodyType;
	}
	protected void setBodyType(BodyTypes bodyType) {
		this.bodyType = bodyType.name();
	}
	protected void setBodyType(String bodyTypeStr) {
		this.bodyType = bodyTypeStr;
	}
	protected BaseBody(){} 
}
