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
 *	   "notation": "skos:notation",
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
public abstract class BaseConcept implements Concept {

	protected Map<String, String> prefLabel;
	protected Map<String, String> altLabel;
	protected Map<String, String> hiddenLabel;

	@Override
	public Map<String, String> getPrefLabel() {
	    if(this.prefLabel == null) {
	        this.prefLabel = new HashMap<String, String>();
	    }
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
	    if(this.altLabel == null) {
	        this.altLabel = new HashMap<String, String>();
	    }
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
	    if(this.hiddenLabel == null) {
	        this.hiddenLabel = new HashMap<String, String>();
	    }
		return hiddenLabel;
	}

	    
	private List<String> notation = new ArrayList<String>(2);

	public void addNotation(String newNotation) {
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
	
	private List<String> narrower = new ArrayList<String>(2);

	public void addNarrower(String newNarrower) {
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
		if (!broader.contains(newbroader)) {
			broader.add(newbroader);
		}
	}
	
	public List<String> getbroader() {
		return broader;
	}
	
	public void setbroader(List<String> broaderList) {
		this.broader = broaderList;
	}
	
	private List<String> related = new ArrayList<String>(2);

	public void addRelated(String newRelated) {
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
	
	protected BaseConcept(){}
	
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
	    		(!this.getNotation().equals(that.getNotation()))) {
	    	System.out.println("Concept objects have different 'Notation' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### Concept ###\n";
		
		if (getNotation() != null) 
			res = res + "\t\t" + "notation:" + getNotation() + "\n";
		return res;
	}	
}
