package eu.europeana.annotation.client.abstracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import eu.europeana.annotation.client.EuropeanaLdApiImpl;
import eu.europeana.annotation.client.utils.BaseUtils;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;


public class BaseJsonLdApiTest {
	
	public static final String SIMPLE_TAG_ANNOTATION = "/tag/simple-annotation.json";
	public static final String SIMPLE_LINK_ANNOTATION = "/link/simple-annotation.json";
	
    protected EuropeanaLdApiImpl europeanaLdApi;
    protected AnnotationLdParser europeanaParser;
    
    @BeforeEach
    public void initObjects() {
    	europeanaLdApi = new EuropeanaLdApiImpl();
		europeanaParser = new AnnotationLdParser();
    }
 
	/**
	 * This method validates annotation object. It verifies annotationNr, provider and content.
	 * @param annotationNr
	 * @param annotation
	 * @throws JsonParseException
	 */
	protected void validateAnnotation(String provider, long annotationNr, Annotation annotation)
			throws JsonParseException {
		
		assertTrue(annotation.getIdentifier() > 0);
		assertEquals(annotationNr, annotation.getIdentifier());		
	}
	
	/**
	 * This method extracts an items count parameter from a HTML response in search methods.
	 * @param annotationStr
	 * @return
	 */
	protected int extractItemsCount(String annotationStr) {
		int res = 0;
		String itemsCountStr = "";
		if (StringUtils.isNotEmpty(annotationStr)) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.ITEMS_COUNT + "\":(\\d+),");
			Matcher matcher = pattern.matcher(annotationStr);
			if (matcher.find())
			{
				itemsCountStr = matcher.group(1);
				res = Integer.valueOf(itemsCountStr);
			}
		}
		
		return res;
	}
    
	/**
	 * @param annotationStr
	 */
	protected void verifySearchResult(String annotationStr) {
		assertTrue(annotationStr.contains(WebAnnotationFields.SUCCESS_TRUE));
		int itemsCount = extractItemsCount(annotationStr);
		assertTrue(itemsCount > 0);
	}
	
	protected String getJsonStringInput(String resource) throws IOException {
		return (new BaseUtils()).getJsonStringInput(resource);
	}
	
}
