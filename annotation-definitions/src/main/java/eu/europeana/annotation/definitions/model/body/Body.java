package eu.europeana.annotation.definitions.model.body;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public interface Body extends InternetResource{

//	public abstract String getBodyType();
//
//	public abstract void setBodyType(String bodyTypeStr);

	public abstract void setType(List<String> bodyType);

	public abstract void addType(String newType);

	public abstract void setTypeEnum(BodyTypes agentType);

	public abstract List<String> getType();
	
//	public String getTypeStr();
	
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
	
	public abstract void setInputString(String string);
	
	public abstract String getInputString();
	
	public String getInternalType();

	public void setInternalType(String internalType);
	
}
