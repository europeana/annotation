package eu.europeana.annotation.definitions.model.graph;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Graph {

	public void setLinkedResource(InternetResource node);

	public InternetResource getLinkedResource();

	public void setLinkedResourceUri(String linkedResourceUri);

	public String getLinkedResourceUri();

	public void setRelationName(String relationName);

	public String getRelationName();

	public void setResourceUri(String resourceUri);

	public String getResourceUri();

	public void setContext(String context);

	public String getContext();

}
