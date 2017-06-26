package eu.europeana.annotation.mongo.model.internal;

import java.util.Date;

import org.bson.types.ObjectId;

import eu.europeana.api.commons.nosql.entity.NoSqlEntity;

public interface PersistentApiWriteLock extends NoSqlEntity {	
	
	public ObjectId getId();

	public String getName();

	public void setName(String name);
	
	public void setId(ObjectId id);

	public void setStarted(Date started);

	public Date getStarted();

	public Date getEnded();

	public void setEnded(Date ended);

}
