package eu.europeana.annotation.solr.model.internal;

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

	private String annotationIdUrl;
	private List<String> targetUrls;
	private List<String> targetRecordIds;
	private String motivationKey;
	private String generatorName;
	private String generatorId;
	private String bodyValue;
	private String bodyInternalTypeKey;
	private String targetInternalTypeKey;
	private String creatorString;
	private String internalTypeKey;
	private String bodyTagId;
	private Long updatedTimestamp;
	private Long createdTimestamp;
	private Long generatedTimestamp;
	private Long moderationScore;
	
	
	@Override
	public Long getCreatedTimestamp() {
		return createdTimestamp;
	}

	@Override
	@Field(CREATED_TIMESTAMP)
	public void setCreatedTimestamp(Long annotatedAtTimestamp) {
		this.createdTimestamp = annotatedAtTimestamp;
	}

	@Override
	public Long getGeneratedTimestamp() {
		return generatedTimestamp;
	}

	@Override
	@Field(GENERATED_TIMESTAMP)
	public void setGeneratedTimestamp(Long serializedAtTimestamp) {
		this.generatedTimestamp = serializedAtTimestamp;
	}

	public String getBodyValue() {
		return bodyValue;
	}

	@Override
	public List<String> getTargetUrls() {
		return targetUrls;
	}

	@Override
	@Field(TARGET_ID)
	public void setTargetUrls(List<String> targetUrls) {
		this.targetUrls = targetUrls;
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
	public String getBodyInternalTypeKey() {
		return bodyInternalTypeKey;
	}

	@Override
	@Field(BODY_INTERNAL_TYPE)
	public void setBodyInternalTypeKey(String bodyInternalTypeKey) {
		this.bodyInternalTypeKey = bodyInternalTypeKey;
	}

	@Override
	public String getTargetInternalTypeKey() {
		return targetInternalTypeKey;
	}

	@Override
	@Field(TARGET_INTERNAL_TYPE)
	public void setTargetInternalTypeKey(String targetInternalTypeKey) {
		this.targetInternalTypeKey = targetInternalTypeKey;
	}

	@Override
	public String getAnnotationIdUrl() {
		return annotationIdUrl;
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
	@Field(ANNOTATION_ID_URL)
	public void setAnnotationIdUrl(String annotationIdUrl) {
		this.annotationIdUrl = annotationIdUrl;
	}

	@Override
	//@Field("creator_string")
	public void setCreatorString(String annotatedBy) {
		this.creatorString = annotatedBy;
	}

	@Override
	public String getCreatorString() {
		return creatorString;
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
	public Long getUpdatedTimestamp() {
		return updatedTimestamp;
	}

	@Override
	@Field(UPDATED_TIMESTAMP)
	public void setUpdatedTimestamp(Long updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}

	@Override
	@Field(MODERATION_SCORE)
	public void setModerationScore(Long moderationScore) {
		this.moderationScore = moderationScore;
	}

	@Override
	public Long getModerationScore() {
		return moderationScore;
	}

		
	public String toString() {
		return "SolrAnnotation [annotationIdUrl:" + getAnnotationIdUrl() + ", annotationIdUrl:" + getAnnotationIdUrl()
				+ ", annotatedAt:" + getCreated() + ", bodyValue:" + getBodyValue() + "]";
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
	public String getGeneratorId() {
		return generatorId;
	}

	@Override
	@Field(GENERATOR_ID)
	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}

}
