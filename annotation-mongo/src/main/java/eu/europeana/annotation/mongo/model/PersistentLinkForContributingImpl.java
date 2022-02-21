package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentLinkForContributingImpl extends PersistentAnnotationImpl implements ObjectTag{
  
	public PersistentLinkForContributingImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_LINK_FOR_CONTRIBUTING.name());
		setMotivation(MotivationTypes.LINKFORCONTRIBUTING.getOaType());
	}
	
}
