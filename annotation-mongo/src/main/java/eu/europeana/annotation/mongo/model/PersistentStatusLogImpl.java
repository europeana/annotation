package eu.europeana.annotation.mongo.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;
import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;

@Entity("statusLog")
public class PersistentStatusLogImpl implements PersistentStatusLog, PersistentObject {

	@Id
	private ObjectId id;
	
	private String user;
	private String status;
	private long identifier;
	private long date;

	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String toString() {
		return "PersistentStatusLog [" + "user:" + getUser() 
				+ "Id:" + getId() 
				+ ", status:" + getStatus()
				+ ", identifier:" + String.valueOf(identifier) 
				+ ", date: " + getDate() + "]";
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public long getDate() {
		return date;
	}

	@Override
	public void setLastIndexedTimestamp(Long lastIndexedTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getLastIndexedTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

    public long getIdentifier() {
      return identifier;
    }

    public void setIdentifier(long identifier) {
      this.identifier = identifier;
    }
}