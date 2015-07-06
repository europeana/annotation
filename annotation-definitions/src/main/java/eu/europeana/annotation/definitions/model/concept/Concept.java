package eu.europeana.annotation.definitions.model.concept;

import java.util.List;
import java.util.Map;

/**
 * This interface defines method for SKOS basic concepts.
 */
public interface Concept {

	public String getUri();
	
	public void setUri(String uri);

	public List<String> getType();

	public void setType(List<String> type);
	
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
	
	public abstract void addScopeNoteInMapping(String id, String label);

	public abstract void addDefinitionInMapping(String id, String label);

	public abstract void addExampleInMapping(String id, String label);

	public abstract void addHistoryNoteInMapping(String id, String label);

	public abstract void addEditorialNoteInMapping(String id, String label);

	public abstract void addChangeNoteInMapping(String id, String label);

	public abstract void addNoteInMapping(String id, String label);

	public abstract void addSemanticRelationInMapping(String id, String label);

	public abstract void addBroaderTransitiveInMapping(String id, String label);

	public abstract void addNarrowerTransitiveInMapping(String id, String label);

	public abstract void addMappingRelationInMapping(String id, String label);

	public abstract void addBroadMatchInMapping(String id, String label);

	public abstract void addNarrowMatchInMapping(String id, String label);

	public abstract void addExactMatchInMapping(String id, String label);

	public abstract void addCloseMatchInMapping(String id, String label);

	public abstract void addRelateMatchInMapping(String id, String label);
	
	public abstract String getContext();

	public abstract String getVocabulary();
	
	public abstract List<String> getAncestors();

	public abstract List<String> getInScheme();

	public abstract List<String> getTopConceptOf();

	public abstract Map<String, String> getScopeNote();

	public abstract Map<String, String> getDefinition();

	public abstract Map<String, String> getExample();

	public abstract Map<String, String> getHistoryNote();

	public abstract Map<String, String> getEditorialNote();

	public abstract Map<String, String> getChangeNote();

	public abstract Map<String, String> getNote();

	public abstract Map<String, String> getSemanticRelation();

	public abstract Map<String, String> getBroaderTransitive();

	public abstract Map<String, String> getNarrowerTransitive();

	public abstract Map<String, String> getMember();

	public abstract Map<String, String> getMappingRelation();

	public abstract Map<String, String> getBroadMatch();

	public abstract Map<String, String> getNarrowMatch();

	public abstract Map<String, String> getRelateMatch();

	public abstract Map<String, String> getExactMatch();

	public abstract Map<String, String> getCloseMatch();

	public abstract List<String> getHasTopConcept();

	public abstract List<String> getMemberList();

}
