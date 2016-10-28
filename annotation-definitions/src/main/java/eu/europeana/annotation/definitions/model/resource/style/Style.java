package eu.europeana.annotation.definitions.model.resource.style;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Style extends InternetResource{

	public abstract boolean isEmbedded();

	public abstract void setAnnotationClass(String annotationClass);

	public abstract String getAnnotationClass();
	
	public boolean equalsContent(Object other);	
}
