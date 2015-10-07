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

	public abstract String getInternalType();

	public abstract void setSerializedBy(Agent serializedBy);

	public abstract Agent getSerializedBy();

	public abstract void setStyledBy(Style styledBy);

	public abstract Style getStyledBy();

	public abstract void setMotivation(String motivation);

	public abstract String getMotivation();

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

	public abstract void setDisabled(boolean isDisabled);

	public abstract boolean isDisabled();

	public abstract void setSameAs(String sameAs);

	public abstract String getSameAs();

	public abstract void setEquivalentTo(String equivalentTo);

	public abstract String getEquivalentTo();

	public void setInternalType(String internalType);
	
	public abstract String getStatus();
	
	public void setStatus(String status);
	
	public abstract Date getLastUpdate();

	public abstract void setLastUpdate(Date lastUpdateTimestamp);

	public boolean isPrivate();

	public void setDefaultMotivation();
	
}