package eu.europeana.annotation.definitions.model.graph.impl;

import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.resource.InternetResource;

public class RdfGraph implements Graph{

	String resourceUri;
	//aka rdf predicate
	String relationName;
	String linkedResourceUri;
	InternetResource linkedResource;
	String context;
	
	@Override
	public String getResourceUri() {
		return resourceUri;
	}
	@Override
	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}
	@Override
	public String getRelationName() {
		return relationName;
	}
	@Override
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	@Override
	public String getLinkedResourceUri() {
		return linkedResourceUri;
	}
	@Override
	public void setLinkedResourceUri(String nodeUri) {
		this.linkedResourceUri = nodeUri;
	}
	@Override
	public InternetResource getLinkedResource() {
		return linkedResource;
	}
	@Override
	public void setLinkedResource(InternetResource node) {
		this.linkedResource = node;
	}
	@Override
	public String getContext() {
		return context;
	}
	@Override
	public void setContext(String context) {
		this.context = context;
	}
}
