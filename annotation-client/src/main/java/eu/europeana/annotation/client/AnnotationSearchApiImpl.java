package eu.europeana.annotation.client;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.AnnotationSearchResults;
import eu.europeana.annotation.client.model.result.TagSearchResults;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.impl.TagResource;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationSearchApiImpl extends BaseAnnotationApi implements AnnotationSearchApi {

	public AnnotationSearchApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public AnnotationSearchApiImpl(){
		super();
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query) {
//		List<? extends Annotation> res;
		AnnotationSearchResults res;
		try {
//			query = addFieldToQuery(query, null, null);
			res = apiConnection.search(query);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res.getItems();
	}

	@Override
	public List<? extends Annotation> searchAnnotations(
			String query, String startOn, String limit, String field, String language) {
		
		AnnotationSearchResults res;
		try {
			if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language)) 
				query = addFieldToQuery(query, field, language);
			res = apiConnection.search(query, startOn, limit);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res.getItems();
	}

	@Override
	public List<? extends TagResource> searchTags(String query) {
		TagSearchResults res;
		try {
			res = apiConnection.searchTags(query);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res.getItems();
	}

	@Override
	public List<? extends TagResource> searchTags(
			String query, String startOn, String limit, String field, String language) {
		
		TagSearchResults res;
		try {
			if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language)) 
				query = addFieldToQuery(query, field, language);
			res = apiConnection.searchTags(query, startOn, limit);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res.getItems();
	}
	
	/**
	 * This method extends SOLR query by field and language if given.
	 * @param query
	 * @param field
	 * @param language
	 * @return extended query
	 */
	public static String addFieldToQuery(String query, String field, String language) {
		if (StringUtils.isNotEmpty(field)) {
//			if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
				String prefix = WebAnnotationFields.DEFAULT_LANGUAGE + WebAnnotationFields.UNDERSCORE;
				if (field.equals(WebAnnotationFields.MULTILINGUAL)) {
					if (StringUtils.isNotEmpty(language) 
							&& field.equals(WebAnnotationFields.MULTILINGUAL)) {
						prefix = language.toUpperCase() + WebAnnotationFields.UNDERSCORE;
					}
				} else {
					prefix = "";
				}
				query = prefix + field + "*:*" + query;
//			}
		} else {
			query = "*:*";
		}
		return query;
	}
				
}
