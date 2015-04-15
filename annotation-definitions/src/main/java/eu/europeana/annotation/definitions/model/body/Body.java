package eu.europeana.annotation.definitions.model.body;

import java.util.Map;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.resource.InternetResource;

public interface Body extends InternetResource{

	public abstract String getBodyType();

	public abstract void setBodyType(String bodyTypeStr);

	public abstract Map<String, String> getMultilingual();

	public abstract void setMultilingual(Map<String, String> multiLingual);
	
	/**
	 * This method puts label in multilingual mapping.
	 * @param language
	 * @param label
	 */
	public void addLabelInMapping(String language, String label);
	
	public abstract Concept getConcept();
	
	public abstract void setConcept(Concept concept);
	
}
