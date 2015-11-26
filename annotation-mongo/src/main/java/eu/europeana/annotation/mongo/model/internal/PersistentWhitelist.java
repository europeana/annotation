package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentWhitelist) 
 * with the NoSql based implementation (NoSqlEntity) and provides additional methods used internally by 
 * the service implementation
 */
public interface PersistentWhitelist extends Whitelist, NoSqlEntity {

	public final static String FIELD_URI = "uri";
	
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

}
