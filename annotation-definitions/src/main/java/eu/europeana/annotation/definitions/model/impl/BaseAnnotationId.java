package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class BaseAnnotationId implements AnnotationId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342617580049965304L;

//	private String resourceId;
	private String identifier = null;
	private String provider;

	public BaseAnnotationId(){
	}
	
//	public BaseAnnotationId(String europeanaId){
//		this.resourceId = europeanaId;
//	}
	
//	public BaseAnnotationId(String europeanaId, Long annotationNr){
	public BaseAnnotationId(String identifier){
//		this.resourceId = europeanaId;
		this.identifier = identifier;
	}
	
//	public BaseAnnotationId(String europeanaId, String provider, Long annotationNr){
	public BaseAnnotationId(String provider, String identifier){
//		this.resourceId = europeanaId;
		this.provider = provider;
		this.identifier = identifier;
	}
	
//	public void setResourceId(String resourceId) {
//		this.resourceId = resourceId;
//	}
//	
//	@Override
//	public String getResourceId() {
//		return resourceId;
//	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

	@Override
	public String getIdentifier() {
		return identifier;
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
//				&& this.getResourceId() != null 
				&& this.getIdentifier() != null
//				&& this.getResourceId().equals(((AnnotationId)obj).getResourceId())
				&& this.getProvider().equals(((AnnotationId)obj).getProvider())
				&& this.getIdentifier().equals(((AnnotationId)obj).getIdentifier()))
			return true;
				
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return toUri();
	}

	@Override
	public String toUri() {
		return WebAnnotationFields.SLASH
				+ getProvider() + WebAnnotationFields.SLASH
				+ getIdentifier();
	}
	
	@Override
	public String toUrl(String baseUrl) {
		return baseUrl + toUri(); 
	}
}
