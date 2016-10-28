package eu.europeana.annotation.mongo.model.internal;

import java.util.List;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentConcept) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 */
public interface PersistentConcept extends Concept, NoSqlEntity {

	public final static String FIELD_URI           = "uri";

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
	
	public String getUri();
	
	public void setUri(String uri);

	public List<String> getType();

	public void setType(List<String> type);
	
}
