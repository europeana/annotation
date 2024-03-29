package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectCommentImpl extends PersistentAnnotationImpl implements ObjectTag{
	public PersistentObjectCommentImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_COMMENT.name());
		setMotivation(MotivationTypes.COMMENTING.getOaType());

	}
}
