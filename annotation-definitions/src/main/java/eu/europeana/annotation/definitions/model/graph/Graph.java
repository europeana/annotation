package eu.europeana.annotation.definitions.model.graph;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Graph {

	void setNode(InternetResource node);

	InternetResource getNode();

	void setNodeUri(String nodeUri);

	String getNodeUri();

	void setRelationName(String relationName);

	String getRelationName();

	void setResourceUri(String resourceUri);

	String getResourceUri();

}
