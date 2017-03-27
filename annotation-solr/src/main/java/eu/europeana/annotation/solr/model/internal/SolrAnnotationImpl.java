package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.body.SkosConceptBody;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * Change the implementation to use an adapter pattern.
 * @Deprecated 
 * @author GordeaS
 *
 */
public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation, SolrAnnotationConstants {

	private String annoUri;
	private String annoId;
	
	private List<String> targetUris;
	private List<String> targetRecordIds;
	private String motivationKey;
	private String generatorName;
	private String generatorUri;
	private String bodyValue;
	private String creatorUri;
	private String creatorName;
	
	private String internalTypeKey;
	private String bodyTagId;
	private Integer moderationScore;
	
	private String linkResourceUri;
	private String linkRelation;
	
	
	@Override
	@Field(CREATED)
	public void setCreated(Date created) {
		super.setCreated(created);
	}
	
	@Override
	@Field(GENERATED)
	public void setGenerated(Date generated) {
		super.setGenerated(generated);
	}

	public String getBodyValue() {
		return bodyValue;
	}

	@Override
	public List<String> getTargetUris() {
		return targetUris;
	}

	@Override
	@Field(TARGET_URI)
	public void setTargetUris(List<String> targetUris) {
		this.targetUris = targetUris;
	}

	@Override
	public List<String> getTargetRecordIds() {
		return targetRecordIds;
	}

	@Override
	@Field(TARGET_RECORD_ID)
	public void setTargetRecordIds(List<String> recordIds) {
		this.targetRecordIds = recordIds;
	}

	@Override
	public String getMotivationKey() {
		return motivationKey;
	}

	@Override
	@Field(MOTIVATION)
	public void setMotivationKey(String motivationKey) {
		this.motivationKey = motivationKey;
	}

	@Override
	public String getAnnoUri() {
		return annoUri;
	}

	
	@Override
	@Field(BODY_TAG_ID)
	public void setBodyTagId(String id) {
		this.bodyTagId = id;
	}

	@Override
	public String getBodyTagId() {
		return bodyTagId;
	}

	
	@Override
	@Field(BODY_VALUE)
	public void setBodyValue(String bodyValue) {

		this.bodyValue = bodyValue;
	}

	public Map<String, String> getMultilingual() {
		
		
		if(getBody()!=null && (getBody() instanceof SkosConceptBody))
			return ((SkosConceptBody)getBody()).getMultilingual();
		
		return null;		
	}

//	@Field("*_multilingual")
	public void setMultilingual(Map<String, String> multilingual) {

		//TODO: re-implement when needed
//		if (super.getBody() != null)
//			super.getBody().setMultilingual(multilingual);
		
	}

	/**
	 * This method adds a new language/label association to the multilingual
	 * map.
	 * 
	 * @param language
	 * @param label
	 */
	public void addLabelInMapping(String language, String label) {
		getMultilingual().put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
		// if(this.multiLingual == null) {
		// this.multiLingual = new HashMap<String, String>();
		// }
		// this.multiLingual.put(language + "_multilingual", label);
	}

	@Override
	@Field(ANNO_URI)
	public void setAnnoUri(String annotationIdUrl) {
		this.annoUri = annotationIdUrl;
	}

	@Override
	@Field(SolrAnnotationConstants.INTERNAL_TYPE)
	public void setInternalTypeKey(String internalTypeKey) {
		this.internalTypeKey = internalTypeKey;
	}

	@Override
	public String getInternalTypeKey() {
		return internalTypeKey;
	}

	
	@Override
	@Field(MODERATION_SCORE)
	public void setModerationScore(Integer moderationScore) {
		this.moderationScore = moderationScore;
	}

	@Override
	public Integer getModerationScore() {
		return moderationScore;
	}

		
	public String toString() {
		return "SolrAnnotation [anno_uri:" + getAnnoUri() + ", created:" + getCreated() + ", bodyValue:" + getBodyValue() + "]";
	}

	@Override
	public String getGeneratorName() {
		return generatorName;
	}

	@Override
	@Field(GENERATOR_NAME)
	public void setGeneratorName(String generatorName) {
		this.generatorName = generatorName;
	}

	@Override
	public String getGeneratorUri() {
		return generatorUri;
	}

	@Override
	@Field(GENERATOR_ID)
	public void setGeneratorUri(String generatorId) {
		this.generatorUri = generatorId;
	}

	@Override
	public String getAnnoId() {
		return annoId;
	}

	@Override
	public void setAnnoId(String annoId) {
		this.annoId = annoId;
	}
	
	@Override
	public String getCreatorUri() {
		return creatorUri;
	}

	@Override
	@Field(CREATOR_URI)
	public void setCreatorUri(String creatorUri) {
		this.creatorUri = creatorUri;
	}

	@Override
	public String getCreatorName() {
		return creatorName;
	}

	@Override
	@Field(CREATOR_NAME)
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	@Override
	public Date getModified() {
		return getLastUpdate();
	}

	@Override
	@Field(MODIFIED)
	public void setModified(Date modified) {
		setLastUpdate(modified);
	}

	@Override
	public String getLinkResourceUri() {
		return linkResourceUri;
	}

	@Override
	@Field(LINK_RESOURCE_URI)
	public void setLinkResourceUri(String linkResourceUri) {
		this.linkResourceUri = linkResourceUri;
	}

	@Override
	public String getLinkRelation() {
		return linkRelation;
	}

	@Override
	@Field(LINK_RELATION)
	public void setLinkRelation(String linkRelation) {
		this.linkRelation = linkRelation;
	}

}
