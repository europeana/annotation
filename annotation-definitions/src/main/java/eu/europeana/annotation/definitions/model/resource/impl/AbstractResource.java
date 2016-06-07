package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.resource.ResourceDescription;

public class AbstractResource implements ResourceDescription{

	//** functional fields
	private String contentType;
	private String language;
	private String value;

	//in 90+% of cases the size is 1, for the 99+% the size is <=2
	private List<String> bodyType = new ArrayList<String>(1);
	
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
		if (!bodyType.contains(newType)) {
			bodyType.add(newType);
		}
	}
	
	@Override
	public List<String> getType() {
		return bodyType;
	}
	
	@Override
	public void setType(List<String> bodyTypeList) {
		this.bodyType = bodyTypeList;
	}
	
}
