package eu.europeana.api2.utils.serialization;

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

import eu.europeana.annotation.definitions.model.factory.impl.SelectorObjectFactory;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;

public class SelectorDeserializer extends StdDeserializer<Selector> {
	
	public SelectorDeserializer() {
		super(Selector.class);
		
	}

	@Override
	public Selector deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends Selector> realClass = null;
		
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
			if ("selectorType".equals(element.getKey())) {
				realClass = SelectorObjectFactory.getInstance()
						.getClassForType(element.getValue().getTextValue());
				break;
			}
		}
		
		if (realClass == null)
			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
