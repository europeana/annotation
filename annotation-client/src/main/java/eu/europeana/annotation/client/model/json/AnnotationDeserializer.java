package eu.europeana.annotation.client.model.json;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;

public class AnnotationDeserializer implements JsonDeserializer<Annotation> {

	Gson gson = new GsonBuilder().create();

	@Override
	public Annotation deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		Class<? extends Annotation> concreteClass = null;

		JsonObject jsonObj = json.getAsJsonObject();
		for (Map.Entry<String, JsonElement> attribute : jsonObj.entrySet()) {
			if ("type".equals(attribute.getKey())) {
				concreteClass = AnnotationObjectFactory.getInstance()
						.getAnnotationClass(attribute.getValue().getAsString());
				break;
			}
		}

		return (Annotation) gson.fromJson(jsonObj, concreteClass);
	}

}
