package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;

public class BaseTagResource extends BaseInternetResource implements TagResource{

	private String id;
	private Long creationTimestamp;
	private Long lastUpdateTimestamp;
	private String lastUpdatedBy;
	private String creator;
	private String tagType;
	private List<String> sameAs; 
	protected Map<String, String> multilingual;

			
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getLabel() {
		return getValue();
	}

	@Override
	public void setLabel(String label) {
		setValue(label);
	}

	
	public Long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}
	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getTagType() {
		return tagType;
	}
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	
	public List<String> getSameAs() {
		return sameAs;
	}
	public void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}
	
	@Override
	public void addToSameAs(String value) {
		if(getSameAs() == null)
			this.sameAs = new ArrayList<String>();
		
		getSameAs().add(value);
	}
	
	public String toString() {
		return "SolrTag [id:" + getId() + ", creator:" + getCreator() + 
				", language:" + getLanguage() + ", label:" + getValue() + "]";
	}
	
	@Override
	public void setTagTypeEnum(TagTypes tagType) {
		setTagType(tagType.name());
	}
	
	@Override
	public Map<String, String> getMultilingual() {
		return multilingual;
	}

	@Override
	public void setMultilingual(Map<String, String> multilingual) {
		this.multilingual = multilingual;
	}
	
	@Override
	public void addMultilingualLabel(String language, String label) {
	    if(this.multilingual == null) {
	        this.multilingual = new HashMap<String, String>();
	    }
	    this.multilingual.put(language, label);
	}
	
}
