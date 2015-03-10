package eu.europeana.api2.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.web.servlet.ModelAndView;

import com.github.jsonldjava.utils.JSONUtils;

import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;

public class JsonWebUtils {
	
	private static final Logger log = Logger.getLogger(JSONUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static ModelAndView toJson(Object object) {
		return toJson(object, null);
	}
	
	public static ModelAndView toJson(String json, String callback) {
		String resultPage = "json";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("json", json);
		if (StringUtils.isNotBlank(callback)) {
			resultPage = "jsonp";
			model.put("callback", callback);
		}
		return new ModelAndView(resultPage, model);
	}

	public static ModelAndView toJson(Object object, String callback) {
		
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		try {
			return toJson(objectMapper.writeValueAsString(object), callback);
		} catch (JsonGenerationException e) {
			log.error("Json Generation Exception: " + e.getMessage(),e);
		} catch (JsonMappingException e) {
			log.error("Json Mapping Exception: " + e.getMessage(),e);
		} catch (IOException e) {
			log.error("I/O Exception: " + e.getMessage(),e);
		}
		// TODO report error...
		String resultPage = "json";
		Map<String, Object> model = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(callback)) {
			resultPage = "jsonp";
			model.put("callback", callback);
		}
		return new ModelAndView(resultPage, model);
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
			if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
				String prefix = SolrAnnotationConst.DEFAULT_LANGUAGE + SolrAnnotationConst.UNDERSCORE;
				if (field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
					if (StringUtils.isNotEmpty(language) 
							&& field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
					}
				} else {
					prefix = "";
				}
				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
			}
		} else {
			query = SolrAnnotationConst.ALL_SOLR_ENTRIES;
		}
		return query;
	}
		
}
