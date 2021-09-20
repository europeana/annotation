package eu.europeana.annotation.statistics.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsFields;

@JsonPropertyOrder({AnnotationStatisticsFields.CREATED, AnnotationStatisticsFields.ANNOTATIONS_STATISTICS})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationStatistics {

    @JsonProperty(AnnotationStatisticsFields.CREATED)
    private Date timestamp;

    @JsonProperty(AnnotationStatisticsFields.ANNOTATIONS_STATISTICS)
    private List<NumberAnnotationsPerScenario> annotationsStatistics;

    public class NumberAnnotationsPerScenario {
    	
        @JsonProperty(AnnotationStatisticsFields.ANNOTATIONS_SCENARIO)
        private String annotationsScenario;
        
        @JsonProperty(AnnotationStatisticsFields.NUMBER_ANNOTATONS)
        private String numberAnnotations;

		public String getAnnotationsScenario() {
			return annotationsScenario;
		}

		public void setAnnotationsScenario(String annotationsScenario) {
			this.annotationsScenario = annotationsScenario;
		}

		public String getNumberAnnotations() {
			return numberAnnotations;
		}

		public void setNumberAnnotations(String numberAnnotations) {
			this.numberAnnotations = numberAnnotations;
		}
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

	public List<NumberAnnotationsPerScenario> getAnnotationsStatistics() {
		return annotationsStatistics;
	}

	public void setAnnotationsStatistics(List<NumberAnnotationsPerScenario> annotationsStatistics) {
		this.annotationsStatistics = annotationsStatistics;
	}
   
}