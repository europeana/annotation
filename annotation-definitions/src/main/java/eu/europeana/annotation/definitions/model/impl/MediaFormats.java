package eu.europeana.annotation.definitions.model.impl;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "media-formats")
public class MediaFormats {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "format")
	private List<MediaFormat> formats;

	public List<MediaFormat> getFormats() {
		return formats;
	}

	public Optional<MediaFormat> getFormat(String mimetype) {
		return formats.stream().filter(s -> mimetype.equals(s.getMimetype())).findFirst();
	}

}
