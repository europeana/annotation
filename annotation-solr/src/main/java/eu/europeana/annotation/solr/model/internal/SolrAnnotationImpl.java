package eu.europeana.annotation.solr.model.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationFields;

public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation {

	private String annotationIdUrl;
	private List<String> targetUrls;
	private List<String> targetRecordIds;
	private String motivationKey;
	private String bodyValue;
	private String bodyInternalTypeKey;
	private String targetInternalTypeKey;
	private String annotatedByString;
	private String internalTypeKey;
	private String bodyTagId;
	private Long updatedTimestamp;
	private Long annotatedAtTimestamp;
	private Long serializedAtTimestamp;
	
	
	@Override
	public Long getAnnotatedAtTimestamp() {
		return annotatedAtTimestamp;
	}

	@Override
	@Field("annotatedAt_timestamp")
	public void setAnnotatedAtTimestamp(Long annotatedAtTimestamp) {
		this.annotatedAtTimestamp = annotatedAtTimestamp;
	}

	@Override
	public Long getSerializedAtTimestamp() {
		return serializedAtTimestamp;
	}

	@Override
	@Field("serializedAt_timestamp")
	public void setSerializedAtTimestamp(Long serializedAtTimestamp) {
		this.serializedAtTimestamp = serializedAtTimestamp;
	}

	public String getBodyValue() {
		return bodyValue;
	}

	@Override
	public List<String> getTargetUrls() {
		return targetUrls;
	}

	@Override
	@Field("target_urls")
	public void setTargetUrls(List<String> targetUrls) {
		this.targetUrls = targetUrls;
	}

	@Override
	public List<String> getTargetRecordIds() {
		return targetRecordIds;
	}

	@Override
	@Field("target_record_ids")
	public void setTargetRecordIds(List<String> recordIds) {
		this.targetRecordIds = recordIds;
	}

	@Override
	public String getMotivationKey() {
		return motivationKey;
	}

	@Override
	@Field("motivation_key")
	public void setMotivationKey(String motivationKey) {
		this.motivationKey = motivationKey;
	}

	@Override
	public String getBodyInternalTypeKey() {
		return bodyInternalTypeKey;
	}

	@Override
	@Field("body_internal_type_key")
	public void setBodyInternalTypeKey(String bodyInternalTypeKey) {
		this.bodyInternalTypeKey = bodyInternalTypeKey;
	}

	@Override
	public String getTargetInternalTypeKey() {
		return targetInternalTypeKey;
	}

	@Override
	@Field("target_internal_type_key")
	public void setTargetInternalTypeKey(String targetInternalTypeKey) {
		this.targetInternalTypeKey = targetInternalTypeKey;
	}

	@Override
	public String getAnnotationIdUrl() {
		return annotationIdUrl;
	}

	
	@Override
	@Field("body_tag_id")
	public void setBodyTagId(String id) {
		this.bodyTagId = id;
	}

	@Override
	public String getBodyTagId() {
		return bodyTagId;
	}

	
	@Override
	@Field("body_value")
	public void setBodyValue(String bodyValue) {

		this.bodyValue = bodyValue;
	}

	// @Field("*_multilingual")
	// protected Map<String, String> multiLingual;

	public Map<String, String> getMultilingual() {
		Map<String, String> res = new HashMap<String, String>();
		if (getBody() != null && getBody().getMultilingual() != null)
			res = getBody().getMultilingual();
		return res;
		// return multiLingual;
	}

//	@Field("*_multilingual")
	public void setMultilingual(Map<String, String> multilingual) {
		// if (super.getBody() == null) {
		// Body body =
		// BodyObjectFactory.getInstance().createModelObjectInstance(
		// BodyTypes.SEMANTIC_TAG.name());
		// body.setMultilingual(multilingual);
		// super.setBody(body);
		// } else {
		if (super.getBody() != null)
			super.getBody().setMultilingual(multilingual);
		// }
		// this.multiLingual = multiLingual;
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
	@Field("annotation_id_url")
	public void setAnnotationIdUrl(String annotationIdUrl) {
		this.annotationIdUrl = annotationIdUrl;
	}

	@Override
	//@Field("annotatedBy_string")
	public void setAnnotatedByString(String annotatedBy) {
		this.annotatedByString = annotatedBy;
	}

	@Override
	public String getAnnotatedByString() {
		return annotatedByString;
	}

	@Override
	@Field(SolrAnnotationFields.INTERNAL_TYPE_KEY)
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
	@Field("updated_timestamp")
	public void setUpdatedTimestamp(Long updatedTimestamp) {
		this.updatedTimestamp = updatedTimestamp;
	}

		
	public String toString() {
		return "SolrAnnotation [annotationIdUrl:" + getAnnotationIdUrl() + ", annotationIdUrl:" + getAnnotationIdUrl()
				+ ", annotatedAt:" + getAnnotatedAt() + ", bodyValue:" + getBodyValue() + "]";
	}

}
