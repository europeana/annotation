package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseImageAnnotation extends AbstractAnnotation implements ImageAnnotation {

	public BaseImageAnnotation(){
		super();
		setMotivation(MotivationTypes.COMMENTING.getOaType());

	} 
	
	@Override
	public String getImageUrl() {
		
		return getTarget().getHttpUri();
	}
	
	
	

	
}
