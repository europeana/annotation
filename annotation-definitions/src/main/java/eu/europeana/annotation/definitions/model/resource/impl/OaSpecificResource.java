package eu.europeana.annotation.definitions.model.resource.impl;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;

public class OaSpecificResource extends BaseInternetResource implements SpecificResource{

	//private List<Scope> hasScope;
	private Selector selector;
	
	private InternetResource source;
	private State state;
	private String styleClass;
	
	@Override
	public Selector getSelector() {
		return selector;
	}
	@Override
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	@Override
	public InternetResource getSource() {
		return source;
	}
	@Override
	public void setSource(InternetResource source) {
		this.source = source;
	}
	
	@Override
	public State getState() {
		return state;
	}
	@Override
	public void setState(State state) {
		this.state = state;
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
