package eu.europeana.annotation.definitions.model.resource.style.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class BaseStyle extends BaseInternetResource implements Style {

	public boolean isEmbedded() {
		return false;
	}

	public void setAnnotationClass(String annotationClass) {
	}

	public String getAnnotationClass() {
		return null;
	}

}
