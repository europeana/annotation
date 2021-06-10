package eu.europeana.annotation.config;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "format")
public class SubtitleFormat {

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
	private String handler;
	
	public String getHandler() {
		return handler;
	}
}
