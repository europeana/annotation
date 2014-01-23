package eu.europeana.api2.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;

public class AnnotationDeserializer extends StdDeserializer<Annotation> {
	
	AnnotationDeserializer() {
		super(Annotation.class);
	}

	@Override
	public Annotation deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends Annotation> realClass = null;
		
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
			if ("type".equals(element.getKey())) {
				realClass = AnnotationObjectFactory.getInstance()
						.getAnnotationClass(element.getValue().getTextValue());
				break;
			}
		}
		
		if (realClass == null)
			return null;
		return mapper.readValue(root, realClass);
	}
}
