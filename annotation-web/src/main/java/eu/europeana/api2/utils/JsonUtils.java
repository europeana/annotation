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

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
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
import eu.europeana.api2.utils.serialization.AgentDeserializer;
import eu.europeana.api2.utils.serialization.AnnotationDeserializer;
import eu.europeana.api2.utils.serialization.BodyDeserializer;
import eu.europeana.api2.utils.serialization.SelectorDeserializer;
import eu.europeana.api2.utils.serialization.TargetDeserializer;
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
			module.addDeserializer(Target.class, new TargetDeserializer());  
			module.addDeserializer(Body.class, new BodyDeserializer());  
			module.addDeserializer(Agent.class, new AgentDeserializer());
			module.addDeserializer(Selector.class, new SelectorDeserializer());
			//module.addDeserializer(State.class, new StateDeserializer());
			module.addDeserializer(Agent.class, new AgentDeserializer());
			
			//module.addDeserializer(Style.class, new StyleDeserializer());  
			module.addAbstractTypeMapping(Point.class, PointImpl.class); 
			module.addAbstractTypeMapping(Style.class, CssStyle.class); 
			module.addAbstractTypeMapping(State.class, BaseState.class);
			
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
}
