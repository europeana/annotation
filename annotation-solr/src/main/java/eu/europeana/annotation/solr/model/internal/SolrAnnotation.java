package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.Annotation;


public interface SolrAnnotation extends Annotation {

	

//	void setResourceId(String resourceId);
//	
//	String getResourceId();

//	public void setLanguage(String language);
//
//	public String getLanguage();
//
//	public void setHttpUri(String httpUri);
//
//	public String getHttpUri();
	
	public void setInternalTypeKey(String annotation_type);
	
	public String getInternalTypeKey();
	
	Date getCreated();

	void setCreated(Date annotatedAt);

	String getCreatorString();

	void setCreatorString(String annotatedBy);

	void setAnnotationIdUrl(String annotationIdUrl);

	void setBodyValue(String bodyValue);

	String getBodyValue();

	String getBodyTagId();

	void setBodyTagId(String id);

	String getAnnotationIdUrl();

	void setTargetInternalTypeKey(String targetInternalTypeKey);

	String getTargetInternalTypeKey();

	void setBodyInternalTypeKey(String bodyInternalTypeKey);

	String getBodyInternalTypeKey();

	void setMotivationKey(String motivationKey);

	String getMotivationKey();

	void setTargetRecordIds(List<String> recordIds);

	List<String> getTargetRecordIds();

	void setTargetUrls(List<String> targetUrls);

	List<String> getTargetUrls();

	void setUpdatedTimestamp(Long updatedTimestamp);

	Long getUpdatedTimestamp();

	void setGeneratedTimestamp(Long serializedAtTimestamp);

	Long getGeneratedTimestamp();

	void setCreatedTimestamp(Long annotatedAtTimestamp);

	Long getCreatedTimestamp();

	public void setModerationScore(Long moderationScore);
	
	public Long getModerationScore();

	void setGeneratorId(String generatorId);

	String getGeneratorId();

	void setGeneratorName(String generatorName);

	String getGeneratorName();
	
}
