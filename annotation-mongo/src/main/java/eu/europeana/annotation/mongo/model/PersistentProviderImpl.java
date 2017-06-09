package eu.europeana.annotation.mongo.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;
import eu.europeana.annotation.mongo.model.internal.PersistentProvider;

@Entity("provider")
@Polymorphic
public class PersistentProviderImpl implements PersistentProvider, PersistentObject {

	@Id
	private ObjectId id;
	
	private String name;
	private String uri;
	private String idGeneration;
	private Long lastIndexedTimestamp;

	
	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

    @Override
	public void setLastIndexedTimestamp(Long lastIndexedTimestamp) {
		this.lastIndexedTimestamp = lastIndexedTimestamp;
	}
	
	public Long getLastIndexedTimestamp() {
		return lastIndexedTimestamp;
	}
	
	public String toString() {
		return "PersistentProvider [name:" + getName() + ", uri:" + getUri() + 
				", idGeneration:" + getIdGeneration() + ", last update: " + getLastIndexedTimestamp() + "]";
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String getUri() {
		return this.uri;
	}

	@Override
	public void setIdGeneration(IdGenerationTypes idGeneration) {
		this.idGeneration = idGeneration.getIdType();
	}

	@Override
	public String getIdGeneration() {
		return this.idGeneration;
	}
			
}