package eu.europeana.annotation.mongo.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import eu.europeana.annotation.definitions.model.authentication.Application;
import eu.europeana.annotation.definitions.model.authentication.impl.BaseClientImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("client")
public class PersistentClientImpl extends BaseClientImpl implements PersistentClient, PersistentObject {

	@Embedded
	Application clientApplication;

	@Id
	private ObjectId id;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public void setClientApplication(Application clientApplication) {
		this.clientApplication = clientApplication;
	}
	public Application getClientApplication() {
		return clientApplication;
	}
	
	public String toString() {
		return "PersistentClientImpl [" 
				+ "Id:" + getId() + ", " 
	            + "clientId:" + getClientId() + ", " 
	            + "authenticationConfigJson:" + getAuthenticationConfigJson() + "]";
	}


}