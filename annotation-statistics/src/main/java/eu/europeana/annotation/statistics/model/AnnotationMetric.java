package eu.europeana.annotation.statistics.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationMetric {
    
	@JsonProperty(AnnotationStatisticsConstants.CREATED)
    private Date timestamp;

	@JsonProperty(AnnotationStatisticsConstants.PER_CLIENT)
	List<AnnotationStatistics> annotationStatisticsClients;

	@JsonProperty(AnnotationStatisticsConstants.PER_USER)
	List<AnnotationStatistics> annotationStatisticsUsers;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

	public List<AnnotationStatistics> getAnnotationStatisticsClients() {
		return annotationStatisticsClients;
	}

	public void setAnnotationStatisticsClients(List<AnnotationStatistics> annotationStatisticsClients) {
		this.annotationStatisticsClients = annotationStatisticsClients;
	}

	public List<AnnotationStatistics> getAnnotationStatisticsUsers() {
		return annotationStatisticsUsers;
	}

	public void setAnnotationStatisticsUsers(List<AnnotationStatistics> annotationStatisticsUsers) {
		this.annotationStatisticsUsers = annotationStatisticsUsers;
	}

	
}