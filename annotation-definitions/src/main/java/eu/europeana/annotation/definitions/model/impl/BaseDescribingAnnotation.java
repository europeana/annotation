package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.DescribingAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseDescribingAnnotation extends AbstractAnnotation implements DescribingAnnotation {

	public BaseDescribingAnnotation(){
		super();
		motivation =MotivationTypes.DESCRIBING.getOaType();

	} 
	
}
