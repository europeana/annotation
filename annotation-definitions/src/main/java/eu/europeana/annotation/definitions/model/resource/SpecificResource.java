package eu.europeana.annotation.definitions.model.resource;

import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;

public interface SpecificResource extends InternetResource{

	public abstract void setStyleClass(String styleClass);

	public abstract String getStyleClass();

	public abstract void setState(State state);

	public abstract State getState();

	public abstract void setSelector(Selector selector);

	public abstract Selector getSelector();

	public abstract void setSourceResource(InternetResource source);

	public abstract InternetResource getSourceResource();

	public abstract void setSource(String source);

	public abstract String getSource();
	
	public String getInternalType();

	public void setInternalType(String internalId);
	
	public abstract void setInputString(String string);
	
	public abstract String getInputString();
	
	public abstract void setPurpose(String motivation);

	public abstract String getPurpose();
	
	public abstract void setScope(String scope);

	public abstract String getScope();

	public abstract void setRights(String rights);

	public abstract String getRights();
	
}
