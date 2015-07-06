package eu.europeana.annotation.definitions.model.concept.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.concept.SkosConcept;

/**
 * This is a class for SkosConcept object that is a part of the Body object.
 * The definition is located on http://gbv.github.io/jskos/jskos-ad3846c.html.
 * The following JSON-LD context document can be used to map JSKOS to RDF triples.
 * The presentation of this object is:
 * 
{
    "skos": "http://www.w3.org/2004/02/skos/core#",
    "uri": "@id",
    "type": "http://www.w3c.org/1999/02/22-rdf-syntax-ns#type",
    "notation": "skos:notation",
    "prefLabel": {
        "@id": "skos:prefLabel",
        "@container": "@language"
    },
    "altLabel": {
        "@id": "skos:altLabel",
        "@container": "@language"
    },
    "hiddenLabel": {
        "@id": "skos:altLabel",
        "@container": "@language"
    },
    "narrower": "skos:narrower",
    "broader": "skos:broader",
    "related": "skos:related",
    "ancestors": "skos:broaderTransitive",
    "inScheme": {
        "@id": "skos:inScheme",
        "@type": "@id"
    },
    "topConceptOf": {
        "@id": "skos:topConceptOf",
        "@type": "@id"
    },
    "topConcepts": "skos:hasTopConcept"
}
 */
public abstract class BaseSkosConcept extends BaseConcept implements SkosConcept {

	private List<String> ancestor = new ArrayList<String>(2);

	public void addAncestor(String newAncestor) {
		if (!ancestor.contains(newAncestor)) {
			ancestor.add(newAncestor);
		}
	}
	
	public List<String> getAncestor() {
		return ancestor;
	}
	
	public void setAncestor(List<String> ancestorList) {
		this.ancestor = ancestorList;
	}

	private List<String> topConcept = new ArrayList<String>(2);

	public void addTopConcept(String newTopConcept) {
		if (!topConcept.contains(newTopConcept)) {
			topConcept.add(newTopConcept);
		}
	}
	
	public List<String> getTopConcept() {
		return topConcept;
	}
	
	public void setTopConcept(List<String> topConceptList) {
		this.topConcept = topConceptList;
	}

	private List<String> inScheme;
//	protected Map<String, String> inScheme;
//	protected Map<String, String> topConceptOf;
	private List<String> topConceptOf;	

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

//	@Override
//	public Map<String, String> getInScheme() {
//	    if(this.inScheme == null) {
//	        this.inScheme = new HashMap<String, String>();
//	    }
//		return inScheme;
//	}
//
//	@Override
//	public void setInScheme(Map<String, String> inScheme) {
//		this.inScheme = inScheme;
//	}
//	
//	@Override
//	public void addInSchemeInMapping(String id, String label) {
//	    if(this.inScheme == null) {
//	        this.inScheme = new HashMap<String, String>();
//	    }
//	    this.inScheme.put(id + "_" + WebAnnotationFields.IN_SCHEME, label);
//	}

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
	
	
//	@Override
//	public void setTopConceptOf(Map<String, String> topConceptOf) {
//		this.topConceptOf = topConceptOf;
//	}
//	
//	@Override
//	public void addTopConceptOfInMapping(String id, String label) {
//	    if(this.topConceptOf == null) {
//	        this.topConceptOf = new HashMap<String, String>();
//	    }
//	    this.topConceptOf.put(id + "_" + WebAnnotationFields.TOP_CONCEPT_OF, label);
//	}
//
//	@Override
//	public Map<String, String> getTopConceptOf() {
//	    if(this.topConceptOf == null) {
//	        this.topConceptOf = new HashMap<String, String>();
//	    }
//		return topConceptOf;
//	}

	private String skos;
	
	@Override
	public String getSkos() {
		return skos;
	}
	@Override
	public void setSkos(String skos) {
		this.skos = skos;
	}
	
	private String httpUri;
	
	private String skosType;
	
	@Override
	public String getSkosType() {
		return skosType;
	}

	@Override
	public void setSkosType(String skosType) {
		this.skosType = skosType;
	}

	@Override
	public String getHttpUri() {
		return httpUri;
	}

	@Override
	public void setHttpUri(String httpUri) {
		this.httpUri = httpUri;
	}
	
	protected BaseSkosConcept(){}
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Concept)) {
	        return false;
	    }

	    SkosConcept that = (SkosConcept) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getSkos() != null) && (that.getSkos() != null) &&
	    		(!this.getSkos().equals(that.getSkos()))) {
	    	System.out.println("SKOS Concept objects have different 'skos' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSkosType() != null) && (that.getSkosType() != null) &&
	    		(!this.getSkosType().equals(that.getSkosType()))) {
	    	System.out.println("SKOS Concept objects have different 'skos type' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	System.out.println("SKOS Concept objects have different 'HTTP URI' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### SKOS Concept ###\n";
		
		if (getSkos() != null) 
			res = res + "\t\t" + "skos:" + getSkos() + "\n";
		if (getSkosType() != null) 
			res = res + "\t\t" + "skos:" + getSkosType() + "\n";
		if (getHttpUri() != null) 
			res = res + "\t\t" + "skos:" + getHttpUri() + "\n";
		return res;
	}	
}
