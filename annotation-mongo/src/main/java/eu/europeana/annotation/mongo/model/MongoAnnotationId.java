package eu.europeana.annotation.mongo.model;

import com.google.code.morphia.annotations.Embedded;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Embedded
/**
 * This class is used to map the AnnotationId to native Mongo object using morphia OM
 * @author GrafR
 *
 */
public class MongoAnnotationId extends BaseAnnotationId implements PersistentObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -145869923290638714L;
	
	public MongoAnnotationId(){
		super();
	}
	
	public MongoAnnotationId(AnnotationId volatileObject){
		super();
		copyFrom(volatileObject);
		setHttpUrl(toHttpUrl());
	}
}
