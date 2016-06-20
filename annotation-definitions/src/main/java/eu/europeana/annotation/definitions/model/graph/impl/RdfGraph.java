package eu.europeana.annotation.definitions.model.graph.impl;

import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.resource.InternetResource;

public class RdfGraph implements Graph{

	String resourceUri;
	//aka rdf predicate
	String relationName;
	String nodeUri;
	InternetResource node;
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
	public String getNodeUri() {
		return nodeUri;
	}
	@Override
	public void setNodeUri(String nodeUri) {
		this.nodeUri = nodeUri;
	}
	@Override
	public InternetResource getNode() {
		return node;
	}
	@Override
	public void setNode(InternetResource node) {
		this.node = node;
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
