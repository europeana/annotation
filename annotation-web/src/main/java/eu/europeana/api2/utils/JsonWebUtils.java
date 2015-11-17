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

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConst;

public class JsonWebUtils {
	
	private static final Logger log = Logger.getLogger(JSONUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static ModelAndView toJson(Object object) {
		return toJson(object, null);
	}
	
	public static ModelAndView toJson(String json, String callback) {
		String resultPage = "json";
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(resultPage, json);
		if (StringUtils.isNotBlank(callback)) {
			resultPage = "jsonp";
			model.put("callback", callback);
		}
		return new ModelAndView(resultPage, model);
	}

	public static ModelAndView toJson(Object object, String callback) {
		
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		String errorMessage = null;
		try {
			return toJson(objectMapper.writeValueAsString(object), callback);
		} catch (JsonGenerationException e) {
			log.error("Json Generation Exception: " + e.getMessage(),e);
			errorMessage = "Json Generation Exception: " + e.getMessage() + " See error logs!";
		} catch (JsonMappingException e) {
			log.error("Json Mapping Exception: " + e.getMessage(),e);
			errorMessage = "Json Generation Exception: " + e.getMessage() + " See error logs!";
		} catch (IOException e) {
			log.error("I/O Exception: " + e.getMessage(),e);
			errorMessage = "I/O Exception: " + e.getMessage() + " See error logs!";
		}
		//Report technical errors...
		String resultPage = "json";
		return new ModelAndView(resultPage, "errorMessage", errorMessage);
	}
	
	/**
	 * This method extends SOLR query by field and language if given.
	 * @deprecated needs to be re-implemented according to new schema and 
	 * @param query
	 * @param field
	 * @param language
	 * @return extended query
	 */
	public static String addFieldToQuery(String query, String field, String language) {
		if (StringUtils.isNotEmpty(field)) {
//			if (SolrAnnotationFields.contains(field)) {
				String prefix = WebAnnotationFields.DEFAULT_LANGUAGE + WebAnnotationFields.UNDERSCORE;
//				if (field.equals(SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
//					if (StringUtils.isNotEmpty(language) 
//							&& field.equals(SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
//						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
//					}
//				} else {
					prefix = "";
				//}
				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
//			}
		} else {
			query = SolrAnnotationConst.ALL_SOLR_ENTRIES;
		}
		return query;
	}
		
}
