package eu.europeana.api2.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.web.servlet.ModelAndView;

import com.github.jsonldjava.utils.JSONUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.shape.Point;
import eu.europeana.annotation.definitions.model.shape.impl.PointImpl;
import eu.europeana.corelib.logging.Logger;

public class JsonUtils {
	
	private static final Logger log = Logger.getLogger(JSONUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static final JsonFactory jsonFactory = new JsonFactory();
	
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
			module.addAbstractTypeMapping(Point.class, PointImpl.class); 
			
			objectMapper.registerModule(module); 
			
//			SimpleModule shapeModule =  
//				      new SimpleModule("ShapeDeserializerModule",  
//				          new Version(1, 0, 0, null));  
//				
//			
//			addDeserializer(Annotation.class, new AnnotationDeserializer());  
//			objectMapper.registerModule(module); 
			
			
			parser.setCodec(objectMapper);
			annotation = objectMapper.readValue(parser, Annotation.class);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return annotation;
	}
}
