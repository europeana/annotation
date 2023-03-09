package eu.europeana.annotation.definitions.model.impl;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "format")
public class MediaFormat {
  
  public static final String KEY_VERSION=";version=";

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
	
	public String getVersion() {
	  if(getMimetype() == null || !getMimetype().contains(KEY_VERSION)) {
	    return null; 
	  }
	  
	  String version = StringUtils.substringAfter(getMimetype(), KEY_VERSION);
	  return StringUtils.remove(version, '"');
	}

	@Override
	public boolean equals(Object obj) {
	  if(this == obj) {
	    return true;
	  }
	  if(obj == null) {
	    return false;
	  }
	  
	  if(! (obj instanceof MediaFormat)) {
	    return false;
	  }
	  
	  return this.getMimetype().equals( ((MediaFormat)obj).getMimetype());
	}
	
	@Override
	public int hashCode() {
	  return getMimetype().hashCode();
	}
}
