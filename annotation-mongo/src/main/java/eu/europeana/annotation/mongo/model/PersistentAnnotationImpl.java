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
	private Date annotatedAt;
	
	@Embedded
	private Agent annotatedBy;
	@Embedded
	private Body hasBody;
	@Embedded
	private Target hasTarget;
	
	private String motivatedBy;
	private Date serializedAt;
	@Embedded
	private Agent serializedBy;
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

}