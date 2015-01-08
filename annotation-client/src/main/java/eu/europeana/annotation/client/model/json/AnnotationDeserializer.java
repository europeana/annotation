package eu.europeana.annotation.client.model.json;

import java.lang.reflect.Type;
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
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;

public class AnnotationDeserializer implements JsonDeserializer<Annotation>{

	Gson gson = null;
	
	@Override
	public Annotation deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		Class<? extends Annotation> concreteClass = null;

		JsonObject jsonObj = json.getAsJsonObject();
		for (Map.Entry<String, JsonElement> attribute : jsonObj.entrySet()) {
			if ("type".equals(attribute.getKey())) {
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
}
