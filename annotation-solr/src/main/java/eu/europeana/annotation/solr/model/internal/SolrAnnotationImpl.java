package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.SkosConceptBody;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * TODO: Change the implementation to use an adapter pattern.
 *  
 * @author GordeaS
 *
 */
public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation, SolrAnnotationConstants {

	private String annoUri;
	private String annoId;
	
	private String generatorUri;
	private String generatorName;
	
	private String creatorUri;
	private String creatorName;
	
	private Integer moderationScore = 0;
	private String motivationKey;
	
	private List<String> targetUris;
	private List<String> targetRecordIds;
	
	private String linkResourceUri;
	private String linkRelation;
	
	private String bodyValue;
	private List<String> bodyUris;
	
	/**
	 * public default constructor
	 */
	public SolrAnnotationImpl(){
		
	}
	
	/**
	 * Constructor using web annotation and moderation summary
	 */
	public SolrAnnotationImpl(Annotation annotation, Summary summary){
		
		this.setInternalType(annotation.getInternalType());
		this.setMotivation(annotation.getMotivation());
		
		this.setAnnotationId(annotation.getAnnotationId());
		this.setAnnoUri(annotation.getAnnotationId().getHttpUrl());
		this.setAnnoId(annotation.getAnnotationId().toRelativeUri());
		
		if(annotation.getGenerator() != null){
			this.setGenerator(annotation.getGenerator());
			this.setGeneratorUri(annotation.getGenerator().getHttpUrl());
			this.setGeneratorName(annotation.getGenerator().getName());
		}
		
		this.setGenerated(annotation.getGenerated());
		
		this.setCreator(annotation.getCreator());
		this.setCreatorName(annotation.getCreator().getName());
		this.setCreatorUri(annotation.getCreator().getHttpUrl());
		
		this.setCreated(annotation.getCreated());
		//modified is alias to lastUpdate
		this.setLastUpdate(annotation.getLastUpdate());
		
		if (summary != null)
			this.setModerationScore(summary.getScore());
		
		this.setTarget(annotation.getTarget());
		this.setBody(annotation.getBody());
		
//		this.setStyledBy(annotation.getStyledBy());
//		this.setCanonical(annotation.getCanonical());
//		this.setVia(annotation.getVia());
	}
	
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
	@Field(INTERNAL_TYPE)
	public void setInternalType(String internalType) {
		this.internalType = internalType;
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
	@Field(GENERATOR_URI)
	public void setGeneratorUri(String generatorId) {
		this.generatorUri = generatorId;
	}

	@Override
	public String getAnnoId() {
		return annoId;
	}

	@Field(ANNO_ID)
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

	@Override
	public List<String> getBodyUris() {
		return bodyUris;
	}

	@Override
	@Field(BODY_URI)
	public void setBodyUris(List<String> bodyUris) {
		this.bodyUris = bodyUris;
	}

}
