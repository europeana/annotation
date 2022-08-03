package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import eu.europeana.annotation.definitions.model.Annotation;


public interface SolrAnnotation extends Annotation {

	
	Date getCreated();

	void setCreated(Date annotatedAt);

	void setAnnoUri(String annoUri);

	void setBodyValue(String bodyValue);

	String getBodyValue();

	String getAnnoUri();

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

	void setBodyMultilingualValue(Map<String, String> multilingualText);
	
	void setScenario(String scenario);

	String getScenario();

	
}
