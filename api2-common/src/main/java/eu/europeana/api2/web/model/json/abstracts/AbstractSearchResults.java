package eu.europeana.api2.web.model.json.abstracts;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
@JsonSerialize(include = Inclusion.NON_EMPTY)
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
