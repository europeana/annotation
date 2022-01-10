package eu.europeana.annotation.statistics.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationMetric {

	@JsonProperty(AnnotationStatisticsConstants.PER_SCENARIO)
	AnnotationStatistics annotationStatisticsScenarios;

	@JsonProperty(AnnotationStatisticsConstants.PER_CLIENT)
	Map<String, Long> annotationStatisticsClients;

	@JsonProperty(AnnotationStatisticsConstants.PER_USER)
	Map<String, Long> annotationStatisticsUsers;

	public AnnotationStatistics getAnnotationStatisticsScenarios() {
		return annotationStatisticsScenarios;
	}

	public void setAnnotationStatisticsScenarios(AnnotationStatistics annotationStatisticsScenarios) {
		this.annotationStatisticsScenarios = annotationStatisticsScenarios;
	}
	
	public Map<String, Long> getAnnotationStatisticsClients() {
		return annotationStatisticsClients;
	}

	public void setAnnotationStatisticsClients(Map<String, Long> annotationStatisticsClients) {
		this.annotationStatisticsClients = annotationStatisticsClients;
	}

	public Map<String, Long> getAnnotationStatisticsUsers() {
		return annotationStatisticsUsers;
	}

	public void setAnnotationStatisticsUsers(Map<String, Long> annotationStatisticsUsers) {
		this.annotationStatisticsUsers = annotationStatisticsUsers;
	}
}