package eu.europeana.annotation.client;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
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
	public AnnotationPage searchAnnotations(String query) {
//		List<? extends Annotation> res;
		AnnotationPage res;
		try {
//			query = addFieldToQuery(query, null, null);
			res = apiConnection.search(query);
		} catch (IOException | JsonParseException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res;
	}
	
	@Override
	@Deprecated
	public AnnotationPage searchAnnotations(String query, SearchProfiles searchProfile, String language) {
		return searchAnnotations(query, null, null, null, null, searchProfile, language);
	}
	
	@Override
        public AnnotationPage searchAnnotations(String query, String qf, String sort, String sortOrder, String page,
    	    String pageSize, SearchProfiles searchProfile, String language) {
        	AnnotationPage res;
        	try {
        	    res = apiConnection.search(query, qf, sort, sortOrder, page, pageSize, searchProfile, language);
        	} catch (IOException | JsonParseException e) {
        	    throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
        	}
        
        	return res;
        }

	@Override
	@Deprecated
	public AnnotationPage searchAnnotations(
			String query, String page, String pageSize, String field, String language) {
		
		return searchAnnotations(query, page, pageSize, field, language, SearchProfiles.STANDARD, null);
	}
	
	
	@Override
	public AnnotationPage searchAnnotations(
			String query, String page, String pageSize, String field, String language, SearchProfiles searchProfile, String paramLanguage) {
		
		AnnotationPage res;
		try {
			if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language)) 
				query = addFieldToQuery(query, field, language);
			res = apiConnection.search(query, page, pageSize, searchProfile, paramLanguage);
		} catch (IOException | JsonParseException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
		}

		return res;
	}
	

//	@Override
//	public List<? extends TagResource> searchTags(String query) {
//		TagSearchResults res;
//		try {
//			res = apiConnection.searchTags(query);
//		} catch (IOException e) {
//			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
//		}
//
//		return res.getItems();
//	}

//	@Override
//	public List<? extends TagResource> searchTags(
//			String query, String startOn, String limit, String field, String language) {
//		
//		TagSearchResults res;
//		try {
//			if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language)) 
//				query = addFieldToQuery(query, field, language);
//			res = apiConnection.searchTags(query, startOn, limit);
//		} catch (IOException e) {
//			throw new TechnicalRuntimeException("Exception occured when invoking the AnnotationSearchApi", e);
//		}
//
//		return res.getItems();
//	}
//	
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
