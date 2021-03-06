package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;


/**
 * This class is deprecated and needs to be refactored when adding support for previews and entities 
 * @author GordeaS
 *
 */
@Deprecated
public interface TagResource extends InternetResource{

	
	// technical information used for lifetime management 
	//TODO: consider moving the technical information to own interface 
	public abstract void setLastUpdateTimestamp(Long lastUpdateTimestamp);

	public abstract Long getLastUpdateTimestamp();

	public abstract void setCreationTimestamp(Long creationTimestamp);

	public abstract Long getCreationTimestamp();

	public abstract void setCreator(String creator);

	public abstract String getCreator();

	public abstract void setLastUpdatedBy(String lastUpdatedBy);

	public abstract String getLastUpdatedBy();

	/**
	 * not used and not wa standard
	 * @param value
	 */
	@Deprecated
	public abstract void addToSameAs(String value);
	/**
	 * not used and not wa standard
	 * @param value
	 */
	@Deprecated
	public abstract List<String> getSameAs();

	//functional methods  
	public abstract String getId();
	public abstract String getLabel();
	public abstract void setLabel(String label);
	public abstract void setTagTypeEnum(TagTypes tagType);
	public abstract void setTagType(String tagType);
	public abstract String getTagType();
	
	public abstract Map<String, String> getMultilingual();

	public abstract void setMultilingual(Map<String, String> multiLingual);
	
	/**
	 * This method puts label in multilingual mapping.
	 * @param language
	 * @param label
	 */
	public void addMultilingualLabel(String language, String label);
	
}
