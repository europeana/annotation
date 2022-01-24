package eu.europeana.annotation.statistics.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationMetric {
  
    @JsonProperty(AnnotationStatisticsConstants.CREATED)
    Date created;

	@JsonProperty(AnnotationStatisticsConstants.PER_SCENARIO)
	AnnotationStatisticsScenarios scenariosTotal;

	@JsonProperty(AnnotationStatisticsConstants.PER_CLIENT)
	Map<String, Long> clientsTotal;

	@JsonProperty(AnnotationStatisticsConstants.PER_USER)
	Map<String, Long> usersTotal;

	@JsonProperty(AnnotationStatisticsConstants.PER_CLIENT_SCENARIO)
    List<AnnotationStatisticsClientsScenarios> clientsScenarios;
	
	@JsonProperty(AnnotationStatisticsConstants.PER_USER_SCENARIO)
    List<AnnotationStatisticsUsersScenarios> usersScenarios;

  public AnnotationStatisticsScenarios getScenariosTotal() {
    return scenariosTotal;
  }

  public void setScenariosTotal(AnnotationStatisticsScenarios scenariosTotal) {
    this.scenariosTotal = scenariosTotal;
  }

  public Map<String, Long> getClientsTotal() {
    return clientsTotal;
  }

  public void setClientsTotal(Map<String, Long> clientsTotal) {
    this.clientsTotal = clientsTotal;
  }

  public Map<String, Long> getUsersTotal() {
    return usersTotal;
  }

  public void setUsersTotal(Map<String, Long> usersTotal) {
    this.usersTotal = usersTotal;
  }

  public List<AnnotationStatisticsClientsScenarios> getClientsScenarios() {
    return clientsScenarios;
  }

  public void setClientsScenarios(List<AnnotationStatisticsClientsScenarios> clientsScenarios) {
    this.clientsScenarios = clientsScenarios;
  }

  public List<AnnotationStatisticsUsersScenarios> getUsersScenarios() {
    return usersScenarios;
  }

  public void setUsersScenarios(List<AnnotationStatisticsUsersScenarios> usersScenarios) {
    this.usersScenarios = usersScenarios;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
	

}