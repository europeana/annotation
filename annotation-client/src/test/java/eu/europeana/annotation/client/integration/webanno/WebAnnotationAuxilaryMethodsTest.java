package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.api.commons.definitions.utils.DateUtils;

public class WebAnnotationAuxilaryMethodsTest extends BaseWebAnnotationTest {

  @Test
  public void getDeletedAnnotations() throws JsonParseException, IOException {
    // store 2 annotations
    Annotation anno_tag = createTestAnnotation(TAG_STANDARD, null);
    Annotation anno_subtitle = createTestAnnotation(SUBTITLE_MINIMAL, null);

    try {

      // delete both annotations
      getApiProtocolClient().deleteAnnotation(anno_tag.getAnnotationId().getIdentifier());
      getApiProtocolClient().deleteAnnotation(anno_subtitle.getAnnotationId().getIdentifier());

      Date now = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(now);
      calendar.add(Calendar.DATE, -1);
      Date startDate = calendar.getTime();
      calendar.add(Calendar.DATE, 2);
      Date stopDate = calendar.getTime();

      List<String> result = getApiAuxilaryMethodsClient().getDeleted(null,
          DateUtils.convertDateToStr(startDate), DateUtils.convertDateToStr(stopDate), 0, 100);

      String baseUri = ClientConfiguration.getInstance().getAnnotationIdBaseUri();
      assertTrue(result.contains(baseUri + "/" + anno_tag.getAnnotationId().getIdentifier()));
      assertTrue(result.contains(baseUri + "/" + anno_subtitle.getAnnotationId().getIdentifier()));
    } finally {
      // TODO: move to afterTest method
      deleteAnnotation(anno_tag);
      deleteAnnotation(anno_subtitle);
    }

  }


}
