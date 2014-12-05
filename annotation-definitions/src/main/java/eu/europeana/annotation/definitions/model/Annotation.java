package eu.europeana.annotation.definitions.model;

import java.util.Date;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public interface Annotation {

	public void setAnnotationId(AnnotationId annotationId);
	
	public AnnotationId getAnnotationId();

	public abstract String getType();

	public abstract void setSerializedBy(Agent serializedBy);

	public abstract Agent getSerializedBy();

	public abstract void setStyledBy(Style styledBy);

	public abstract Style getStyledBy();

	public abstract void setMotivatedBy(String motivatedBy);

	public abstract String getMotivatedBy();

	public abstract MotivationTypes getMotivationType();

	public abstract void setTarget(Target target);

	public abstract Target getTarget();

	public abstract void setBody(Body body);

	public abstract Body getBody();

	public abstract void setAnnotatedBy(Agent annotatedBy);

	public abstract Agent getAnnotatedBy();

	public abstract Date getAnnotatedAt();
	
	public abstract Date getSerializedAt();

	void setSerializedAt(Date serializedAt);

	void setAnnotatedAt(Date annotatedAt);
	
	public abstract void setType(String type);

	//public abstract void setType(String type);

//	public abstract Integer getAnnotationNr();

	//
	// public abstract void setAnnotationNr(String nr);

//	public abstract String getEuropeanaId();
//
//	public abstract void setEuropeanaId(String europeanaId);

//	public abstract String getCreator();
//
//	public abstract void setCreator(String creator);

}