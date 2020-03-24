package eu.europeana.annotation.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.europeana.annotation.web.response.AbstractSearchResults;

@JsonInclude(Include.NON_NULL)
public class AnnotationSearchResults<T> extends AbstractSearchResults<T> {

	public AnnotationSearchResults(String apiKey, String action){
		super(apiKey, action);
	}
}
