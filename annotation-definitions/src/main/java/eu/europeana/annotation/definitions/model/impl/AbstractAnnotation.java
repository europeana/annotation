package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;

public class AbstractAnnotation implements Annotation{

	//private AnnotationId annotationId = null;
	private String europeanaId;
	private Integer annotationNr;
	private String creator;
	private String type;
	
	@Override
	public AnnotationId getAnnotationId() {
		return null;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		
	}

	@Override
	public String getEuropeanaId() {
		return europeanaId;
	}

	@Override
	public void setEuropeanaId(String europeanaId) {
		this.europeanaId = europeanaId;
		
	}

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public void setCreator(String creator) {
		this.creator = creator;
		
	}

	@Override
	public Integer getAnnotationNr() {
		return annotationNr;
	}

	public void setAnnotationNr(Integer annotationNr) {
		this.annotationNr = annotationNr;
	}
}
