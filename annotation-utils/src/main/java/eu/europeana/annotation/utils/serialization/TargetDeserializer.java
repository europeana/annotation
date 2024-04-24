package eu.europeana.annotation.utils.serialization;

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

import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;

public class TargetDeserializer extends StdDeserializer<Target> {
	
	public TargetDeserializer() {
		super(Target.class);
		
	}

	@Override
	public Target deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends Target> realClass = null;
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
			if (ModelConst.TARGET_TYPE.equals(element.getKey())) {
				String typeValue = TypeUtils.getInternalTypeFromTypeArrayStatic(element.getValue().getTextValue());
				realClass = TargetObjectFactory.getInstance()
						.getClassForType(typeValue);
				break;
			}
		}
		
		if (realClass == null)
			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
