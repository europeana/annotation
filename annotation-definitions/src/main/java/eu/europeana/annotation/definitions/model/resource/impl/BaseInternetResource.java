package eu.europeana.annotation.definitions.model.resource.impl;

import eu.europeana.annotation.definitions.model.resource.InternetResource;

public abstract class BaseInternetResource implements InternetResource{

	private String contentType;
	private String mediaType;
	private String httpUri;
	private String language;
	private String value;

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String getMediaType() {
		return mediaType;
	}

	@Override
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public String getHttpUri() {
		return httpUri;
	}

	@Override
	public void setHttpUri(String httpUri) {
		this.httpUri = httpUri;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void copyInto(InternetResource destination){
		destination.setContentType(this.getContentType());
		destination.setHttpUri(this.getHttpUri());
		destination.setLanguage(this.getLanguage());
		destination.setMediaType(this.getMediaType());
		destination.setValue(this.getValue());
	}
}
