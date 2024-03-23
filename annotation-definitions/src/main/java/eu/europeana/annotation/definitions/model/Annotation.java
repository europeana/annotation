package eu.europeana.annotation.definitions.model;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public interface Annotation {
  
    public void setIdentifier(long identifier);

    public long getIdentifier();

	public abstract String getType();

	public abstract String getInternalType();

	public abstract void setStyledBy(Style styledBy);

	public abstract Style getStyledBy();

	public abstract void setMotivation(String motivation);

	public abstract String getMotivation();

	public abstract MotivationTypes getMotivationType();

	public abstract void setTarget(List<Target> target);

	public abstract List<Target> getTarget();
	
	public abstract void setBody(Body body);

	public abstract Body getBody();

	public abstract void setCreator(Agent annotatedBy);

	public abstract Agent getCreator();

	public abstract void setGenerator(Agent serializedBy);

	public abstract Agent getGenerator();

	public abstract Date getCreated();
	
	public abstract Date getGenerated();

	void setGenerated(Date generated);

	void setCreated(Date created);
	
	public abstract void setType(String type);

	public abstract void setDisabled(Date disabled);
	
	public abstract Date getDisabled();

	public abstract boolean isDisabled();
	
	/**
	 *  @return
	 */
	public abstract void setSameAs(String sameAs);
	/**
	 * @return
	 */
	public abstract String getSameAs();

	/**
	 * @deprecated
	 * @return
	 */
	public abstract void setEquivalentTo(String equivalentTo);

	/**
	 * @deprecated
	 * @return
	 */
	public abstract String getEquivalentTo();

	public void setInternalType(String internalType);
	
	public abstract String getStatus();
	
	public void setStatus(String status);
	
	public abstract Date getLastUpdate();

	public abstract void setLastUpdate(Date lastUpdate);

	public boolean isPrivate();
	
	boolean equalsContent(Object other);
	
	public abstract void setCanonical(String canonical);
	
	public abstract String getCanonical();
	
	public abstract void setVia(String[] via);
	
	public abstract String[] getVia();
	
}