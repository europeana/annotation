package eu.europeana.annotation.mongo.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;
//import com.google.code.morphia.annotations.Reference;


import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("annotation")
@Polymorphic
//@Indexes( @Index("europeanaId, annotationNr"))
public class PersistentAnnotationImpl implements PersistentAnnotation, PersistentObject {

	@Id
	private ObjectId id;
	
	@Embedded
	private MongoAnnotationId annotationId;
	
	private String type;
	private String internalType;
	private Date annotatedAt;
	
	@Embedded
	private Agent annotatedBy;
	@Embedded
	private Body body;
	@Embedded
//	@Reference(lazy=true)
	private Target target;
	
	private String motivation;
	private Date serializedAt;
	@Embedded
	private Agent serializedBy;
	private Style styledBy;

	private Long lastIndexedTimestamp;
	
	private boolean disabled;
	private String sameAs;
	private String equivalentTo;
	
	private String status;
	private Date lastUpdate;

	
	@Override
	public void copyFrom(Object volatileObject) {
		// TODO Auto-generated method stub
	}

	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public AnnotationId getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(AnnotationId annotationId) {
		if(annotationId instanceof MongoAnnotationId)
			this.annotationId = (MongoAnnotationId) annotationId;
		else{
			MongoAnnotationId id = new MongoAnnotationId();
			id.copyFrom(annotationId);
			this.annotationId = id;
		}
			
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Agent getAnnotatedBy() {
		return annotatedBy;
	}

	public void setAnnotatedBy(Agent annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public String getMotivation() {
		return motivation;
	}
	
	@Override
	public MotivationTypes getMotivationType() {
		return MotivationTypes.getType(getMotivation());
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	

	public Agent getSerializedBy() {
		return serializedBy;
	}

	public void setSerializedBy(Agent serializedBy) {
		this.serializedBy = serializedBy;
	}

	public Style getStyledBy() {
		return styledBy;
	}

	public void setStyledBy(Style styledBy) {
		this.styledBy = styledBy;
	}

	@Override
	public Date getSerializedAt() {
		return serializedAt;
	}

	@Override
	public void setSerializedAt(Date serializedAt) {
		this.serializedAt = serializedAt;
		
	}
	
	public void setSerializedAtTs(Long serializedAtTs) {
		this.serializedAt = new Date(serializedAtTs);
	}

	@Override
	public Date getAnnotatedAt() {
		return annotatedAt;
	}
	
	@Override
	public void setAnnotatedAt(Date annotatedAt) {
		this.annotatedAt = annotatedAt;
		
	}
	
	public void setAnnotatedAtTs(Long annotatedAtTs) {
		this.annotatedAt = new Date(annotatedAtTs);
	}

    @Override
	public void setLastIndexedTimestamp(Long lastIndexedTimestamp) {
		this.lastIndexedTimestamp = lastIndexedTimestamp;
	}
	
	public Long getLastIndexedTimestamp() {
		return lastIndexedTimestamp;
	}
	
	@Override
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public String toString() {
		return "PersistentAnnotation [AnnotationId:" + getAnnotationId() + ", annotatedAt:" + getAnnotatedAt() + 
				", Id:" + getId() + ", last update: " + getLastIndexedTimestamp() + ", disabled: " + isDisabled() + "]";
	}

	@Override
	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}

	@Override
	public String getSameAs() {
		return sameAs;
	}
			
	@Override
	public void setEquivalentTo(String equivalentTo) {
		this.equivalentTo = equivalentTo;
	}

	@Override
	public String getEquivalentTo() {
		return equivalentTo;
	}

	@Override
	public String getInternalType() {
		return internalType;
	}

	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}
			
	@Override
	public Date getLastUpdate(){
		return this.lastUpdate;
	}

	@Override
	public void setLastUpdate(Date lastUpdateTimestamp){
		this.lastUpdate = lastUpdateTimestamp;
	}

	@Override
	public boolean isPrivate() {
		//TODO: change the usage of status to the usage of visibility when the specification is complete
		return AnnotationStates.PRIVATE.equals(getStatus());
	}

	@Override
	public void setDefaultMotivation() {
		//do nothing, must be implemented in subclasses
		
	}
}