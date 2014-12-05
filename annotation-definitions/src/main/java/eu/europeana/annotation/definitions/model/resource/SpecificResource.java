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

	public abstract void setSource(InternetResource source);

	public abstract InternetResource getSource();
		
}
