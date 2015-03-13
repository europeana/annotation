package eu.europeana.annotation.client.model.json;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;
import eu.europeana.annotation.definitions.model.utils.ModelConst;

public class AnnotationDeserializer implements JsonDeserializer<Annotation>{

	Gson gson = null;
	
	@Override
	public Annotation deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		Class<? extends Annotation> concreteClass = null;

		JsonObject jsonObj = json.getAsJsonObject();
		for (Map.Entry<String, JsonElement> attribute : jsonObj.entrySet()) {
			if (ModelConst.TYPE.equals(attribute.getKey())) {
				concreteClass = AnnotationObjectFactory.getInstance().getClassForType((attribute.getValue().getAsString()));
				break;
			}
		}

		return (Annotation) getGson().fromJson(jsonObj, concreteClass);
	}

	private Gson getGson() {
		if(gson == null){
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Point.class, new ShapeDeserializer());
		gsonBuilder.registerTypeAdapter(AnnotationId.class, new AnnotationIdDeserializer());
//		gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());//setDateFormat(ModelConst.GSON_DATE_FORMAT);
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
		//		gsonBuilder.setDateFormat(ModelConst.GSON_DATE_FORMAT);
		gsonBuilder.registerTypeAdapter(Agent.class, new AgentDeserializer());
		gson = gsonBuilder.create();			
		}
		return gson;
	}
		
	public class ShapeDeserializer implements InstanceCreator<Point> {


		@Override
		public Point createInstance(Type type) {
			return new PointImpl();
		}

	}
	
	public class AgentDeserializer implements JsonDeserializer<Agent> {
		
		   public Agent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
				   throws JsonParseException {
//				ObjectMapper mapper = (ObjectMapper) jp.getCodec();
//				ObjectNode root = (ObjectNode) mapper.readTree(jp);
				Class<? extends Agent> realClass = null;
				
//				Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
//				while (elementsIterator.hasNext()) {
//					Entry<String, JsonNode> element = elementsIterator.next();
//					if (ModelConst.AGENT_TYPES.equals(element.getKey())) {
//						String textValue = element.getValue().toString();
//						String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(textValue).replace("\"", "");
//						realClass = AgentObjectFactory.getInstance()
//								.getClassForType(typeValue);
//						break;
//					}
//				}
				
				if (realClass == null)
					return null;
				
				return null; //mapper.readValue(root, realClass);
		   } 
	}

	
	public class JsonDateDeserializer implements JsonDeserializer<Date> {
		
	   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
			   throws JsonParseException {
	      String s = json.getAsJsonPrimitive().getAsString();
	      long l = Long.parseLong(s.substring(6, s.length() - 2));
	      Date d = new Date(l);
	      return d; 
	   } 
	}

	public class AnnotationIdDeserializer implements InstanceCreator<AnnotationId> {


		@Override
		public AnnotationId createInstance(Type type) {
			System.out.println("##### createInstance() type: " + type.toString());
			return new BaseAnnotationId(type.toString());
//			return new GeneratedAnnotationIdImpl(this.createInstance(type).getResourceId()
//					, this.createInstance(type).getAnnotationNr());
		}

	}
	
//	public class AgentDeserializer extends StdDeserializer<Agent> {
//		
//		public AgentDeserializer() {
//			super(Agent.class);			
//		}
//
//		@Override
//		public Agent deserialize(JsonParser jp, DeserializationContext ctxt)
//				throws IOException, JsonProcessingException {
//			
//			ObjectMapper mapper = (ObjectMapper) jp.getCodec();
//			ObjectNode root = (ObjectNode) mapper.readTree(jp);
//			Class<? extends Agent> realClass = null;
//			
//			Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
//			while (elementsIterator.hasNext()) {
//				Entry<String, JsonNode> element = elementsIterator.next();
//				if (ModelConst.AGENT_TYPES.equals(element.getKey())) {
//					String textValue = element.getValue().toString();
//					String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(textValue).replace("\"", "");
//					realClass = AgentObjectFactory.getInstance()
//							.getClassForType(typeValue);
//					break;
//				}
//			}
//			
//			if (realClass == null)
//				return null;
//			
//			return mapper.readValue(root, realClass);
//		}
//		
//		
//	}
	
	
}
