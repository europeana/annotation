package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.api.commons.nosql.entity.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentProvider) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 */
public interface PersistentProvider extends Provider, NoSqlEntity {

	public final static String FIELD_NAME           = "name";
	public final static String FIELD_ID_GENERATION  = "idGeneration";

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
