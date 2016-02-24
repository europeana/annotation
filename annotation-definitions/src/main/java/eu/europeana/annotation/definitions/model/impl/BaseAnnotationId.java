package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class BaseAnnotationId implements AnnotationId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342617580049965304L;

	private String baseUrl;
	private String identifier = null;
	private String provider;

	/**
	 * this constructor is used by factory methods and automatic instantiation of this class
	 * for other purposes is recommended to use the explicit constructor {@link #BaseAnnotationId(String, String, String)}
	 */
	public BaseAnnotationId(){
	}
	
//	public BaseAnnotationId(String europeanaId){
//		this.resourceId = europeanaId;
//	}
	
	public BaseAnnotationId(String baseUrl, String provider, String identifier){
		this.baseUrl = baseUrl;
		this.provider = provider;
		this.identifier = identifier;
	}
	

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
		if(obj instanceof AnnotationId 
				&& this.toHttpUrl().equals(((AnnotationId) obj).toHttpUrl()))
			return true;
				
		return false;
	}
	
	@Override
	public int hashCode() {
		return toHttpUrl().hashCode();
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
	public String toHttpUrl() {
		return getBaseUrl() + toUri(); 
	}

	@Override
	public String getBaseUrl() {
		return baseUrl;
	}

	@Override
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@Override
	public void copyFrom(AnnotationId volatileObject) {
		this.setBaseUrl(((AnnotationId) volatileObject).getBaseUrl());
		this.setProvider(((AnnotationId) volatileObject).getProvider());
		this.setIdentifier(((AnnotationId) volatileObject).getIdentifier());			
	}
}
