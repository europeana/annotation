package eu.europeana.annotation.definitions.model.resource.impl;

import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;

public class OaSpecificResource extends BaseInternetResource implements SpecificResource{

	//private List<Scope> hasScope;
	private Selector hasSelector;
	//private Source hasSource;
	private State hasState;
	private String styleClass;
	
	public Selector getHasSelector() {
		return hasSelector;
	}
	public void setHasSelector(Selector hasSelector) {
		this.hasSelector = hasSelector;
	}
	public State getHasState() {
		return hasState;
	}
	public void setHasState(State hasState) {
		this.hasState = hasState;
	}
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
