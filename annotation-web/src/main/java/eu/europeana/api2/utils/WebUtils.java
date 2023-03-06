package eu.europeana.api2.utils;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;

/**
 * @author GordeaS
 *
 */
public class WebUtils {
	
	private static final Logger log = LogManager.getLogger(WebUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String toJson(Object object) {
//		public static ModelAndView toJson(Object object) {
		return toJson(object, null);
	}
	
//	public static ModelAndView toJson(String json, String callback) {
//		String resultPage = "json";
//		Map<String, Object> model = new HashMap<String, Object>();
//		model.put(resultPage, json);
//		if (StringUtils.isNotBlank(callback)) {
//			resultPage = "jsonp";
//			model.put("callback", callback);
//		}
//		return new ModelAndView(resultPage, model);
//	}

	public static String toJson(Object object, String callback) {
//		public static ModelAndView toJson(Object object, String callback) {
		return toJson(object, callback, false, -1);
	}
		
	public static String toJson(Object object, String callback, boolean shortObject, int objectId) {
//		public static ModelAndView toJson(Object object, String callback, boolean shortObject, int objectId) {
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		String errorMessage = null;
		try {
			String jsonStr = objectMapper.writeValueAsString(object);	
			if (shortObject) {
				String idBeginStr = "id\":{";
				int startIdPos = jsonStr.indexOf(idBeginStr);
				int endIdPos = jsonStr.indexOf("}", startIdPos);
				jsonStr = jsonStr.substring(0, startIdPos) + idBeginStr.substring(0, idBeginStr.length() - 1) 
				    + Integer.valueOf(objectId) + jsonStr.substring(endIdPos + 1);
			}
//			return toJson(jsonStr, callback);
			return jsonStr;
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
//		String resultPage = "json";
//		return new ModelAndView(resultPage, "errorMessage", errorMessage);
		return errorMessage;
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
				query = prefix + field + SolrSyntaxConstants.DELIMETER + query;
//			}
		} else {
			query = SolrSyntaxConstants.ALL_SOLR_ENTRIES;
		}
		return query;
	}
	
	public static boolean checkRights(Authentication authentication, String userRole) {
    	for (Iterator<? extends GrantedAuthority> iterator = authentication.getAuthorities().iterator(); iterator.hasNext();) {
    	    //role based authorization
    	    String role = iterator.next().getAuthority();
    	    if(role!=null && role.equals(userRole)){
    	    	return true;
    	    }
    	}
    	return false;
	}	
}
