package eu.europeana.annotation.mongo.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
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
	private Long annotatedAtTs;
	
	@Embedded
	private Agent annotatedBy;
	@Embedded
	private Body hasBody;
	@Embedded
	private Target hasTarget;
	
	private String motivatedBy;
	private Long serializedAtTs;
	//TODO: check type to Agent
	private String serializedBy;
	private Style styledBy;

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
		this.annotationId = (MongoAnnotationId) annotationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getAnnotatedAtTs() {
		return annotatedAtTs;
	}

	public void setAnnotatedAtTs(Long annotatedAtTs) {
		this.annotatedAtTs = annotatedAtTs;
	}

	public Agent getAnnotatedBy() {
		return annotatedBy;
	}

	public void setAnnotatedBy(Agent annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	public Body getHasBody() {
		return hasBody;
	}

	public void setHasBody(Body hasBody) {
		this.hasBody = hasBody;
	}

	public Target getHasTarget() {
		return hasTarget;
	}

	public void setHasTarget(Target hasTarget) {
		this.hasTarget = hasTarget;
	}

	public String getMotivatedBy() {
		return motivatedBy;
	}
	
	@Override
	public MotivationTypes getMotivationType() {
		return MotivationTypes.valueOf(getMotivatedBy());
	}

	public void setMotivatedBy(String motivatedBy) {
		this.motivatedBy = motivatedBy;
	}

	public Long getSerializedAtTs() {
		return serializedAtTs;
	}

	public void setSerializedAtTs(Long serializedAtTs) {
		this.serializedAtTs = serializedAtTs;
	}

	public String getSerializedBy() {
		return serializedBy;
	}

	public void setSerializedBy(String serializedBy) {
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
		return new Date(getSerializedAtTs());
	}

	@Override
	public Date getAnnotatedAt() {
		return new Date(getAnnotatedAtTs());
	}
	
	
}