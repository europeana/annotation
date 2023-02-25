package eu.europeana.annotation.definitions.model.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "format")
public class MediaFormat {

	@JacksonXmlProperty(isAttribute = true)
	private String mimetype;
	
	public String getMimetype() {
		return mimetype;
	}

	@JacksonXmlProperty(isAttribute = true)
	private String name;

	public String getName() {
		return name;
	}

	@JacksonXmlProperty(isAttribute = true)
	private String validation;

	public String getValidation() {
		return validation;
	}

	@JacksonXmlProperty(isAttribute = true)
	private String validationResource;

	public String getValidationResource() {
		return validationResource;
	}

}
