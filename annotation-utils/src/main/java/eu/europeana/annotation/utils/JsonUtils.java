package eu.europeana.annotation.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
//import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;
import eu.europeana.annotation.definitions.model.resource.state.impl.BaseState;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.CssStyle;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.utils.serialization.AgentDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.AgentDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.AnnotationDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.AnnotationIdDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.BodyDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.SelectorDeserializer;
//import eu.europeana.annotation.definitions.model.utils.serialization.TargetDeserializer;
//import eu.europeana.corelib.logging.Logger;
import eu.europeana.annotation.utils.serialization.AnnotationDeserializer;
import eu.europeana.annotation.utils.serialization.AnnotationIdDeserializer;
import eu.europeana.annotation.utils.serialization.BodyDeserializer;
import eu.europeana.annotation.utils.serialization.SelectorDeserializer;
import eu.europeana.annotation.utils.serialization.TargetDeserializer;

public class JsonUtils {
	
//	private static Logger log = Logger.getRootLogger();
//	private static final Logger log = Logger.getLogger(JSONUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static final JsonFactory jsonFactory = new JsonFactory();
	
	
	
	public static Annotation toAnnotationObject(String json) {
		JsonParser parser;
		Annotation annotation = null;
		try {
			parser = jsonFactory.createJsonParser(json);
			//DeserializationConfig cfg = DeserializerFactory.Config.
			SimpleModule module =  
			      new SimpleModule("AnnotationDeserializerModule",  
			          new Version(1, 0, 0, null));  
			    
			module.addDeserializer(Annotation.class, new AnnotationDeserializer());  
			module.addDeserializer(Target.class, new TargetDeserializer());  
			module.addDeserializer(Body.class, new BodyDeserializer());  
			module.addDeserializer(Agent.class, new AgentDeserializer());
			module.addDeserializer(Selector.class, new SelectorDeserializer());
			//module.addDeserializer(State.class, new StateDeserializer());
//			module.addDeserializer(Agent.class, new AgentDeserializer());
			module.addDeserializer(AnnotationId.class, new AnnotationIdDeserializer());
			
			//module.addDeserializer(Style.class, new StyleDeserializer());  
			module.addAbstractTypeMapping(Point.class, PointImpl.class); 
			module.addAbstractTypeMapping(Style.class, CssStyle.class); 
			module.addAbstractTypeMapping(State.class, BaseState.class);
			
//			module.addAbstractTypeMapping(Date.class, DateImpl.class); 

			objectMapper.registerModule(module); 
	
			
			parser.setCodec(objectMapper);
			annotation = objectMapper.readValue(parser, Annotation.class);
			
		} catch (JsonParseException e) {
			throw new AnnotationInstantiationException("Json formating exception!", e);
		} catch (IOException e) {
			throw new AnnotationInstantiationException("Json reading exception!", e);
		}
		
		return annotation;
	}
	
	public static String mapToString(Map<String,String> mp) {
		String res = "";
	    Iterator<Map.Entry<String, String>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
	        if (res.length() > 0) {
	        	res = res + ",";
	        }
	        res = res + pairs.getKey() + WebAnnotationFields.SEPARATOR_SEMICOLON + pairs.getValue();
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    if (res.length() > 0) {
	    	res = "[" + res + "]";
	    }
	    return res;
	}	
	
	public static String mapToStringExt(Map<String,Integer> mp) {
		String res = "";
	    Iterator<Map.Entry<String, Integer>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
	        if (res.length() > 0) {
	        	res = res + ",";
	        }
	        res = res + pairs.getKey() + WebAnnotationFields.SEPARATOR_SEMICOLON + pairs.getValue();
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    if (res.length() > 0) {
	    	res = "[" + res + "]";
	    }
	    return res;
	}	
	
    /**
     * This method converts JSON string to map.
     * @param value The input string
     * @return resulting map
     */
    public static Map<String, String> stringToMap(String value) {
    	String reg = ",";
        Map<String,String> res = new HashMap<String, String>();
        if (!value.isEmpty()) {
			value = value.substring(1, value.length() - 1); // remove braces
	        String[] arrValue = value.split(reg);
	        for (String string : arrValue) {
	            String[] mapPair = string.split(WebAnnotationFields.SEPARATOR_SEMICOLON);
	            res.put(mapPair[0], mapPair[1]);
	    	}
        }
        return res;
    }
    
    /**
     * This method converts JSON string to map<String,Integer>.
     * @param value The input string
     * @return resulting map<String,Integer>
     */
    public static Map<String, Integer> stringToMapExt(String value) {
    	String reg = ",";
        Map<String,Integer> res = new HashMap<String, Integer>();
        if (!value.isEmpty()) {
			value = value.substring(1, value.length() - 1); // remove braces
	        String[] arrValue = value.split(reg);
	        for (String string : arrValue) {
	            String[] mapPair = string.split(WebAnnotationFields.SEPARATOR_SEMICOLON);
	            res.put(mapPair[0], Integer.valueOf(mapPair[1]));
	    	}
        }
        return res;
    }
    
    /**
     * TODO do it in JsonLdParser
     * This method converts a multilingual part of the JsonLd string for Annotation
     * in a multilingual value that is conform for Solr. E.g. 'en' in 'EN_multilingual'
     * @param jsonLdAnnotationStr
     * @return
     */
//    public static String convertMultilingualFromJsonLdToSolrType(String jsonLdAnnotationStr) {
//    	String res = jsonLdAnnotationStr;
//    	
//    	/**
//    	 * Check whether a multilingual part exist. If exist extract this part.
//    	 */
//    	if (jsonLdAnnotationStr.contains(WebAnnotationFields.MULTILINGUAL)) {
//    		if (!jsonLdAnnotationStr.isEmpty()) {
//    			Pattern pattern = Pattern.compile(WebAnnotationFields.MULTILINGUAL + "\":\\s+\"(.*?)]");
//    			Matcher matcher = pattern.matcher(jsonLdAnnotationStr);
//    			if (matcher.find()) {
//    			    String multilingualMapStr = matcher.group(1).replace("[", "");
//    			    String solrMultilingualStr = multilingualMapStr;
//			        String[] tokens = multilingualMapStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//			        for(String t : tokens) {
//				        String[] pair = t.split(WebAnnotationFields.SEPARATOR_SEMICOLON);
//				        
//				        if (SolrAnnotationConst.SolrAnnotationLanguages.contains(pair[0])) {
//				        	solrMultilingualStr = solrMultilingualStr.replace(
//				        			pair[0] + SolrAnnotationConst.DELIMETER
//				        			, SolrAnnotationConst.SolrAnnotationLanguages.getLanguageItemByValue(pair[0]) 
//				        				+ SolrAnnotationConst.UNDERSCORE + SolrAnnotationConst.MULTILINGUAL 
//				        				+ SolrAnnotationConst.DELIMETER);
//				        }
//			        }    			    
//    			    res = jsonLdAnnotationStr.replace(multilingualMapStr, solrMultilingualStr);
//    			}		
//    		}    		
//    	}
//    	
//    	return res;
//    }
    	
    /**
     * This method converts a multilingual value that is conform for Solr
     * in a multilingual part of the JsonLd string for Annotation. E.g. 'EN_multilingual' in 'en'
     * @param jsonLdAnnotationStr
     * @return
     */
//    public static String convertMultilingualFromSolrTypeToJsonLd(String solrAnnotationStr) {
//    	String res = solrAnnotationStr;
//    	
//    	/**
//    	 * Check whether a multilingual part exist. If exist extract this part.
//    	 */
//    	if (solrAnnotationStr.contains(SolrAnnotationConst.MULTILINGUAL)) {
//    		if (!solrAnnotationStr.isEmpty()) {
//    			Pattern pattern = Pattern.compile(SolrAnnotationConst.MULTILINGUAL + "\":\\s+\"(.*?)]");
//    			Matcher matcher = pattern.matcher(solrAnnotationStr);
//    			if (matcher.find()) {
//    			    String multilingualMapStr = matcher.group(1).replace("[", "");
//    			    String solrMultilingualStr = multilingualMapStr;
//			        String[] tokens = multilingualMapStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//			        for(String t : tokens) {
//				        String[] pair = t.split(SolrAnnotationConst.DELIMETER);
//				        String language = pair[0].substring(0,2);
//						if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
//				        	solrMultilingualStr = solrMultilingualStr.replace(
//				        			pair[0]	+ SolrAnnotationConst.DELIMETER
//				        			, language.toLowerCase() + SolrAnnotationConst.DELIMETER);
//				        }
//			        }    			    
//    			    res = solrAnnotationStr.replace(multilingualMapStr, solrMultilingualStr);
//    			}		
//    		}    		
//    	}
//    	
//    	return res;
//    }
    
	public static String extractAnnotationStringFromJsonString(String jsonString) {
		String res = "";
		if (StringUtils.isNotEmpty(jsonString)) {
			Pattern pattern = Pattern.compile(ModelConst.ANNOTATION + "\":(.*?)}}");
			Matcher matcher = pattern.matcher(jsonString);
			if (matcher.find())
			{
			    res = matcher.group(1) + "}";
			}
		}
	return res;
}
    
}
