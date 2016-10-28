package eu.europeana.annotation.definitions.model.resource.style.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class CssStyle extends BaseInternetResource implements Style{

	private String annotationClass;
	private boolean embedded;

	@Override
	public String getAnnotationClass() {
		return annotationClass;
	}

	@Override
	public void setAnnotationClass(String annotationClass) {
		this.annotationClass = annotationClass;
	}
	
	public CssStyle(){
		super();
		//setContentType(contentType);
		setMediaType("text/css");
		setLanguage("css");
	}
	
	@Override
	public boolean isEmbedded(){
		embedded = getValue()!= null && !getValue().isEmpty() && (getHttpUri() == null || getHttpUri().isEmpty());
		return embedded;
	}
}
