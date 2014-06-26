package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentAnnotation) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 *   
 * @author Sergiu Gordea 
 *
 */
public interface PersistentAnnotation extends Annotation, NoSqlEntity {

	/**
	 * The method used internally to set the generated Annotation Ids
	 * @param annotationId
	 */
	public void setAnnotationId(AnnotationId annotationId);

	public final static String FIELD_EUROPEANA_ID = "annotationId.resourceId";
	public final static String FIELD_ANNOTATION_NR = "annotationId.annotationNr";
	
	
	/**
	 * 
	 * @return the generated mongo id
	 */
	public ObjectId getId();

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

}
