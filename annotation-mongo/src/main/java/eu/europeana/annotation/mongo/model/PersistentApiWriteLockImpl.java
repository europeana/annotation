package eu.europeana.annotation.mongo.model;

import java.util.Date;
import org.bson.types.ObjectId;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("apiwritelock")
public class PersistentApiWriteLockImpl implements PersistentObject, PersistentApiWriteLock {

	@Id
	private ObjectId id;
	private String name;
	private Date started;
	private Date ended;
	
	public PersistentApiWriteLockImpl() {}
	
	public PersistentApiWriteLockImpl(String name) {
		this.name = name;
		this.started = new Date();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public void setStarted(Date started) {
		this.started = started;
	}

	@Override
	public Date getEnded() {
		return ended;
	}

	@Override
	public void setEnded(Date ended) {
		this.ended = ended;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "PersistentApiWriteLockImplImpl [" 
				+ "id:" + getId() + ", " 
				+ "name:" + getName() + ","
				+ "started:" + getStarted().toString() + ", " 
				+ "ended:" + getEnded().toString() + "] " ;
	}

}