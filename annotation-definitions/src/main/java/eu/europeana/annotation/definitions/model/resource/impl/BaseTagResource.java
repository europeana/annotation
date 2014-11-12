package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.TagResource;

public class BaseTagResource extends BaseInternetResource implements TagResource{

	private String id;
	private Long creationTimestamp;
	private Long lastUpdateTimestamp;
	private String lastUpdatedBy;
	private String creator;
	private String tagType;
	private Map<String, String> translations; 
	private List<String> sameAs; 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Map<String, String> getTranslations() {
		return translations;
	}
	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
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
	
}
