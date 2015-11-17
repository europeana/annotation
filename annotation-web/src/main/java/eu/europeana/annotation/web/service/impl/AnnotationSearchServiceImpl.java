package eu.europeana.annotation.web.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.service.AnnotationSearchService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class AnnotationSearchServiceImpl implements AnnotationSearchService {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	SolrAnnotationService solrService;
	
	@Resource
	AuthenticationService authenticationService;

	Logger logger = Logger.getLogger(getClass());

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public String getComponentName() {
		return configuration.getComponentName();
	}

	protected AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public SolrAnnotationService getSolrService() {
		return solrService;
	}

	public void setSolrService(SolrAnnotationService solrService) {
		this.solrService = solrService;
	}

	@Override
	public ResultSet<? extends AnnotationView> search(Query query) throws HttpException {
		
		try {
			return getSolrService().search(query);
		} catch (AnnotationServiceException e) {
			throw new HttpException("Solr Search Exception", HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	
}
