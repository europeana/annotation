package eu.europeana.annotation.tests.webanno;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.api.commons.definitions.utils.DateUtils;

public class WebAnnotationAuxilaryMethodsTest extends AbstractIntegrationTest {

  @Test
  public void getDeletedAnnotations() throws Exception {
    // store 2 annotations
    Annotation anno_tag = createTestAnnotation(TAG_STANDARD, true, null);
    createdAnnotations.add(anno_tag.getIdentifier());
    Annotation anno_subtitle = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
    createdAnnotations.add(anno_subtitle.getIdentifier());

    // delete both annotations
    deleteAnnotation(anno_tag.getIdentifier());
    deleteAnnotation(anno_subtitle.getIdentifier());

    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);
    calendar.add(Calendar.DATE, -1);
    Date startDate = calendar.getTime();
    calendar.add(Calendar.DATE, 2);
    Date stopDate = calendar.getTime();

    List<String> result = getDeleted(null, DateUtils.convertDateToStr(startDate), DateUtils.convertDateToStr(stopDate), 0, 100);
    assertTrue(result.contains(String.valueOf(anno_tag.getIdentifier())));
    assertTrue(result.contains(String.valueOf(anno_subtitle.getIdentifier())));
  }


}
