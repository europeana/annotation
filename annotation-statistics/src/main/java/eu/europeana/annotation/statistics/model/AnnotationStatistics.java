package eu.europeana.annotation.statistics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationStatistics {
	    
    public AnnotationStatistics() {
		this.transcription = 0;
		this.geoTag = 0;
		this.objectLink = 0;
		this.semanticTag = 0;
		this.subtitle = 0;
	}

    @JsonProperty(AnnotationScenarioTypes.TRANSCRIPTION)
    private long transcription;
    
    @JsonProperty(AnnotationScenarioTypes.GEO_TAG)
    private long geoTag;
    
    @JsonProperty(AnnotationScenarioTypes.OBJECT_LINK)
    private long objectLink;
    
    @JsonProperty(AnnotationScenarioTypes.SEMANTIC_TAG)
    private long semanticTag;
    
    @JsonProperty(AnnotationScenarioTypes.SIMPLE_TAG)
    private long simpleTag;
    
    @JsonProperty(AnnotationScenarioTypes.SUBTITLE)
    private long subtitle;

	public long getTranscription() {
		return transcription;
	}

	public void setTranscription(long transcription) {
		this.transcription = transcription;
	}

	public long getGeoTag() {
		return geoTag;
	}

	public void setGeoTag(long geoTag) {
		this.geoTag = geoTag;
	}

	public long getObjectLink() {
		return objectLink;
	}

	public void setObjectLink(long objectLink) {
		this.objectLink = objectLink;
	}

	public long getSemanticTag() {
		return semanticTag;
	}

	public void setSemanticTag(long semanticTag) {
		this.semanticTag = semanticTag;
	}

	public long getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(long subtitle) {
		this.subtitle = subtitle;
	}

  public long getSimpleTag() {
    return simpleTag;
  }

  public void setSimpleTag(long simpleTag) {
    this.simpleTag = simpleTag;
  }
	
}