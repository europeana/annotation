package eu.europeana.annotation.web.service;

import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.web.exception.HttpException;

public interface AnnotationSearchService {

	public String getComponentName();

	/**
	 * This method retrieves all not disabled annotations for given target.
	 * 
	 * @param targetUrl
	 * @return the list of not disabled annotations
	 * @throws HttpException 
	 */
	public ResultSet<? extends AnnotationView> search(Query query) throws HttpException;

}
