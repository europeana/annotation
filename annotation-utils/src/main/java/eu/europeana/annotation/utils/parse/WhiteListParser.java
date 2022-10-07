package eu.europeana.annotation.utils.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.exception.WhitelistParserException;
import eu.europeana.annotation.definitions.model.whitelist.BaseWhitelistEntry;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.utils.serialization.ListDeserializer;
import eu.europeana.annotation.utils.serialization.WhitelistDeserializer;

public class WhiteListParser extends BaseJsonParser{

	public static List<WhitelistEntry> toWhitelist(String pathToJsonFile) {
		List<WhitelistEntry> res = new ArrayList<WhitelistEntry>();
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(new File(pathToJsonFile));
			List<JsonNode> rootList = rootNode.findValues("whitelist");
			JsonNode entriesJsonNode = rootList.get(0);
			for (JsonNode jsonNode : entriesJsonNode) {
				WhitelistEntry whitelistEntry = mapper.readValue(jsonNode, BaseWhitelistEntry.class);
				res.add(whitelistEntry);
			}
		} catch (JsonProcessingException e) {
			throw new WhitelistParserException(e);
		} catch (IOException e) {
			throw new WhitelistParserException("Cannot access the json file: " + pathToJsonFile,
					e);
		}
		return res;
	}
	
	
	public static WhitelistEntry toWhitelistEntry(String json) {
		JsonParser parser;
		WhitelistEntry whitelist = null;
		try {
			parser = jsonFactory.createJsonParser(json);
			SimpleModule module =  
			      new SimpleModule("WhitelistDeserializerModule",  
			          new Version(1, 0, 0, null));  
			    
			module.addDeserializer(WhitelistEntry.class, new WhitelistDeserializer());  
			module.addDeserializer(List.class, new ListDeserializer());
			
			objectMapper.registerModule(module); 
			
			parser.setCodec(objectMapper);
			whitelist = objectMapper.readValue(parser, WhitelistEntry.class);
		} catch (JsonParseException e) {
			throw new AnnotationInstantiationException("Json formating exception!", e);
		} catch (IOException e) {
			throw new AnnotationInstantiationException("Json reading exception!", e);
		}
		
		return whitelist;
	}
	
}
