package eu.europeana.annotation.definitions.model;

import java.io.Serializable;

public interface AnnotationId extends Serializable {

	public abstract String getEuropeanaId();

	public abstract void setAnnotationNr(Integer nr);

	public abstract Integer getAnnotationNr();

	
}
