package eu.europeana.annotation.definitions.model.agent;

import java.util.List;
import java.util.Map;

public interface ExtAgent extends Agent {
	
	public Map<String, String> getPrefLabel();
	public void setPrefLabel(Map<String, String> prefLabel);
	public Map<String, String> getPlaceOfBirth();
	public void setPlaceOfBirth(Map<String, String> placeOfBirth);
	public Map<String, String> getPlaceOfDeath();
	public void setPlaceOfDeath(Map<String, String> placeOfDeath);
	public List<String> getDateOfDeath();
	public void setDateOfDeath(List<String> dateOfDeath);
	public List<String> getDateOfBirth();
	public void setDateOfBirth(List<String> dateOfBirth);

}
