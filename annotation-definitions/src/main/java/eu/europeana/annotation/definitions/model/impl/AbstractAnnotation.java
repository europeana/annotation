package eu.europeana.annotation.definitions.model.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public abstract class AbstractAnnotation implements Annotation {

	protected AnnotationId annotationId = null;
	private String type;
	//private Long annotatedAtTs;
	private Date annotatedAt;
	private Agent annotatedBy;
	private Body body;
	private Target target;
	private String motivatedBy;
	//private Long serializedAtTs;
	private Date serializedAt;
	
	private Agent serializedBy;
	private Style styledBy;
	
	
	public AbstractAnnotation(){
		super();
	}
	
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
		return annotatedAt;
	}

	@Override
	public Agent getAnnotatedBy() {
		System.out.println("getAnnotatedBy() " + annotatedBy);
		return annotatedBy;
	}

	@Override
	public void setAnnotatedBy(Agent annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public void setBody(Body body) {
		this.body = body;
	}

	@Override
	public Target getTarget() {
		return target;
	}

	@Override
	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	public String getMotivatedBy() {
		return motivatedBy;
	}

	@Override
	public MotivationTypes getMotivationType() {
		if(getMotivatedBy() == null)
			return null;
		
		return MotivationTypes.valueOf(getMotivatedBy());		
	}
	
	@Override
	public void setMotivatedBy(String motivatedBy) {
		this.motivatedBy = motivatedBy;
	}

	@Override
	public Date getSerializedAt() {
		return serializedAt;
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
	public Agent getSerializedBy() {
		return serializedBy;
	}

	@Override
	public void setSerializedBy(Agent serializedBy) {
		this.serializedBy = serializedBy;
	}

//	@Override
//	public Long getAnnotatedAtTs() {
//		return annotatedAt.getTime();
//	}
//
//	@Override
//	public void setAnnotatedAtTs(Long annotatedAtTs) {
//		this.annotatedAt = new Date(annotatedAtTs);
//	}

	@Override
	public void setAnnotatedAt(Date annotatedAt) {
		this.annotatedAt = annotatedAt;
	}
	
	@Override
	public void setSerializedAt(Date serializedAt) {
		this.serializedAt = serializedAt;
	}
	
	public void setAnnotationId(AnnotationId annotationId) {
		this.annotationId = annotationId;
	}
	
}
