package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;


public interface SolrAnnotation extends Annotation {

	
	Date getCreated();

	void setCreated(Date annotatedAt);

	void setAnnoUri(String annoUri);

	void setBodyValue(String bodyValue);

	String getBodyValue();

	String getAnnoUri();

	void setMotivationKey(String motivationKey);

	String getMotivationKey();

	void setTargetRecordIds(List<String> recordIds);

	List<String> getTargetRecordIds();

	void setTargetUris(List<String> targetUris);

	List<String> getTargetUris();

	public void setModerationScore(Integer moderationScore);
	
	public Integer getModerationScore();

	void setGeneratorUri(String generatorUri);

	String getGeneratorUri();

	void setGeneratorName(String generatorName);

	String getGeneratorName();

	void setCreatorName(String creatorName);

	String getCreatorName();

	void setCreatorUri(String creatorUri);

	String getCreatorUri();

	void setAnnoId(long annoId);

	long getAnnoId();

	void setModified(Date modified);

	Date getModified();

	void setLinkRelation(String linkRelation);

	String getLinkRelation();

	void setLinkResourceUri(String linkResourceUri);

	String getLinkResourceUri();

	void setBodyUris(List<String> bodyUris);

	List<String> getBodyUris();

	/**
	 * This method adds a new language/label association to the multilingual
	 * map.
	 * 
	 * @param language
	 * @param label
	 */
	void addMultilingualValue(String language, String label);
	
	void setScenario(String scenario);

	String getScenario();

	
}
