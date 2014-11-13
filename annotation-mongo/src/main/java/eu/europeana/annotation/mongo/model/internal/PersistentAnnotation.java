package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentAnnotation) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 *   
 * @author Sergiu Gordea 
 *
 */
public interface PersistentAnnotation extends Annotation, NoSqlEntity {

	public final static String FIELD_EUROPEANA_ID = "annotationId.resourceId";
	public final static String FIELD_ANNOTATION_NR = "annotationId.annotationNr";
	
	
	/**
	 * 
	 * @return the generated mongo id
	 */
	public ObjectId getId();
	
	/**
	 * This method is necessary for the update
	 * @param id
	 */
	public void setId(ObjectId id);

//	public abstract Long getCreationTimestamp();
//
//	public abstract void setCreationTimestamp(Long creationTimestamp);
//
//	public abstract Long getLastUpdateTimestamp();
//
//	public abstract void setLastUpdateTimestamp(Long lastUpdateTimestamp);
//	
//	public abstract String[] getVisibility();
//
//	public abstract void setVisibility(String[] visibility);

	public abstract void setLastIndexedTimestamp(Long lastIndexedTimestamp);

	public abstract Long getLastIndexedTimestamp();
	
}
