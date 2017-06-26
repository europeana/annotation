package eu.europeana.annotation.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.api.commons.nosql.entity.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentModerationRecord) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 */
public interface PersistentModerationRecord extends ModerationRecord, NoSqlEntity {

	public final static String FIELD_BASEURL = "annotationId.baseUrl";
	public final static String FIELD_PROVIDER = "annotationId.provider";
	public final static String FIELD_IDENTIFIER = "annotationId.identifier";
	
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
