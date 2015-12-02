package eu.europeana.annotation.mongo.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.mongo.model.internal.PersistentObject;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;

@Entity("whitelist")
@Polymorphic
public class PersistentWhitelistImpl implements PersistentWhitelistEntry, PersistentObject {

	@Id
	private ObjectId id;
	
	private String name;
	private String httpUrl;
	private String status;
	private Date creationDate;
	private Date lastUpdate;
	private Date enableFrom;
	private Date disableTo;

	
	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	@Override
	public void setLastUpdate(Date lastUpdateTimestamp) {
		this.lastUpdate = lastUpdateTimestamp;
	}

	@Override
	public Date getCreationDate() {
		return this.creationDate;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Date getEnableFrom() {
		return this.enableFrom;
	}

	@Override
	public void setEnableFrom(Date enableFromTimestamp) {
		this.enableFrom = enableFromTimestamp;
	}

	@Override
	public Date getDisableTo() {
		return this.disableTo;
	}

	@Override
	public void setDisableTo(Date disableToTimestamp) {
		this.disableTo = disableToTimestamp;
	}

	public String toString() {
		return "PersistentWhitelist [" 
				+ "Id:" + getId() + ", " 
	            + "name:" + getName() + ", " 
	            + "url:" + getHttpUrl() + ", " 
				+ ", last update: " + getLastUpdate() + "]";
	}


}