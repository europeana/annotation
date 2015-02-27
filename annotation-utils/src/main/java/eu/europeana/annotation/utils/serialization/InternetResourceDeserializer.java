package eu.europeana.annotation.utils.serialization;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;

public class InternetResourceDeserializer extends StdDeserializer<InternetResource> {
	
	public InternetResourceDeserializer() {
		super(InternetResource.class);
		
	}

	@Override
	public InternetResource deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends InternetResource> realClass = null;
		
//		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
//		while (elementsIterator.hasNext()) {
//			Entry<String, JsonNode> element = elementsIterator.next();
//			if (ModelConst.SELECTOR_TYPE.equals(element.getKey())) {
//				String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(element.getValue().getTextValue());
		realClass = BaseInternetResource.class;
//			break;
//			}
//		}
		
//		if (realClass == null)
//			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
