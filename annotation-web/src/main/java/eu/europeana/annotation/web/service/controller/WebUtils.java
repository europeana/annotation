package eu.europeana.annotation.web.service.controller;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europeana.api.commons.web.exception.InternalServerException;

/**
 * @author GordeaS
 *
 */
public class WebUtils {


  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Object object) throws InternalServerException {
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    try {
      return objectMapper.writeValueAsString(object);
    } catch (IOException e) {
      throw new InternalServerException(e); 
    }
  }
}
