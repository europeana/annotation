package eu.europeana.annotation.definitions.model.resource.impl;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;

public class BaseSpecificResource extends BaseInternetResource implements SpecificResource{

	//private List<Scope> hasScope;
	private Selector selector;
	
	private String sourceUri;
	private InternetResource source;
	private State state;
	private String styleClass;
	protected String purpose;
	
	public String getSource() {
		return sourceUri;
	}
	public void setSource(String sourceUri) {
		this.sourceUri = sourceUri;
	}
	
	@Override
	public Selector getSelector() {
		return selector;
	}
	@Override
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	@Override
	public InternetResource getSourceResource() {
		return source;
	}
	@Override
	public void setSourceResource(InternetResource source) {
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
	
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String role) {
		this.purpose = role;
	}
}
