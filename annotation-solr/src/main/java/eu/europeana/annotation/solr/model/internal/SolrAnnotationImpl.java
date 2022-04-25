package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.solr.client.solrj.beans.Field;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

/**
 * TODO: Change the implementation to use an adapter pattern.
 *  
 * @author GordeaS
 *
 */
public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation, SolrAnnotationConstants {

    @Resource
    AnnotationConfiguration configuration;
  
	private String annoUri;
	private long annoId;
	
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
	private Map<String, String> bodyMultilingualValue;
	private List<String> bodyUris;
	
	private String scenario;
	
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

		this.setIdentifier(annotation.getIdentifier());
		this.setAnnoUri(String.valueOf(annotation.getIdentifier()));
		this.setAnnoId(annotation.getIdentifier());
		
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
		
		this.setScenario(findScenarioType(annotation));
		
//		this.setStyledBy(annotation.getStyledBy());
//		this.setCanonical(annotation.getCanonical());
//		this.setVia(annotation.getVia());
	}
	
	private String findScenarioType(Annotation anno) {		
		switch(anno.getMotivationType()) {
		case TRANSCRIBING:
			return AnnotationScenarioTypes.TRANSCRIPTION;
		case CAPTIONING:
			return AnnotationScenarioTypes.CAPTION;
		case SUBTITLING:
			return AnnotationScenarioTypes.SUBTITLE;
		case TAGGING:
			if(anno.getBody().getInternalType().equalsIgnoreCase(BodyInternalTypes.SEMANTIC_TAG.toString())) {
				return AnnotationScenarioTypes.SEMANTIC_TAG;
			}
			else if(anno.getBody().getInternalType().equalsIgnoreCase(BodyInternalTypes.TAG.toString())) {
				return AnnotationScenarioTypes.SIMPLE_TAG;
			}
			else if(anno.getBody().getInternalType().equalsIgnoreCase(BodyInternalTypes.GEO_TAG.toString())) {
				return AnnotationScenarioTypes.GEO_TAG;
			}
		case LINKING:
			return AnnotationScenarioTypes.OBJECT_LINK;
	    case LINKFORCONTRIBUTING:
            return AnnotationScenarioTypes.CONTRIBUTE_LINK;
		default:
			return "";
		}		
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

	@Override
	@Field(BODY_VALUE_ALL)
	public void setBodyMultilingualValue(Map<String, String> multilingualText) {
	    //TODO: convert to language map by removing field when required  
	    this.bodyMultilingualValue = multilingualText;		
	}
	
	public Map<String, String> getBodyMultilingualValue() {
	    return bodyMultilingualValue;
	}

	@Override
	@Field(ANNO_URI)
	public void setAnnoUri(String annotationIdUrl) {
		this.annoUri = annotationIdUrl;
	}
	
	@Override
//	@Field(INTERNAL_TYPE)
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
	public long getAnnoId() {
		return annoId;
	}

	@Field(ANNO_ID)
	@Override
	public void setAnnoId(long annoId) {
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
	
	@Override
	public String getScenario() {
		return scenario;
	}

	@Override
	@Field(SCENARIO)
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

}
