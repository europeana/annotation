package eu.europeana.annotation.mongo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

/**
 * 
 * @author Sergiu Gordea 
 *
 */
@Entity("tag")
@Indexes( {@Index("httpUri, tagType"), @Index("value, tagType"), @Index("value")})
public class PersistentTagImpl extends BaseInternetResource implements PersistentTag, NoSqlEntity{
	
	@Id
	private ObjectId id;
	private Long creationTimestamp;
	private Long lastUpdateTimestamp;
	private String lastUpdatedBy;
	private String creator;
	private String tagType;
	private Map<String, String> translations; 
	private List<String> sameAs; 
	
	
	@Override
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	@Override
	public Long getCreationTimestamp() {
		return creationTimestamp;
	}
	@Override
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	@Override
	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}
	@Override
	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	@Override
	public Map<String, String> getTranslations() {
		return translations;
	}
	@Override
	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}
	
	public void addTranslation(String language, String text){
		if(getTranslations() == null)
			this.translations = new HashMap<String, String>();
		
		getTranslations().put(language, text);
	} 
	
	@Override
	public List<String> getSameAs() {
		return sameAs;
	}
	protected void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}

	@Override
	public void addToSameAs(String value){
		if(getSameAs() == null)
			this.sameAs = new ArrayList<String>();
		
		getSameAs().add(value);
	}
	@Override
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	@Override
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	@Override
	public String getCreator() {
		return creator;
	}
	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}
	@Override
	public String getLabel() {
		return getValue();
	}
	@Override
	public String getTagType() {
		return tagType;
	}
	@Override
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	@Override
	public void setTagType(TagTypes tagType) {
		this.tagType = tagType.name();
	}
	@Override
	public void setLabel(String label) {
		this.setValue(label);
	}
}
