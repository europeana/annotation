package eu.europeana.annotation.definitions.model;

import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;

public interface Provider {

	public void setName(String name);
	
	public String getName();

	public void setUri(String uri);
	
	public String getUri();

	public void setIdGeneration(IdGenerationTypes idGeneration);
	
	public String getIdGeneration();
	
}