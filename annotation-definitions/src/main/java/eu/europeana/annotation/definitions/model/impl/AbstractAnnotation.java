package eu.europeana.annotation.definitions.model.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public abstract class AbstractAnnotation implements Annotation{

	protected AnnotationId annotationId = null;
	private String type;
	private Long annotatedAtTs;
	private Agent annotatedBy;
	private Body hasBody;
	private Target hasTarget;
	private String motivatedBy;
	private Long serializedAtTs;
	//TODO: check type to Agent
	private String serializedBy;
	private Style styledBy;
	
	
	@Override
	public AnnotationId getAnnotationId() {
		return annotationId;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
		
	}

	@Override
	public Date getAnnotatedAt() {
		return new Date(getAnnotatedAtTs());
	}

	@Override
	public Agent getAnnotatedBy() {
		return annotatedBy;
	}

	@Override
	public void setAnnotatedBy(Agent annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	@Override
	public Body getHasBody() {
		return hasBody;
	}

	@Override
	public void setHasBody(Body hasBody) {
		this.hasBody = hasBody;
	}

	@Override
	public Target getHasTarget() {
		return hasTarget;
	}

	@Override
	public void setHasTarget(Target hasTarget) {
		this.hasTarget = hasTarget;
	}

	@Override
	public String getMotivatedBy() {
		return motivatedBy;
	}

	@Override
	public MotivationTypes getMotivationType() {
		return MotivationTypes.valueOf(getMotivatedBy());
	}
	
	@Override
	public void setMotivatedBy(String motivatedBy) {
		this.motivatedBy = motivatedBy;
	}

	@Override
	public Date getSerializedAt() {
		return new Date(getSerializedAtTs());
	}

	
	@Override
	public Style getStyledBy() {
		return styledBy;
	}

	@Override
	public void setStyledBy(Style styledBy) {
		this.styledBy = styledBy;
	}

	@Override
	public String getSerializedBy() {
		return serializedBy;
	}

	@Override
	public void setSerializedBy(String serializedBy) {
		this.serializedBy = serializedBy;
	}

	@Override
	public Long getAnnotatedAtTs() {
		return annotatedAtTs;
	}

	@Override
	public void setAnnotatedAtTs(Long annotatedAtTs) {
		this.annotatedAtTs = annotatedAtTs;
	}

	@Override
	public Long getSerializedAtTs() {
		return serializedAtTs;
	}

	@Override
	public void setSerializedAtTs(Long serializedAtTs) {
		this.serializedAtTs = serializedAtTs;
	}
	
}
