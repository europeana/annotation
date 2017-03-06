package eu.europeana.annotation.mongo.model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.authentication.impl.BaseClientImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("client")
@Polymorphic
public class PersistentClientImpl extends BaseClientImpl implements PersistentClient, PersistentObject {

	@Id
	private ObjectId id;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String toString() {
		return "PersistentClientImpl [" 
				+ "Id:" + getId() + ", " 
	            + "clientId:" + getClientId() + ", " 
	            + "authenticationConfigJson:" + getAuthenticationConfigJson() + "]";
	}


}