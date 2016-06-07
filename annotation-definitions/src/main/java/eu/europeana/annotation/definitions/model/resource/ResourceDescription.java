package eu.europeana.annotation.definitions.model.resource;

import java.util.List;

public interface ResourceDescription {

	void setValue(String value);

	String getValue();

	void setLanguage(String language);

	String getLanguage();

	void setContentType(String contentType);

	String getContentType();

	void setType(List<String> bodyTypeList);

	List<String> getType();

	void addType(String newType);

	void setInternalType(String internalType);

	String getInternalType();

	void setInputString(String inputString);

	String getInputString();

}
