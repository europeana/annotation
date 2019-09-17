package eu.europeana.annotation.web.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.europeana.api.commons.web.model.ApiResponse;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
@JsonSerialize()
@JsonInclude(content=Include.NON_EMPTY, value=Include.NON_EMPTY)
public class AbstractSearchResults<T> extends ApiResponse {

	public long itemsCount;

	public long totalResults;

	public List<T> items;

	public AbstractSearchResults(String apikey, String action) {
		super(apikey, action);
	}

	public AbstractSearchResults() {
		// used by Jackson
		super();
	}
}
