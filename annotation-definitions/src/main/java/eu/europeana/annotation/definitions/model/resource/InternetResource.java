package eu.europeana.annotation.definitions.model.resource;

import java.util.List;

public interface InternetResource extends ResourceDescription {


	public abstract void setValues(List<String> values);

	public abstract void addValue(String value);

	public abstract List<String> getValues();
	
	public abstract void setResourceId(String resourceId);

	public abstract String getResourceId();

	public abstract void setResourceIds(List<String> resourceIds);

	public abstract void addResourceId(String resourceId);

	public abstract List<String> getResourceIds();
	

	/** replaced by the type of the extenral resources */
	@Deprecated
	public abstract void setMediaType(String mediaType);
	/** replaced by the type of the extenral resources */
	@Deprecated
	public abstract String getMediaType();

//	public abstract void setContentType(String contentType);
//
//	public abstract String getContentType();
	
	void setHttpUri(String httpUri);

	String getHttpUri();

	public abstract void copyInto(InternetResource destination);

}
