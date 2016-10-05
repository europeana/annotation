package eu.europeana.annotation.definitions.model.resource;

import java.util.List;

public interface ResourceDescription {

	public void setValue(String value);

	public String getValue();

	public void setLanguage(String language);

	public String getLanguage();

	/**
	 * Web Annotation Model - Format 
	 * @param contentType
	 */
	public void setContentType(String contentType);

	public String getContentType();

	public void setType(List<String> bodyTypeList);

	public List<String> getType();

	public void addType(String newType);

	public void setInternalType(String internalType);

	public String getInternalType();

	public void setInputString(String inputString);

	public String getInputString();

	public void setTitle(String title);

	public String getTitle();
	
	public abstract void setHttpUri(String httpUri);

	public abstract String getHttpUri();

	public void setContext(String context);

	public String getContext();	
}
