package eu.europeana.annotation.web.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.api.commons.web.exception.HttpException;

public interface AnnotationSearchService {

	public String getComponentName();

	/**
	 * This method retrieves all not disabled annotations for given target.
	 * 
	 * @param query
	 * @param request
	 * @return the list of not disabled annotations
	 * @throws HttpException 
	 */
	public AnnotationPage search(Query query, HttpServletRequest request) throws HttpException;
	
	/**
	 * This method retrieves all disabled Annotations.
	 * 
	 * @param query
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws HttpException
	 */
	public AnnotationPage searchDisabled(Query query, HttpServletRequest request, Date startDate, Date endDate)
			throws HttpException;


	public Query buildSearchQuery(String queryString, String[] filters, String[] facets, String sort, String sortOrder, int pageNr, int pageSize,
			SearchProfiles profile);

	
}
