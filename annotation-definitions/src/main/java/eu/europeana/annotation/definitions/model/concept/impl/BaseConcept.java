package eu.europeana.annotation.definitions.model.concept.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;

/**
 * This is a class for Concept object that is a part of the Body object.
 * The sample SKOS presentation of this object is:
 * 
 *	    "notation": "skos:notation",
 *	    "prefLabel": {
 *	        "@id": "skos:prefLabel",
 *	        "@container": "@language"
 *	    },
 *	    "altLabel": {
 *	        "@id": "skos:altLabel",
 *	        "@container": "@language"
 *	    },
 *	    "hiddenLabel": {
 *	        "@id": "skos:altLabel",
 *	        "@container": "@language"
 *	    },
 *	    "narrower": "skos:narrower",
 *	    "broader": "skos:broader",
 *	    "related": "skos:related",
 */
public class BaseConcept implements Concept {

	private String uri;
	private List<String> type;
	Map<String, String> prefLabel;
	Map<String, String> altLabel;
	Map<String, String> hiddenLabel;
	private List<String> notation;
	private List<String> narrower;
	private List<String> related;
	
	/**
	 * additional fields from JSKOS definition
	 * http://www.w3.org/2009/08/skos-reference/skos.rdf
	 */
	private String vocabulary; //URI referencing a JSKOS JSKOS-LD context document
	private String context;
	private List<String> ancestors;
	private List<String> inScheme;
	private List<String> topConceptOf;	
	Map<String, String> scopeNote;
	Map<String, String> definition;
	Map<String, String> example;
	Map<String, String> historyNote;
	Map<String, String> editorialNote;
	Map<String, String> changeNote;
	
	/**
	 * additional fields from SKOS definition
	 * https://gbv.github.io/jskos/jskos.html
	 */
	private List<String> hasTopConcept;
	Map<String, String> note;
	Map<String, String> semanticRelation;
	Map<String, String> broaderTransitive;
	Map<String, String> narrowerTransitive;
	Map<String, String> member;
	private List<String> memberList;	
	Map<String, String> mappingRelation;
	Map<String, String> broadMatch;
	Map<String, String> narrowMatch;
	Map<String, String> relateMatch;
	Map<String, String> exactMatch;
	Map<String, String> closeMatch;
	
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}
	
	public void addType(String newType) {
		if(type == null)
			type = new ArrayList<String>();
		
		if(! type.contains(newType))
			type.add(newType);
	}
	

	@Override
	public Map<String, String> getPrefLabel() {
	  	return prefLabel;
	}

	@Override
	public void setPrefLabel(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	@Override
	public void addPrefLabelInMapping(String id, String label) {
	    if(this.prefLabel == null) {
	        this.prefLabel = new HashMap<String, String>();
	    }
	    this.prefLabel.put(id + "_" + WebAnnotationFields.PREF_LABEL, label);
	}
	
	@Override
	public void setAltLabel(Map<String, String> altLabel) {
		this.altLabel = altLabel;
	}
	
	@Override
	public void addAltLabelInMapping(String id, String label) {
	    if(this.altLabel == null) {
	        this.altLabel = new HashMap<String, String>();
	    }
	    this.altLabel.put(id + "_" + WebAnnotationFields.ALT_LABEL, label);
	}

	@Override
	public Map<String, String> getAltLabel() {
	   return altLabel;
	}

	@Override
	public void setHiddenLabel(Map<String, String> hiddenLabel) {
		this.hiddenLabel = hiddenLabel;
	}
	
	@Override
	public void addHiddenLabelInMapping(String id, String label) {
	    if(this.hiddenLabel == null) {
	        this.hiddenLabel = new HashMap<String, String>();
	    }
	    this.hiddenLabel.put(id + "_" + WebAnnotationFields.HIDDEN_LABEL, label);
	}

	@Override
	public Map<String, String> getHiddenLabel() {
	   return hiddenLabel;
	}

	public void addNotation(String newNotation) {
		if(notation == null)
			notation = new ArrayList<String>(2);
		if (!notation.contains(newNotation)) {
			notation.add(newNotation);
		}
	}
	
	public List<String> getNotation() {
		return notation;
	}
	
	public void setNotation(List<String> notationList) {
		this.notation = notationList;
	}
	
	public void addNarrower(String newNarrower) {
		if(narrower == null)
			narrower = new ArrayList<String>(2);
		if (!narrower.contains(newNarrower)) {
			narrower.add(newNarrower);
		}
	}
	
	public List<String> getNarrower() {
		return narrower;
	}
	
	public void setNarrower(List<String> narrowerList) {
		this.narrower = narrowerList;
	}
		
	private List<String> broader = new ArrayList<String>(2);

	public void addBroader(String newbroader) {
		if(broader == null)
			broader = new ArrayList<String>(2);
		if (!broader.contains(newbroader)) {
			broader.add(newbroader);
		}
	}
	
	public List<String> getBroader() {
		return broader;
	}
	
	public void setBroader(List<String> broaderList) {
		this.broader = broaderList;
	}
	
	public void addRelated(String newRelated) {
		if(related == null)
			related = new ArrayList<String>(2);
		if (!related.contains(newRelated)) {
			related.add(newRelated);
		}
	}
	
	public List<String> getRelated() {
		return related;
	}
	
	public void setRelated(List<String> relatedList) {
		this.related = relatedList;
	}
	
	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public List<String> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<String> ancestors) {
		this.ancestors = ancestors;
	}

	public void addAncestors(String newAncestor) {
		if(ancestors == null)
			ancestors = new ArrayList<String>(2);
		if (!ancestors.contains(newAncestor)) {
			ancestors.add(newAncestor);
		}
	}
	
	public List<String> getInScheme() {
		return inScheme;
	}

	public void setInScheme(List<String> inScheme) {
		this.inScheme = inScheme;
	}

	public void addInScheme(String newInScheme) {
		if(inScheme == null)
			inScheme = new ArrayList<String>(2);
		if (!inScheme.contains(newInScheme)) {
			inScheme.add(newInScheme);
		}
	}
	
	public List<String> getTopConceptOf() {
		return topConceptOf;
	}

	public void setTopConceptOf(List<String> topConceptOf) {
		this.topConceptOf = topConceptOf;
	}

	public void addTopConceptOf(String newTopConceptOf) {
		if(topConceptOf == null)
			topConceptOf = new ArrayList<String>(2);
		if (!topConceptOf.contains(newTopConceptOf)) {
			topConceptOf.add(newTopConceptOf);
		}
	}
	
	public Map<String, String> getScopeNote() {
		return scopeNote;
	}

	public void setScopeNote(Map<String, String> scopeNote) {
		this.scopeNote = scopeNote;
	}

	@Override
	public void addScopeNoteInMapping(String id, String label) {
	    if(this.scopeNote == null) {
	        this.scopeNote = new HashMap<String, String>();
	    }
	    this.scopeNote.put(id + "_" + WebAnnotationFields.SCOPE_NOTE, label);
	}

	public Map<String, String> getDefinition() {
		return definition;
	}

	public void setDefinition(Map<String, String> definition) {
		this.definition = definition;
	}

	@Override
	public void addDefinitionInMapping(String id, String label) {
	    if(this.definition == null) {
	        this.definition = new HashMap<String, String>();
	    }
	    this.definition.put(id + "_" + WebAnnotationFields.DEFINITION, label);
	}

	public Map<String, String> getExample() {
		return example;
	}

	public void setExample(Map<String, String> example) {
		this.example = example;
	}

	@Override
	public void addExampleInMapping(String id, String label) {
	    if(this.example == null) {
	        this.example = new HashMap<String, String>();
	    }
	    this.example.put(id + "_" + WebAnnotationFields.EXAMPLE, label);
	}

	public Map<String, String> getHistoryNote() {
		return historyNote;
	}

	public void setHistoryNote(Map<String, String> historyNote) {
		this.historyNote = historyNote;
	}

	@Override
	public void addHistoryNoteInMapping(String id, String label) {
	    if(this.historyNote == null) {
	        this.historyNote = new HashMap<String, String>();
	    }
	    this.historyNote.put(id + "_" + WebAnnotationFields.HISTORY_NOTE, label);
	}

	public Map<String, String> getEditorialNote() {
		return editorialNote;
	}

	public void setEditorialNote(Map<String, String> editorialNote) {
		this.editorialNote = editorialNote;
	}

	@Override
	public void addEditorialNoteInMapping(String id, String label) {
	    if(this.editorialNote == null) {
	        this.editorialNote = new HashMap<String, String>();
	    }
	    this.editorialNote.put(id + "_" + WebAnnotationFields.EDITORIAL_NOTE, label);
	}

	public Map<String, String> getChangeNote() {
		return changeNote;
	}

	public void setChangeNote(Map<String, String> changeNote) {
		this.changeNote = changeNote;
	}

	@Override
	public void addChangeNoteInMapping(String id, String label) {
	    if(this.changeNote == null) {
	        this.changeNote = new HashMap<String, String>();
	    }
	    this.changeNote.put(id + "_" + WebAnnotationFields.CHANGE_NOTE, label);
	}

	public List<String> getHasTopConcept() {
		return hasTopConcept;
	}

	public void setHasTopConcept(List<String> hasTopConcept) {
		this.hasTopConcept = hasTopConcept;
	}

	public void addHasTopConcept(String newTopConcept) {
		if(hasTopConcept == null)
			hasTopConcept = new ArrayList<String>(2);
		if (!hasTopConcept.contains(newTopConcept)) {
			hasTopConcept.add(newTopConcept);
		}
	}
	
	public Map<String, String> getNote() {
		return note;
	}

	public void setNote(Map<String, String> note) {
		this.note = note;
	}

	@Override
	public void addNoteInMapping(String id, String label) {
	    if(this.note == null) {
	        this.note = new HashMap<String, String>();
	    }
	    this.note.put(id + "_" + WebAnnotationFields.NOTE, label);
	}

	public Map<String, String> getSemanticRelation() {
		return semanticRelation;
	}

	public void setSemanticRelation(Map<String, String> semanticRelation) {
		this.semanticRelation = semanticRelation;
	}

	@Override
	public void addSemanticRelationInMapping(String id, String label) {
	    if(this.semanticRelation == null) {
	        this.semanticRelation = new HashMap<String, String>();
	    }
	    this.semanticRelation.put(id + "_" + WebAnnotationFields.SEMANTIC_RELATION, label);
	}

	public Map<String, String> getBroaderTransitive() {
		return broaderTransitive;
	}

	public void setBroaderTransitive(Map<String, String> broaderTransitive) {
		this.broaderTransitive = broaderTransitive;
	}

	@Override
	public void addBroaderTransitiveInMapping(String id, String label) {
	    if(this.broaderTransitive == null) {
	        this.broaderTransitive = new HashMap<String, String>();
	    }
	    this.broaderTransitive.put(id + "_" + WebAnnotationFields.BROADER_TRANSITIVE, label);
	}

	public Map<String, String> getNarrowerTransitive() {
		return narrowerTransitive;
	}

	public void setNarrowerTransitive(Map<String, String> narrowerTransitive) {
		this.narrowerTransitive = narrowerTransitive;
	}

	@Override
	public void addNarrowerTransitiveInMapping(String id, String label) {
	    if(this.narrowerTransitive == null) {
	        this.narrowerTransitive = new HashMap<String, String>();
	    }
	    this.narrowerTransitive.put(id + "_" + WebAnnotationFields.NARROWER_TRANSITIVE, label);
	}

	public Map<String, String> getMember() {
		return member;
	}

	public void setMember(Map<String, String> member) {
		this.member = member;
	}

	public List<String> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<String> memberList) {
		this.memberList = memberList;
	}

	public void addMemberList(String newMember) {
		if(memberList == null)
			memberList = new ArrayList<String>(2);
		if (!memberList.contains(newMember)) {
			memberList.add(newMember);
		}
	}
	
	public Map<String, String> getMappingRelation() {
		return mappingRelation;
	}

	public void setMappingRelation(Map<String, String> mappingRelation) {
		this.mappingRelation = mappingRelation;
	}

	@Override
	public void addMappingRelationInMapping(String id, String label) {
	    if(this.mappingRelation == null) {
	        this.mappingRelation = new HashMap<String, String>();
	    }
	    this.mappingRelation.put(id + "_" + WebAnnotationFields.MAPPING_RELATION, label);
	}

	public Map<String, String> getBroadMatch() {
		return broadMatch;
	}

	public void setBroadMatch(Map<String, String> broadMatch) {
		this.broadMatch = broadMatch;
	}

	@Override
	public void addBroadMatchInMapping(String id, String label) {
	    if(this.broadMatch == null) {
	        this.broadMatch = new HashMap<String, String>();
	    }
	    this.broadMatch.put(id + "_" + WebAnnotationFields.BROAD_MATCH, label);
	}

	public Map<String, String> getNarrowMatch() {
		return narrowMatch;
	}

	public void setNarrowMatch(Map<String, String> narrowMatch) {
		this.narrowMatch = narrowMatch;
	}

	@Override
	public void addNarrowMatchInMapping(String id, String label) {
	    if(this.narrowMatch == null) {
	        this.narrowMatch = new HashMap<String, String>();
	    }
	    this.narrowMatch.put(id + "_" + WebAnnotationFields.NARROW_MATCH, label);
	}

	public Map<String, String> getRelateMatch() {
		return relateMatch;
	}

	public void setRelateMatch(Map<String, String> relateMatch) {
		this.relateMatch = relateMatch;
	}

	@Override
	public void addRelateMatchInMapping(String id, String label) {
	    if(this.relateMatch == null) {
	        this.relateMatch = new HashMap<String, String>();
	    }
	    this.relateMatch.put(id + "_" + WebAnnotationFields.RELATE_MATCH, label);
	}

	public Map<String, String> getExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(Map<String, String> exactMatch) {
		this.exactMatch = exactMatch;
	}

	@Override
	public void addExactMatchInMapping(String id, String label) {
	    if(this.exactMatch == null) {
	        this.exactMatch = new HashMap<String, String>();
	    }
	    this.exactMatch.put(id + "_" + WebAnnotationFields.EXACT_MATCH, label);
	}

	public Map<String, String> getCloseMatch() {
		return closeMatch;
	}

	public void setCloseMatch(Map<String, String> closeMatch) {
		this.closeMatch = closeMatch;
	}
	
	@Override
	public void addCloseMatchInMapping(String id, String label) {
	    if(this.closeMatch == null) {
	        this.closeMatch = new HashMap<String, String>();
	    }
	    this.closeMatch.put(id + "_" + WebAnnotationFields.CLOSE_MATCH, label);
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Concept)) {
	        return false;
	    }

	    Concept that = (Concept) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().toString().equals(that.getType().toString()))) {
	    	System.out.println("Concept objects have different 'Type' fields.");
	    	res = false;
	    }
	    
	    if ((this.getNotation() != null) && (that.getNotation() != null) &&
	    		(!this.getNotation().toString().equals(that.getNotation().toString()))) {
	    	System.out.println("Concept objects have different 'Notation' fields.");
	    	res = false;
	    }
	    
	    if ((this.getNarrower() != null) && (that.getNarrower() != null) &&
	    		(!this.getNarrower().toString().equals(that.getNarrower().toString()))) {
	    	System.out.println("Concept objects have different 'Narrower' fields.");
	    	res = false;
	    }
	    
	    if ((this.getBroader() != null) && (that.getBroader() != null) &&
	    		(!this.getBroader().toString().equals(that.getBroader().toString()))) {
	    	System.out.println("Concept objects have different 'Broader' fields.");
	    	res = false;
	    }
	    
	    if ((this.getRelated() != null) && (that.getRelated() != null) &&
	    		(!this.getRelated().toString().equals(that.getRelated().toString()))) {
	    	System.out.println("Concept objects have different 'Related' fields.");
	    	res = false;
	    }
	    
	    if ((this.getPrefLabel() != null) && (that.getPrefLabel() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getPrefLabel(), that.getPrefLabel()))) {
	    	System.out.println("Concept objects have different 'PrefLabel' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAltLabel() != null) && (that.getAltLabel() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getAltLabel(), that.getAltLabel()))) {
	    	System.out.println("Concept objects have different 'AltLabel' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHiddenLabel() != null) && (that.getHiddenLabel() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getHiddenLabel(), that.getHiddenLabel()))) {
	    	System.out.println("Concept objects have different 'HiddenLabel' fields.");
	    	res = false;
	    }
	    
	    if (StringUtils.isNotEmpty(this.getVocabulary()) 
	    		&& StringUtils.isNotEmpty(that.getVocabulary()) 
	    		&& !this.getVocabulary().equals(that.getVocabulary())) {
	    	System.out.println("Concept objects have different 'Vocabulary' fields.");
	    	res = false;
	    }

	    if (StringUtils.isNotEmpty(this.getContext()) 
	    		&& StringUtils.isNotEmpty(that.getContext()) 
	    		&& !this.getContext().equals(that.getContext())) {
	    	System.out.println("Concept objects have different 'Context' fields.");
	    	res = false;
	    }

	    if ((this.getAncestors() != null) && (that.getAncestors() != null) &&
	    		(!this.getAncestors().toString().equals(that.getAncestors().toString()))) {
	    	System.out.println("Concept objects have different 'Ancestors' fields.");
	    	res = false;
	    }
	    
	    if ((this.getInScheme() != null) && (that.getInScheme() != null) &&
	    		(!this.getInScheme().toString().equals(that.getInScheme().toString()))) {
	    	System.out.println("Concept objects have different 'InScheme' fields.");
	    	res = false;
	    }
	    
	    if ((this.getTopConceptOf() != null) && (that.getTopConceptOf() != null) &&
	    		(!this.getTopConceptOf().toString().equals(that.getTopConceptOf().toString()))) {
	    	System.out.println("Concept objects have different 'TopConceptOf' fields.");
	    	res = false;
	    }
	    
	    if ((this.getScopeNote() != null) && (that.getScopeNote() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getScopeNote(), that.getScopeNote()))) {
	    	System.out.println("Concept objects have different 'ScopeNote' fields.");
	    	res = false;
	    }
	    
	    if ((this.getDefinition() != null) && (that.getDefinition() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getDefinition(), that.getDefinition()))) {
	    	System.out.println("Concept objects have different 'Definition' fields.");
	    	res = false;
	    }
	    
	    if ((this.getExample() != null) && (that.getExample() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getExample(), that.getExample()))) {
	    	System.out.println("Concept objects have different 'Example' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHistoryNote() != null) && (that.getHistoryNote() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getHistoryNote(), that.getHistoryNote()))) {
	    	System.out.println("Concept objects have different 'HistoryNote' fields.");
	    	res = false;
	    }
	    
	    if ((this.getEditorialNote() != null) && (that.getEditorialNote() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getEditorialNote(), that.getEditorialNote()))) {
	    	System.out.println("Concept objects have different 'EditorialNote' fields.");
	    	res = false;
	    }
	    
	    if ((this.getChangeNote() != null) && (that.getChangeNote() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getChangeNote(), that.getChangeNote()))) {
	    	System.out.println("Concept objects have different 'ChangeNote' fields.");
	    	res = false;
	    }
	    
	    if ((this.getNote() != null) && (that.getNote() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getNote(), that.getNote()))) {
	    	System.out.println("Concept objects have different 'Note' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSemanticRelation() != null) && (that.getSemanticRelation() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getSemanticRelation(), that.getSemanticRelation()))) {
	    	System.out.println("Concept objects have different 'SemanticRelation' fields.");
	    	res = false;
	    }
	    
	    if ((this.getBroaderTransitive() != null) && (that.getBroaderTransitive() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getBroaderTransitive(), that.getBroaderTransitive()))) {
	    	System.out.println("Concept objects have different 'BroaderTransitive' fields.");
	    	res = false;
	    }
	    
	    if ((this.getNarrowerTransitive() != null) && (that.getNarrowerTransitive() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getNarrowerTransitive(), that.getNarrowerTransitive()))) {
	    	System.out.println("Concept objects have different 'NarrowerTransitive' fields.");
	    	res = false;
	    }
	    
	    if ((this.getMember() != null) && (that.getMember() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getMember(), that.getMember()))) {
	    	System.out.println("Concept objects have different 'Member' fields.");
	    	res = false;
	    }
	    
	    if ((this.getMappingRelation() != null) && (that.getMappingRelation() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getMappingRelation(), that.getMappingRelation()))) {
	    	System.out.println("Concept objects have different 'MappingRelation' fields.");
	    	res = false;
	    }
	    
	    if ((this.getBroadMatch() != null) && (that.getBroadMatch() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getBroadMatch(), that.getBroadMatch()))) {
	    	System.out.println("Concept objects have different 'BroadMatch' fields.");
	    	res = false;
	    }
	    
	    if ((this.getNarrowMatch() != null) && (that.getNarrowMatch() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getNarrowMatch(), that.getNarrowMatch()))) {
	    	System.out.println("Concept objects have different 'NarrowMatch' fields.");
	    	res = false;
	    }
	    
	    if ((this.getRelateMatch() != null) && (that.getRelateMatch() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getRelateMatch(), that.getRelateMatch()))) {
	    	System.out.println("Concept objects have different 'RelateMatch' fields.");
	    	res = false;
	    }
	    
	    if ((this.getExactMatch() != null) && (that.getExactMatch() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getExactMatch(), that.getExactMatch()))) {
	    	System.out.println("Concept objects have different 'ExactMatch' fields.");
	    	res = false;
	    }
	    
	    if ((this.getCloseMatch() != null) && (that.getCloseMatch() != null) &&
	    		(!TypeUtils.areEqualMaps(this.getCloseMatch(), that.getCloseMatch()))) {
	    	System.out.println("Concept objects have different 'CloseMatch' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHasTopConcept() != null) && (that.getHasTopConcept() != null) &&
	    		(!this.getHasTopConcept().toString().equals(that.getHasTopConcept().toString()))) {
	    	System.out.println("Concept objects have different 'HasTopConcept' fields.");
	    	res = false;
	    }
	    
	    if ((this.getMemberList() != null) && (that.getMemberList() != null) &&
	    		(!this.getMemberList().toString().equals(that.getMemberList().toString()))) {
	    	System.out.println("Concept objects have different 'MemberList' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### Concept ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + WebAnnotationFields.TYPE + ":" + getRelated() + "\n";
		if (getNotation() != null) 
			res = res + "\t\t" + WebAnnotationFields.NOTATION + ":" + getNotation() + "\n";
		if (getNarrower() != null) 
			res = res + "\t\t" + WebAnnotationFields.NARROWER + ":" + getNarrower() + "\n";
		if (getBroader() != null) 
			res = res + "\t\t" + WebAnnotationFields.BROADER + ":" + getBroader() + "\n";
		if (getRelated() != null) 
			res = res + "\t\t" + WebAnnotationFields.RELATED + ":" + getRelated() + "\n";
		if (getPrefLabel() != null) 
			res = res + "\t\t" + WebAnnotationFields.PREF_LABEL + ":" + getPrefLabel() + "\n";
		if (getHiddenLabel() != null) 
			res = res + "\t\t" + WebAnnotationFields.HIDDEN_LABEL + ":" + getHiddenLabel() + "\n";
		if (getAltLabel() != null) 
			res = res + "\t\t" + WebAnnotationFields.ALT_LABEL + ":" + getAltLabel() + "\n";
		if (getVocabulary() != null) 
			res = res + "\t\t" + WebAnnotationFields.VOCABULARY + ":" + getVocabulary() + "\n";
		if (getContext() != null) 
			res = res + "\t\t" + WebAnnotationFields.CONTEXT + ":" + getVocabulary() + "\n";
		if (getAncestors() != null) 
			res = res + "\t\t" + WebAnnotationFields.ANCESTORS + ":" + getAncestors() + "\n";
		if (getInScheme() != null) 
			res = res + "\t\t" + WebAnnotationFields.IN_SCHEME + ":" + getInScheme() + "\n";
		if (getTopConceptOf() != null) 
			res = res + "\t\t" + WebAnnotationFields.TOP_CONCEPT_OF + ":" + getTopConceptOf() + "\n";
		if (getScopeNote() != null) 
			res = res + "\t\t" + WebAnnotationFields.SCOPE_NOTE + ":" + getScopeNote() + "\n";
		if (getDefinition() != null) 
			res = res + "\t\t" + WebAnnotationFields.DEFINITION + ":" + getDefinition() + "\n";
		if (getExample() != null) 
			res = res + "\t\t" + WebAnnotationFields.EXAMPLE + ":" + getExample() + "\n";
		if (getHistoryNote() != null) 
			res = res + "\t\t" + WebAnnotationFields.HISTORY_NOTE + ":" + getHistoryNote() + "\n";
		if (getEditorialNote() != null) 
			res = res + "\t\t" + WebAnnotationFields.EDITORIAL_NOTE + ":" + getEditorialNote() + "\n";
		if (getChangeNote() != null) 
			res = res + "\t\t" + WebAnnotationFields.CHANGE_NOTE + ":" + getChangeNote() + "\n";
		if (getHasTopConcept() != null) 
			res = res + "\t\t" + WebAnnotationFields.HAS_TOP_CONCEPT + ":" + getHasTopConcept() + "\n";
		if (getNote() != null) 
			res = res + "\t\t" + WebAnnotationFields.NOTE + ":" + getNote() + "\n";
		if (getSemanticRelation() != null) 
			res = res + "\t\t" + WebAnnotationFields.SEMANTIC_RELATION + ":" + getSemanticRelation() + "\n";
		if (getBroaderTransitive() != null) 
			res = res + "\t\t" + WebAnnotationFields.BROADER_TRANSITIVE + ":" + getBroaderTransitive() + "\n";
		if (getNarrowerTransitive() != null) 
			res = res + "\t\t" + WebAnnotationFields.NARROWER_TRANSITIVE + ":" + getNarrowerTransitive() + "\n";
		if (getMember() != null) 
			res = res + "\t\t" + WebAnnotationFields.MEMBER + ":" + getMember() + "\n";
		if (getMemberList() != null) 
			res = res + "\t\t" + WebAnnotationFields.MEMBER_LIST + ":" + getMemberList() + "\n";
		if (getMappingRelation() != null) 
			res = res + "\t\t" + WebAnnotationFields.MAPPING_RELATION + ":" + getMappingRelation() + "\n";
		if (getBroadMatch() != null) 
			res = res + "\t\t" + WebAnnotationFields.BROAD_MATCH + ":" + getBroadMatch() + "\n";
		if (getNarrowMatch() != null) 
			res = res + "\t\t" + WebAnnotationFields.NARROW_MATCH + ":" + getNarrowMatch() + "\n";
		if (getRelateMatch() != null) 
			res = res + "\t\t" + WebAnnotationFields.RELATE_MATCH + ":" + getRelateMatch() + "\n";
		if (getExactMatch() != null) 
			res = res + "\t\t" + WebAnnotationFields.EXACT_MATCH + ":" + getExactMatch() + "\n";
		if (getCloseMatch() != null) 
			res = res + "\t\t" + WebAnnotationFields.CLOSE_MATCH + ":" + getCloseMatch() + "\n";
		return res;
	}	
}
