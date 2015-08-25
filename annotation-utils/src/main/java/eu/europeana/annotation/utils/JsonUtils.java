package eu.europeana.annotation.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
//import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.state.State;
import eu.europeana.annotation.definitions.model.resource.state.impl.BaseState;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.CssStyle;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.utils.serialization.AgentDeserializer;
import eu.europeana.annotation.utils.serialization.AnnotationDeserializer;
import eu.europeana.annotation.utils.serialization.BodyDeserializer;
import eu.europeana.annotation.utils.serialization.ConceptDeserializer;
import eu.europeana.annotation.utils.serialization.InternetResourceDeserializer;
import eu.europeana.annotation.utils.serialization.ListDeserializer;
import eu.europeana.annotation.utils.serialization.MapDeserializer;
import eu.europeana.annotation.utils.serialization.SelectorDeserializer;
import eu.europeana.annotation.utils.serialization.TargetDeserializer;

public class JsonUtils {
	
//	private static Logger log = Logger.getRootLogger();
//	private static final Logger log = Logger.getLogger(JSONUtils.class);
//	private static ObjectMapper objectMapper = new ObjectMapper();
	private static ObjectMapper objectMapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY)
			.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final JsonFactory jsonFactory = new JsonFactory();
	
	private static AnnotationIdHelper idHelper;
	public static AnnotationIdHelper getIdHelper() {
		if (idHelper == null)
			idHelper = new AnnotationIdHelper();
		return idHelper;
	}
	
	
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
			module.addDeserializer(Concept.class, new ConceptDeserializer());  
			module.addDeserializer(Agent.class, new AgentDeserializer());
			module.addDeserializer(Selector.class, new SelectorDeserializer());
			module.addDeserializer(InternetResource.class, new InternetResourceDeserializer());
			//module.addDeserializer(State.class, new StateDeserializer());
			//TODO: needs improvement, otherwise all strings and maps will be converted to String entries
			module.addDeserializer(Map.class, new MapDeserializer());
			module.addDeserializer(List.class, new ListDeserializer());
			
			//module.addDeserializer(Style.class, new StyleDeserializer());  
			module.addAbstractTypeMapping(AnnotationId.class, BaseAnnotationId.class);
			module.addAbstractTypeMapping(Point.class, PointImpl.class); 
			module.addAbstractTypeMapping(Style.class, CssStyle.class); 
			module.addAbstractTypeMapping(State.class, BaseState.class);
//			module.addAbstractTypeMapping(Agent.class, Person.class);
			module.addAbstractTypeMapping(Target.class, ImageTarget.class);

			objectMapper.registerModule(module); 
	
			
			parser.setCodec(objectMapper);
			annotation = objectMapper.readValue(parser, Annotation.class);
			annotation.setInternalType(annotation.getType());
			if (annotation.getTarget() != null) 
				annotation.setTarget((Target) getIdHelper().setResourceIds(annotation.getTarget()));
			if (annotation.getBody() != null) 
				annotation.setBody((Body) getIdHelper().setResourceIds(annotation.getBody()));
		} catch (JsonParseException e) {
			throw new AnnotationInstantiationException("Json formating exception!", e);
		} catch (IOException e) {
			throw new AnnotationInstantiationException("Json reading exception!", e);
		}
		
		return annotation;
	}
	
	public static Concept toConceptObject(String json) {
		JsonParser parser;
		Concept concept = null;
		try {
			parser = jsonFactory.createJsonParser(json);
			SimpleModule module =  
			      new SimpleModule("ConceptDeserializerModule",  
			          new Version(1, 0, 0, null));  
			    
			module.addDeserializer(Concept.class, new ConceptDeserializer());  
//			module.addDeserializer(Map.class, new MapDeserializer());
			module.addDeserializer(List.class, new ListDeserializer());
			
			objectMapper.registerModule(module); 
			
			parser.setCodec(objectMapper);
			concept = objectMapper.readValue(parser, Concept.class);
		} catch (JsonParseException e) {
			throw new AnnotationInstantiationException("Json formating exception!", e);
		} catch (IOException e) {
			throw new AnnotationInstantiationException("Json reading exception!", e);
		}
		
		return concept;
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
     * This method extracts uri part from a concept JSON string.
     * @param jsonString
     * @return
     */
    public static String extractUriFromConceptJson(String jsonString) {
		String res = "";
		if (StringUtils.isNotEmpty(jsonString)) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.URI + "\":(.*?),");
			Matcher matcher = pattern.matcher(jsonString);
			if (matcher.find())
			{
			    res = matcher.group(1).replace(" ","").replace("\"", "");
			}
		}
		return res;    	
    }
    
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
    
	/**
	 * This method extracts value from json string.
	 * @param valueName
	 * @param jsonString
	 * @return value
	 */
	public static String extractValueFromJsonString(String valueName, String jsonString) {
		String res = "";
		if (StringUtils.isNotEmpty(jsonString)) {
			Pattern pattern = Pattern.compile(valueName + "\": *\"(.*?)\",");
			Matcher matcher = pattern.matcher(jsonString);
			if (matcher.find())
			{
			    res = matcher.group(1);
			}
		}
		return res;
	}
    
	public static String extractAnnotationListStringFromJsonString(String jsonString) {
		return extractAnnotationListStringFromJsonString(jsonString, "\":(.*?)}}]");
	}
    
	public static String extractAnnotationListStringFromJsonString(String jsonString, String regex) {
		String res = "";
		if (StringUtils.isNotEmpty(jsonString)) {
			Pattern pattern = Pattern.compile(ModelConst.ITEMS + regex);
			Matcher matcher = pattern.matcher(jsonString);
			if (matcher.find())
			{
			    res = matcher.group(1) + "}}]";
			}
		}
		return res;
	}
    
	/**
	 * This method extracts EuropeanaId from the JsonLd string in order to find out
	 * the collection and object IDs for the REST service URL.
	 * @param annotationJsonLdStr
	 * @return the europeanaId string
	 */
	public static String extractEuropeanaIdFromJsonLdStr(String annotationJsonLdStr) {
		String res = "";
		if (StringUtils.isNotEmpty(annotationJsonLdStr)) {
			Pattern pattern = Pattern.compile(ModelConst.EUROPEANA_ID + "\":\"(.*?)\",");
			Matcher matcher = pattern.matcher(annotationJsonLdStr);
			if (matcher.find())
			{
			    res = matcher.group(1);
			}
		}
		return res;
	}
	
    /**
     * This method converts JSON string to List<String>.
     * @param value The input string
     * @return resulting List<String>
     */
    public static List<String> stringToList(String value) {
    	String reg = ",";
    	value = value.replace(" ", ""); // remove blanks
        List<String> res = new ArrayList<String>();
        if (!value.isEmpty()) {
			value = value.substring(1, value.length() - 1); // remove braces
	        String[] arrValue = value.split(reg);
	        for (String string : arrValue) {
	        	res.add(string);
	    	}
        }
        return res;
    }
    
    
    /**
     * This method extracts identifier from the AnnotationId string. 
     * It is the last token in array.
     * @param annotationId
     * @return identifier
     */
    public static String extractIdentifierFromAnnotationIdString(String annotationId) {
        String[] arrValue = annotationId.split(WebAnnotationFields.SLASH);
        return arrValue[arrValue.length - 1];
    }
	
}
