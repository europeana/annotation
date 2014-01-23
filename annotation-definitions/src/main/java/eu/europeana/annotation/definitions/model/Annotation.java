package eu.europeana.annotation.definitions.model;

public interface Annotation {

	public AnnotationId getAnnotationId();

	public abstract String getType();

	//public abstract void setType(String type);

	public abstract Integer getAnnotationNr();

	//
	// public abstract void setAnnotationNr(String nr);

	public abstract String getEuropeanaId();

	public abstract void setEuropeanaId(String europeanaId);

	public abstract String getCreator();

	public abstract void setCreator(String creator);

}