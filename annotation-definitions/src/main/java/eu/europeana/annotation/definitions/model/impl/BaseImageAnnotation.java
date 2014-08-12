package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.ImageAnnotation;

public class BaseImageAnnotation extends AbstractAnnotation implements ImageAnnotation {

	public BaseImageAnnotation(){
		super();
	} 
	
	@Override
	public String getImageUrl() {
		
		return getHasTarget().getHttpUri();
	}

	

	
}
