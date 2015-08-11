package eu.europeana.annotation.definitions.model.resource.style.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class BaseStyle extends BaseInternetResource implements Style {

//	private String styleType;
//	
//	@Override
//	public String getStyleType() {
//		return styleType;
//	}
//	protected void setStyleTypeEnum(StyleTypes bodyType) {
//		this.styleType = styleType.name();
//	}
//	@Override
//	public void setStyleType(String styleTypeStr) {
//		this.styleType = styleTypeStr;
//	}
	
	public boolean isEmbedded() {
		return false;
	}

	public void setAnnotationClass(String annotationClass) {
	}

	public String getAnnotationClass() {
		return null;
	}

}
