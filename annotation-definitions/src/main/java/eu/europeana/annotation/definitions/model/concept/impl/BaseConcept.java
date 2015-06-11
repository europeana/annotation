package eu.europeana.annotation.definitions.model.concept.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.concept.Concept;

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
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### Concept ###\n";
		
		if (getNotation() != null) 
			res = res + "\t\t" + "notation:" + getNotation() + "\n";
		if (getNarrower() != null) 
			res = res + "\t\t" + "narrower:" + getNarrower() + "\n";
		if (getBroader() != null) 
			res = res + "\t\t" + "broader:" + getBroader() + "\n";
		if (getRelated() != null) 
			res = res + "\t\t" + "related:" + getRelated() + "\n";
		if (getPrefLabel() != null) 
			res = res + "\t\t" + "prefLabel:" + getPrefLabel() + "\n";
		if (getHiddenLabel() != null) 
			res = res + "\t\t" + "hiddenLabel:" + getHiddenLabel() + "\n";
		if (getAltLabel() != null) 
			res = res + "\t\t" + "altLabel:" + getAltLabel() + "\n";
		return res;
	}	
}
