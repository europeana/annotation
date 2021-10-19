package eu.europeana.annotation.statistics.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonPropertyOrder({AnnotationStatisticsConstants.CREATED, AnnotationStatisticsConstants.ANNOTATIONS_STATISTICS})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationStatistics {
    
	@JsonProperty(AnnotationStatisticsConstants.CREATED)
    private Date timestamp;

	@JsonProperty(AnnotationStatisticsConstants.ANNOTATIONS_STATISTICS)
	List<AnnotationStatisticsElement> annotationStatistics;
	
	public class AnnotationStatisticsElement {
	    
	    public AnnotationStatisticsElement() {
			super();
			this.target = null;
			this.value = null;
			this.transcriptions = 0;
			this.geoTags = 0;
			this.objectLinks = 0;
			this.semanticTags = 0;
			this.subtitles = 0;
		}

		@JsonProperty(AnnotationStatisticsConstants.TARGET)
	    private String target;
	    
	    @JsonProperty(AnnotationStatisticsConstants.VALUE)
	    private String value;
	    
	    @JsonProperty(AnnotationScenarioTypes.TRANSCRIPTIONS)
	    private long transcriptions;
	    
	    @JsonProperty(AnnotationScenarioTypes.GEO_TAGS)
	    private long geoTags;
	    
	    @JsonProperty(AnnotationScenarioTypes.OBJECT_LINKS)
	    private long objectLinks;
	    
	    @JsonProperty(AnnotationScenarioTypes.SEMANTIC_TAGS)
	    private long semanticTags;
	    
	    @JsonProperty(AnnotationScenarioTypes.SUBTITLES)
	    private long subtitles;
	
		public String getTarget() {
			return target;
		}
	
		public void setTarget(String target) {
			this.target = target;
		}
	
		public String getValue() {
			return value;
		}
	
		public void setValue(String value) {
			this.value = value;
		}
	
		public long getTranscriptions() {
			return transcriptions;
		}
	
		public void setTranscriptions(long transcriptions) {
			this.transcriptions = transcriptions;
		}
	
		public long getGeoTags() {
			return geoTags;
		}
	
		public void setGeoTags(long geoTags) {
			this.geoTags = geoTags;
		}
	
		public long getObjectLinks() {
			return objectLinks;
		}
	
		public void setObjectLinks(long objectLinks) {
			this.objectLinks = objectLinks;
		}
	
		public long getSemanticTags() {
			return semanticTags;
		}
	
		public void setSemanticTags(long semanticTags) {
			this.semanticTags = semanticTags;
		}
	
		public long getSubtitles() {
			return subtitles;
		}
	
		public void setSubtitles(long subtitles) {
			this.subtitles = subtitles;
		}
	}
	
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

	public List<AnnotationStatisticsElement> getAnnotationStatistics() {
		return annotationStatistics;
	}

	public void setAnnotationStatistics(List<AnnotationStatisticsElement> annotationStatistics) {
		this.annotationStatistics = annotationStatistics;
	}
    
   
}