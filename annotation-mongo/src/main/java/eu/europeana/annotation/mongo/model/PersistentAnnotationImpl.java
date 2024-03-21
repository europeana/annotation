package eu.europeana.annotation.mongo.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity(AnnotationConfiguration.MONGO_COLLECTION_NAME)
@Indexes(@Index(fields=@Field(PersistentAnnotation.FIELD_IDENTIFIER)))
public class PersistentAnnotationImpl implements PersistentAnnotation, PersistentObject {

	@Id
	private ObjectId id;
	
	private long identifier;
	
	private String type;
	private String internalType;
	
	@Embedded
	private Agent creator;
	@Embedded
	private Agent generator;
	private Date created;
	private Date generated;
	
	@Embedded(PersistentAnnotation.FIELD_BODY)
	private Body body;
	@Embedded
	private Target target;
	@Embedded
	private List<Target> targets;

	@Property(PersistentAnnotation.FIELD_MOTIVATION)
	private String motivation;
	private Style styledBy;

	private Date lastIndexed;

	private Date disabled;
	private String sameAs;
	private String equivalentTo;
	
	private String canonical;
	private String[] via;
	
	private String status;
	private Date lastUpdate;
	
	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Agent getCreator() {
		return creator;
	}

	public void setCreator(Agent annotatedBy) {
		this.creator = annotatedBy;
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

	public List<Target> getTargets() {
		return targets;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}
		
	public String getMotivation() {
		return motivation;
	}
	
	@Override
	public MotivationTypes getMotivationType() {
		return MotivationTypes.getType(getMotivation());
	}

	public final void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	

	public Agent getGenerator() {
		return generator;
	}

	public void setGenerator(Agent serializedBy) {
		this.generator = serializedBy;
	}

	public Style getStyledBy() {
		return styledBy;
	}

	public void setStyledBy(Style styledBy) {
		this.styledBy = styledBy;
	}

	@Override
	public Date getGenerated() {
		return generated;
	}

	@Override
	public void setGenerated(Date serializedAt) {
		this.generated = serializedAt;
		
	}
	
	public void setSerializedAtTs(Long serializedAtTs) {
		this.generated = new Date(serializedAtTs);
	}

	@Override
	public Date getCreated() {
		return created;
	}
	
	@Override
	public void setCreated(Date annotatedAt) {
		this.created = annotatedAt;
		
	}
	
	public void setAnnotatedAtTs(Long annotatedAtTs) {
		this.created = new Date(annotatedAtTs);
	}
	
	@Override
	public Date getLastIndexed() {
		return lastIndexed;
	}

	public void setLastIndexed(Date lastIndexed) {
		this.lastIndexed = lastIndexed;
	}
	
	@Override
	public boolean isDisabled() {
		return disabled!=null;
	}

	@Override
	public void setDisabled(Date disabled) {
		this.disabled = disabled;
	}
	
	@Override
	public Date getDisabled() {
		return disabled;
	}
	
	public String toString() {
		return "PersistentAnnotation [identifier:" + identifier + ", created:" + getCreated() + 
				", Id:" + getId() + ", last update: " + getLastIndexed() + ", disabled: " + isDisabled() + "]";
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
	public final void setInternalType(String internalType) {
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
	public boolean equalsContent(Object other) {
		throw new RuntimeException("Operation not supported yet");
	}

	@Override
	public void setCanonical(String canonical) {
		this.canonical = canonical;		
	}

	@Override
	public String getCanonical() {
		return canonical;
	}

	@Override
	public void setVia(String[] via) {
		this.via = via;
	}

	@Override
	public String[] getVia() {
		return via;
	}

    public long getIdentifier() {
      return identifier;
    }

    public void setIdentifier(long identifier) {
      this.identifier = identifier;
    }
	
}