package eu.europeana.annotation.utils.serialization;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import eu.europeana.annotation.definitions.model.whitelist.BaseWhitelistEntry;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;

public class WhitelistDeserializer extends StdDeserializer<WhitelistEntry> {
	
	public WhitelistDeserializer() {
		this(BaseWhitelistEntry.class);
		
	}

	public WhitelistDeserializer(Class<? extends WhitelistEntry> clazz) {
		super(clazz);
		
	}
	
	@Override
	public WhitelistEntry deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
//		Class<? extends WhitelistEntry> realClass = null;
//		realClass = BaseWhitelistEntry.class;
		
		return (WhitelistEntry) mapper.readValue(root, getValueClass());
	}
	
	
}
