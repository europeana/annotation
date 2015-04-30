package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class BaseAnnotationId implements AnnotationId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342617580049965304L;

	private String resourceId;
	private Long annotationNr = null;
	private String provider;

	public BaseAnnotationId(){
	}
	
	public BaseAnnotationId(String europeanaId){
		this.resourceId = europeanaId;
	}
	
	public BaseAnnotationId(String europeanaId, Long annotationNr){
		this.resourceId = europeanaId;
		this.annotationNr = annotationNr;
	}
	
	public BaseAnnotationId(String europeanaId, String provider, Long annotationNr){
		this.resourceId = europeanaId;
		this.provider = provider;
		this.annotationNr = annotationNr;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	@Override
	public String getResourceId() {
		return resourceId;
	}

	@Override
	public void setAnnotationNr(Long annotationNr) {
		this.annotationNr = annotationNr;

	}

	@Override
	public Long getAnnotationNr() {
		return annotationNr;
	}

	@Override
	public String getProvider() {
		return provider;
	}

	@Override
	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public boolean equals(Object obj) {
		//europeana id and annotation number must not be null
		if(obj instanceof AnnotationId 
				&& this.getResourceId() != null && this.getAnnotationNr() != null
				&& this.getResourceId().equals(((AnnotationId)obj).getResourceId())
				&& this.getProvider().equals(((AnnotationId)obj).getProvider())
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
		return WebAnnotationFields.ANNOTATION_ID_PREFIX 
				+ getResourceId() + WebAnnotationFields.SLASH
				+ getProvider() + WebAnnotationFields.SLASH
				+ getAnnotationNr();
	}

	@Override
	public String toUri() {
		return WebAnnotationFields.ANNOTATION_ID_PREFIX 
				//+ getResourceId() 
				+ WebAnnotationFields.SLASH
				+ getProvider() + WebAnnotationFields.SLASH
				+ getAnnotationNr();
	}
	
}
