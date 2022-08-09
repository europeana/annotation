package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching textual values in 
 * different body types.
 * 
 * @author GrafR
 *
 */
public class SearchSubtitlesTest extends BaseSearchTest {

	
	@Test
	public void searchSubtitles() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	    
		String requestBody = getJsonStringInput(SUBTITLE_MINIMAL);
		MotivationTypes motivationType = MotivationTypes.SUBTITLING;
		Annotation inputAnno = parseAnnotation(requestBody, motivationType);
		
		// create indexed subtitle
		Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, null);
		addCreatedAnnotation(storedAnno);
		
		assertTrue(BodyInternalTypes.isFullTextResourceTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		//TODO: restore after updating schema
		AnnotationPage annoPage = search(VALUE_SEARCH_BODY_VALUE_IT, SearchProfiles.STANDARD, "1");
		Annotation retrievedAnnotation = (Annotation)annoPage.getAnnotations().get(0);
		assertEquals(storedAnno.getIdentifier(), retrievedAnnotation.getIdentifier());
		validateSubtitle(retrievedAnnotation);
	}

	/**
	 * Validate full text resource tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateSubtitle(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.SUBTITLING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.FULL_TEXT_RESOURCE.name());
		FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
		assertNotNull(textBody.getValue());
		assertTrue(textBody.getValue().contains("con il grande finale"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getSource());
		assertTrue(target.getSource().equals("http://www.euscreen.eu/item.html?id=EUS_D61E8DF003E30114621A92ABDE846AD7"));
		assertNotNull(target.getScope());
		assertTrue(target.getScope().equals(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2051933/data_euscreenXL_EUS_D61E8DF003E30114621A92ABDE846AD7"));
	}
	

}
