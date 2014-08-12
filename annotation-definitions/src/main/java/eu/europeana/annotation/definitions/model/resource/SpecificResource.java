package eu.europeana.annotation.definitions.model.resource;

import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;

public interface SpecificResource extends InternetResource{

	public abstract void setStyleClass(String styleClass);

	public abstract String getStyleClass();

	public abstract void setHasState(State hasState);

	public abstract State getHasState();

	public abstract void setHasSelector(Selector hasSelector);

	public abstract Selector getHasSelector();

	
}
