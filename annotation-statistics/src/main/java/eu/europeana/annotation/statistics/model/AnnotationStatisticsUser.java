package eu.europeana.annotation.statistics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationStatisticsUser extends AnnotationStatistics {
	    
    public AnnotationStatisticsUser() {
		super();
		this.user="";
	}

    @JsonProperty(AnnotationStatisticsConstants.USER)
    private String user;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}