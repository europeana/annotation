package eu.europeana.annotation.definitions.model.body;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Body extends InternetResource{

	public abstract String getBodyType();

	public abstract void setBodyType(String bodyTypeStr);

}
