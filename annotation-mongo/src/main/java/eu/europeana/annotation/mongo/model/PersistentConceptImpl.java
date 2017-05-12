package eu.europeana.annotation.mongo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.model.internal.PersistentConcept;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("concept")
@Polymorphic
public class PersistentConceptImpl implements PersistentConcept, PersistentObject {

	@Id
	private ObjectId id;
	
//	private String name;
//	private String uri;
//	private String idGeneration;
//	private Long lastIndexedTimestamp;

	private String uri;
	private List<String> type;
	Map<String, String> prefLabel;
	Map<String, String> altLabel;
	Map<String, String> hiddenLabel;
	private List<String> notation;
	private List<String> broader;
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
	

	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

//    @Override
//	public void setLastIndexedTimestamp(Long lastIndexedTimestamp) {
//		this.lastIndexedTimestamp = lastIndexedTimestamp;
//	}
//	
//	public Long getLastIndexedTimestamp() {
//		return lastIndexedTimestamp;
//	}
	
	public String toString() {
		return "PersistentConcept [" //"name:" + getName() 
				+ "Id:" + getId() 
//				", idGeneration:" + getIdGeneration() 
				+ ", last update: " + getLastIndexedTimestamp() + "]";
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAltLabel(Map<String, String> altLabel) {
		this.altLabel = altLabel;
	}

	@Override
	public void addAltLabelInMapping(String id, String label) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getHiddenLabel() {
		return hiddenLabel;
	}

	@Override
	public void addNotation(String newNotation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getNotation() {
		return notation;
	}

	@Override
	public void setNotation(List<String> notationList) {
		this.notation = notationList;
	}

	@Override
	public void addNarrower(String newNarrower) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getNarrower() {
		return narrower;
	}

	@Override
	public void setNarrower(List<String> narrowerList) {
		this.narrower = narrowerList;
	}

	@Override
	public void addBroader(String newbroader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getBroader() {
		// TODO Auto-generated method stub
		return broader;
	}

	@Override
	public void setBroader(List<String> broaderList) {
		this.broader = broaderList;
	}

//	@Override
//	public void addRelated(String newRelated) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public List<String> getRelated() {
//		return related;
//	}
//
//	@Override
//	public void setRelated(List<String> relatedList) {
//		this.related = relatedList;
//	}
//
	@Override
	public void setLastIndexedTimestamp(Long lastIndexedTimestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getLastIndexedTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	@Override
//	public String getName() {
//		return this.name;
//	}

//	@Override
//	public void setUri(String uri) {
//		this.uri = uri;
//	}
//
//	@Override
//	public String getUri() {
//		return this.uri;
//	}
//
//	@Override
//	public void setIdGeneration(IdGenerationTypes idGeneration) {
//		this.idGeneration = idGeneration.getIdType();
//	}
//
//	@Override
//	public String getIdGeneration() {
//		return this.idGeneration;
//	}
		
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
	
	
}