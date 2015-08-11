package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentStatusLog) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 */
public interface PersistentStatusLog extends StatusLog, NoSqlEntity {

	public final static String FIELD_PROVIDER = "annotationId.provider";
	public final static String FIELD_ANNOTATION_NR = "annotationId.annotationNr";
	public final static String FIELD_STATUS = "status";
	
	
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

	public abstract void setLastIndexedTimestamp(Long lastIndexedTimestamp);

	public abstract Long getLastIndexedTimestamp();
	
}
