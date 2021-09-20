package eu.europeana.annotation.mongo.model.internal;

import java.util.Date;

import org.bson.types.ObjectId;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.api.commons.nosql.entity.NoSqlEntity;

/**
 * This type is used as internal interface that binds the external interface (model.PersistentAnnotation) with the NoSql based implementation (NoSqlEntity)
 * and provides additional methods used internally by the service implementation
 *   
 * @author Sergiu Gordea 
 *
 */
public interface PersistentAnnotation extends Annotation, NoSqlEntity {

	public final static String FIELD_BASEURL = "annotationId.baseUrl";
	public final static String FIELD_PROVIDER = "annotationId.provider";
	public final static String FIELD_IDENTIFIER = "annotationId.identifier";
	public final static String FIELD_HTTPURL = "annotationId.httpUrl";
	public final static String FIELD_DISABLED = "disabled";
	public final static String FIELD_VALUE = "value";
	public final static String FIELD_VALUES = "values";
	public final static String FIELD_RESOURCE_IDS = "resourceIds";
	public final static String FIELD_TARGET = "target.";
	public final static String FIELD_MOTIVATION = "motivation";
	public final static String FIELD_BODY = "body";
	public final static String FIELD_INTERNAL_TYPE = "internalType";
	public final static String FIELDS_SEPARATOR_MONGO = ".";
	
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

	
	public abstract void setLastIndexed(Date lastIndexedDate);

	public abstract Date getLastIndexed();
	
}
