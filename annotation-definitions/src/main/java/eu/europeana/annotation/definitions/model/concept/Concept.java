package eu.europeana.annotation.definitions.model.concept;

import java.util.List;
import java.util.Map;

/**
 * This interface defines method for SKOS basic concepts.
 */
public interface Concept {

	public abstract Map<String, String> getPrefLabel();

	public abstract void setPrefLabel(Map<String, String> prefLabel);

	public abstract void addPrefLabelInMapping(String id, String label);

	public abstract void setAltLabel(Map<String, String> altLabel);
	
	public abstract void addAltLabelInMapping(String id, String label);

	public abstract Map<String, String> getAltLabel();

	public abstract void setHiddenLabel(Map<String, String> hiddenLabel);
	
	public abstract void addHiddenLabelInMapping(String id, String label);

	public abstract Map<String, String> getHiddenLabel();
	    
	public abstract void addNotation(String newNotation);
	
	public abstract List<String> getNotation();
	
	public abstract void setNotation(List<String> notationList);

	public abstract void addNarrower(String newNarrower);
	
	public abstract List<String> getNarrower();

	public abstract void setNarrower(List<String> narrowerList);

	public abstract void addBroader(String newbroader);
	
	public abstract List<String> getBroader();
	
	public abstract void setBroader(List<String> broaderList);

	public abstract void addRelated(String newRelated);
	
	public abstract List<String> getRelated();
	
	public abstract void setRelated(List<String> relatedList);
	
}
