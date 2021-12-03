package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;

public class WebAnnotationAuxilaryMethodsTest extends BaseWebAnnotationTest { 
	
	@Test
	public void getDeletedAnnotations() throws JsonParseException, IOException {						
		//store 2 annotations 
		Annotation anno_tag = createTestAnnotation(TAG_STANDARD, null);
		Annotation anno_subtitle = createTestAnnotation(SUBTITLE_MINIMAL, null);
		
		//delete both annotations
		getApiClient().deleteAnnotation(anno_tag.getAnnotationId().getIdentifier());
		getApiClient().deleteAnnotation(anno_subtitle.getAnnotationId().getIdentifier());
		
		final String pattern = "dd-MM-yyyy";
		String startDate = new SimpleDateFormat(pattern).format(new Date());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1); //getting the tommorow's date
		String stopDate = new SimpleDateFormat(pattern).format(c.getTime());

		List<String> result = getApiClient().getDeleted(null, startDate, stopDate, 0, 100);
		assertTrue(result.size()>0);
	}
			
				
}
