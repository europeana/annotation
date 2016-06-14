package eu.europeana.annotation.definitions.model.graph.impl;

import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.resource.InternetResource;

public class RdfGraph implements Graph{

	String resourceUri;
	//aka rdf predicate
	String relationName;
	String nodeUri;
	InternetResource node;
	
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
}
