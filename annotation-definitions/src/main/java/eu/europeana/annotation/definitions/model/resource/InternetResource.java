package eu.europeana.annotation.definitions.model.resource;

import java.util.List;

public interface InternetResource {

	public abstract void setValue(String value);

	public abstract String getValue();

	public abstract void setValues(List<String> values);

	public abstract void addValue(String value);

	public abstract List<String> getValues();
	
	public abstract void setLanguage(String language);

	public abstract String getLanguage();

	public abstract void setHttpUri(String httpUri);

	public abstract String getHttpUri();

	public abstract void setMediaType(String mediaType);

	public abstract String getMediaType();

	public abstract void setContentType(String contentType);

	public abstract String getContentType();
	
	public abstract void copyInto(InternetResource destination);

}
