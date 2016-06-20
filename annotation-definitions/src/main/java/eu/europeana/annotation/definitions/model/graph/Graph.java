package eu.europeana.annotation.definitions.model.graph;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Graph {

	public void setNode(InternetResource node);

	public InternetResource getNode();

	public void setNodeUri(String nodeUri);

	public String getNodeUri();

	public void setRelationName(String relationName);

	public String getRelationName();

	public void setResourceUri(String resourceUri);

	public String getResourceUri();

	public void setContext(String context);

	public String getContext();

}
