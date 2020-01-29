package eu.europeana.annotation.web.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import eu.europeana.annotation.web.response.AbstractSearchResults;

@JsonSerialize(include = Inclusion.NON_EMPTY)
public class WhitelsitSearchResults<T> extends AbstractSearchResults<T> {

	public WhitelsitSearchResults(String apiKey, String action){
		super(apiKey, action);
	}
}
