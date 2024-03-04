package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.TranscriptionAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * base POJO for Translation annotations
 */
public class BaseTranslationAnnotation extends AbstractAnnotation implements TranscriptionAnnotation {

  /**
   * Default constructor which sets the motivation to translating	
   */
  public BaseTranslationAnnotation(){
		super();
		motivation = MotivationTypes.TRANSLATING.getOaType();
	} 
	
}
