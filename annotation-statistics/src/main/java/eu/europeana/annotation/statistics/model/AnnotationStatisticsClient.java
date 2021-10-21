package eu.europeana.annotation.statistics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationStatisticsClient extends AnnotationStatistics {
	    
    public AnnotationStatisticsClient() {
		super();
		this.client="";
	}

    @JsonProperty(AnnotationStatisticsConstants.CLIENT)
    private String client;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}
}