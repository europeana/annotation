package eu.europeana.annotation.mongo.model;

import com.google.code.morphia.annotations.Embedded;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
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

	@Override
	public void copyFrom(Object volatileObject) {
		if(volatileObject instanceof AnnotationId){
			this.setResourceId(((AnnotationId) volatileObject).getResourceId());
			this.setProvider(((AnnotationId) volatileObject).getProvider());
			this.setAnnotationNr(((AnnotationId) volatileObject).getAnnotationNr());
			
		} else
			throw new AnnotationMongoRuntimeException( volatileObject.getClass().getCanonicalName() + " does not implement the interface " + AnnotationId.class.getCanonicalName());
	}

	
	
}
