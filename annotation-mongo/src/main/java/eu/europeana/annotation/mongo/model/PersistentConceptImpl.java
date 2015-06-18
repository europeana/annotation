package eu.europeana.annotation.mongo.model;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;

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
	
	@Override
	public void copyFrom(Object volatileObject) {
		// TODO Auto-generated method stub
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

	@Override
	public void addRelated(String newRelated) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getRelated() {
		return related;
	}

	@Override
	public void setRelated(List<String> relatedList) {
		this.related = relatedList;
	}

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
			
}