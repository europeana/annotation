package eu.europeana.annotation.definitions.model.body.impl;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.annotation.definitions.model.body.SkosConceptBody;
import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class EmbeddedSkosConceptBody extends BaseBody implements SkosConceptBody {

	private String internalId;

	@Override
	public String getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	@Deprecated
	// not used for now. waiting for preview specifications and entities
	private Concept concept;

	@Override
	public Concept getConcept() {
		return concept;
	}

	@Override
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	@Override
	public Map<String, String> getMultilingual() {
		if (this.multilingual == null) {
			this.multilingual = new HashMap<String, String>();
		}
		return multilingual;
	}

	@Override
	public void setMultilingual(Map<String, String> multilingual) {
		this.multilingual = multilingual;
	}

	@Override
	public void addLabelInMapping(String language, String label) {
		if (this.multilingual == null) {
			this.multilingual = new HashMap<String, String>();
		}
		this.multilingual.put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
	}

	public boolean equals(Object other) {

		if(! super.equals(other))
			return false;
		
		boolean res = true;
		SkosConceptBody that = (SkosConceptBody)other;
		
		if ((this.getMultilingual() != null) && (that.getMultilingual() != null)
				&& (!this.getMultilingual().toString().equals(that.getMultilingual().toString()))) {
			System.out.println("Body objects have different multilingual values.");
			res = false;
		}

		if ((this.getConcept() != null) && (that.getConcept() != null)
				&& (!this.getConcept().equals(that.getConcept()))) {
			System.out.println("Body objects have different concept objects.");
			res = false;
		}

		if ((this.getInternalId() != null) && (that.getInternalId() != null)
				&& (!this.getInternalId().equals(that.getInternalId()))) {
			System.out.println("Body objects have different body IDs.");
			res = false;
		}

		return res;
	}
	
	@Override
	public String toString() {
		
		String res = super.toString();
		if (getMultilingual() != null) 
			res = res + "\t\t" + "multilingual:" + getMultilingual().toString() + "\n";
		if (getConcept() != null) 
			res = res + "\n\t\t" + "Concept:" + getConcept().toString() + "\n";
		if (getInternalId() != null) 
			res = res + "\t\t" + "bodyId:" + getInternalId().toString() + "\n";
		
		return res;
	}
}
