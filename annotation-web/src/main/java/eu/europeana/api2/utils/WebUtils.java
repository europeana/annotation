package eu.europeana.api2.utils;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author GordeaS
 *
 */
public class WebUtils {
	
	private static final String JSON_GENERATION_EXCEPTION = "Json Generation Exception: ";
  private static final Logger log = LogManager.getLogger(WebUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
		

	public static String toJson(Object object) {
		return toJson(object, false, -1);
	}
		
	public static String toJson(Object object, boolean shortObject, int objectId) {
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		String errorMessage = null;
		try {
			String jsonStr = objectMapper.writeValueAsString(object);	
			if (shortObject) {
				String idBeginStr = "id\":{";
				int startIdPos = jsonStr.indexOf(idBeginStr);
				int endIdPos = jsonStr.indexOf('}', startIdPos);
				jsonStr = jsonStr.substring(0, startIdPos) + idBeginStr.substring(0, idBeginStr.length() - 1) 
				    + Integer.valueOf(objectId) + jsonStr.substring(endIdPos + 1);
			}
			return jsonStr;
		} catch (JsonGenerationException e) {
			log.error(e);
			errorMessage = JSON_GENERATION_EXCEPTION + e.getMessage();
		} catch (JsonMappingException e) {
			log.error(e);
			errorMessage = JSON_GENERATION_EXCEPTION + e.getMessage();
		} catch (IOException e) {
			log.error(e);
			errorMessage = "I/O Exception: " + e.getMessage();
		}
		return errorMessage;
	}	
}
