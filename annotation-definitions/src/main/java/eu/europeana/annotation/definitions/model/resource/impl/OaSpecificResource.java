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
	
	@Override
	public Selector getHasSelector() {
		return hasSelector;
	}
	@Override
	public void setHasSelector(Selector hasSelector) {
		this.hasSelector = hasSelector;
	}
	@Override
	public State getHasState() {
		return hasState;
	}
	@Override
	public void setHasState(State hasState) {
		this.hasState = hasState;
	}
	@Override
	public String getStyleClass() {
		return styleClass;
	}
	@Override
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
