package eu.europeana.annotation.client.integration.webanno.linkforcontributing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;

public class LinkForContributingTest extends BaseWebAnnotationTest {



  @Test
  public void createLinkForContributingBodyObject() throws JsonParseException, IOException {
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT);
    validateResponse(response);

    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
    addCreatedAnnotation(storedAnno);
  }

  @Test
  public void createLinkForContributingBodyString() throws JsonParseException, IOException {
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING);
    Annotation storedAnno = validateResponse(response);

    addCreatedAnnotation(storedAnno);
  }

  @Test
  public void createLinkForContributingTargetSpecific() throws JsonParseException, IOException {
    //specific resources not allowed as target
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void createLinkForContributingTargetSpecificWithId() throws JsonParseException, IOException {
    //specific resources not allowed as target
    ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC_ID);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
