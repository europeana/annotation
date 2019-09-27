package eu.europeana.annotation.definitions.model.body.impl;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class AgentBody extends BaseBody implements Body {

	public Map<String, String> prefLabel;
	public Map<String, String> placeOfBirth;
	public Map<String, String> placeOfDeath;
	public List<String> dateOfBirth;
	public List<String> dateOfDeath;

	public AgentBody(){
		super();
		setInternalType(BodyInternalTypes.ENTITY.name());
	}

	public Map<String, String> getPrefLabel() {
	  	return prefLabel;
	}

	public void setPrefLabel(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	public Map<String, String> getPlaceOfBirth() {
	  	return placeOfBirth;
	}

	public void setPlaceOfBirth(Map<String, String> placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	
	public Map<String, String> getPlaceOfDeath() {
	  	return placeOfDeath;
	}

	public void setPlaceOfDeath(Map<String, String> placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}
	
	public List<String> getDateOfDeath() {
	  	return dateOfDeath;
	}

	public void setDateOfDeath(List<String> dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	
	public List<String> getDateOfBirth() {
	  	return dateOfBirth;
	}

	public void setDateOfBirth(List<String> dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
}
