package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.resource.ResourceDescription;

public class AbstractResource implements ResourceDescription{

	//** functional fields
	protected String httpUri;
	private String contentType;
	private String language;
	private String value;
	private String title;
	
	

	//in 90+% of cases the size is 1, for the 99+% the size is <=2
	private List<String> type = new ArrayList<String>(1);
	
	//** technical fields
	private String internalType;
	private String inputString;
	
	
	@Override
	public String getInputString() {
		return inputString;
	}
	@Override
	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
	@Override
	public String getInternalType() {
		return internalType;
	}
	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
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
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	public void addType(String newType) {
		if (!type.contains(newType)) {
			type.add(newType);
		}
	}
	
	@Override
	public List<String> getType() {
		return type;
	}
	
	@Override
	public void setType(List<String> bodyTypeList) {
		this.type = bodyTypeList;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	
}
