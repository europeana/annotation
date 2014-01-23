package eu.europeana.annotation.definitions.model.impl;

import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.definitions.model.SemanticTag;

public class SemanticTagImpl extends AbstractAnnotation implements SemanticTag {

	public SemanticTagImpl(){
		super();
		setType(AnnotationTypes.SEMANTIC_TAG.toString());
	}
	
	private String label;
	private List<String> namedEntityIdList;
	private List<String> namedEntityLabelList;

	
	@Override
	public List<String> getNamedEntityIdList() {
		return namedEntityIdList;
	}

	@Override
	public void setNamedEntityIdList(List<String> namedEntityIdList) {
		this.namedEntityIdList = namedEntityIdList;
		
	}

	@Override
	public List<String> getNamedEntityLabelList() {
		return namedEntityLabelList;
	}

	@Override
	public void setNamedEntityLabelList(List<String> namedEntityLabelList) {
		this.namedEntityLabelList = namedEntityLabelList;
		
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
		
	}

}
