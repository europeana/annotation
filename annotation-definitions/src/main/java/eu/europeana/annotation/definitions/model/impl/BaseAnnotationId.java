package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.AnnotationId;

public class BaseAnnotationId implements AnnotationId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342617580049965304L;
	private String resourceId;
	private Integer annotationNr = -1;

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	@Override
	public String getResourceId() {
		return resourceId;
	}

	@Override
	public void setAnnotationNr(Integer nr) {
		this.annotationNr = nr;

	}

	@Override
	public Integer getAnnotationNr() {
		return annotationNr;
	}

	@Override
	public boolean equals(Object obj) {
		//europeana id and annotation number must not be null
		if(obj instanceof AnnotationId 
				&& this.getResourceId() != null && this.getAnnotationNr() != null
				&& this.getResourceId().equals(((AnnotationId)obj).getResourceId())
				&& this.getAnnotationNr().equals(((AnnotationId)obj).getAnnotationNr()))
			return true;
				
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return getResourceId()+"/"+getAnnotationNr();
	}
	
}
