package eu.europeana.annotation.definitions.model.impl;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "subtitle-formats")
public class SubtitleFormats {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "format")
	private List<SubtitleFormat> formats;

	public List<SubtitleFormat> getFormats() {
		return formats;
	}

	public boolean hasFormat(String mimetype) {
		return formats.stream().anyMatch(s -> mimetype.contains(s.getMimetype()));
	}

	public Optional<SubtitleFormat> getFormat(String mimetype) {
		return formats.stream().filter(s -> mimetype.contains(s.getMimetype())).findFirst();
	}

}
